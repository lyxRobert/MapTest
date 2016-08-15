package com.yongxiang.liu.testmap.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.overlay.BusRouteOverlay;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.yongxiang.liu.testmap.R;

/**
 * 路径规划
 * 
 * @author Administrator
 *
 */
public class RouteActivity extends Activity implements OnClickListener, OnRouteSearchListener {

	private AMap aMap;
	private MapView mapView;

	/**
	 * 公交按钮，驾车按钮，步行按钮
	 */
	private ImageView img_transit, img_driving, img_walk;

	private int busMode = RouteSearch.BusDefault;// 公交默认模式
	private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
	private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
	private RouteSearch routeSearch;

	private BusRouteResult busRouteResult;// 公交模式查询结果
	private DriveRouteResult driveRouteResult;// 驾车模式查询结果
	private WalkRouteResult walkRouteResult;// 步行模式查询结果

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route);
		mapView = (MapView) findViewById(R.id.route_map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		img_transit = (ImageView) findViewById(R.id.img_transit);
		img_driving = (ImageView) findViewById(R.id.img_driving);
		img_walk = (ImageView) findViewById(R.id.img_walk);

		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}

		img_transit.setOnClickListener(this);
		img_driving.setOnClickListener(this);
		img_walk.setOnClickListener(this);

		// 设置地图可视缩放大小
		aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onClick(View v) {

		// 这里是写死的两个位置
		LatLonPoint startPoint = new LatLonPoint(31.383755, 118.438321);
		LatLonPoint endPoint = new LatLonPoint(31.339746, 118.381727);

		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
		if (v == img_transit) {// 公交
			BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode, "芜湖市", 1);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (v == img_driving) {// 驾车
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode, null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (v == img_walk) {// 步行
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
			routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
				busRouteResult = result;
				BusPath busPath = busRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap, busPath, busRouteResult.getStartPos(), busRouteResult.getTargetPos());
				routeOverlay.removeFromMap();
				routeOverlay.addToMap();
				routeOverlay.zoomToSpan();
			} else {
				showToast("对不起，没有搜索到相关数据！");
			}
		} else if (rCode == 27) {
			showToast("搜索失败,请检查网络连接！");
		} else if (rCode == 32) {
			showToast("key验证无效！");
		} else {
			showToast("未知错误，请稍后重试!错误码为" + rCode);
		}
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(this, aMap, drivePath, driveRouteResult.getStartPos(), driveRouteResult.getTargetPos());
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
			} else {
				showToast("对不起，没有搜索到相关数据！");
			}
		} else if (rCode == 27) {
			showToast("搜索失败,请检查网络连接！");
		} else if (rCode == 32) {
			showToast("key验证无效！");
		} else {
			showToast("未知错误，请稍后重试!错误码为" + rCode);
		}
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
				walkRouteResult = result;
				WalkPath walkPath = walkRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this, aMap, walkPath, walkRouteResult.getStartPos(), walkRouteResult.getTargetPos());
				walkRouteOverlay.removeFromMap();
				walkRouteOverlay.addToMap();
				walkRouteOverlay.zoomToSpan();
			} else {
				showToast("对不起，没有搜索到相关数据！");
			}
		} else if (rCode == 27) {
			showToast("搜索失败,请检查网络连接！");
		} else if (rCode == 32) {
			showToast("key验证无效！");
		} else {
			showToast("未知错误，请稍后重试!错误码为" + rCode);
		}
	}

	/**
	 * toast封装
	 * 
	 * @param str
	 */
	private void showToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

}
