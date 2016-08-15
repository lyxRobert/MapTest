package com.yongxiang.liu.testmap.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.yongxiang.liu.testmap.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yongxiang.liu.testmap.custom.InfoPopupWindow;
import com.yongxiang.liu.testmap.custom.RoundImageView;

public class ImageMarkerAty extends Activity implements OnMarkerClickListener{
	/**
	 * 基础地图
	 */
	private MapView mapView;
	private AMap aMap;
	/**
	 * 点击标记物弹出popWindow信息
	 */
	private InfoPopupWindow popWindow;
	/**
	 * 展示popWindow布局
	 */
	private RelativeLayout mpop;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_marker);
		mapView = (MapView) findViewById(R.id.map_image_marker);
		mpop = (RelativeLayout) findViewById(R.id.image_marker_pop);
		mapView.onCreate(savedInstanceState);// 必须要写
		initView();
		initEvent();
	}

	private void initEvent() {
		aMap.setOnMarkerClickListener(this);
	}

	final List<LatLng> positionList = new ArrayList<LatLng>();

	/**
	 * 初始化AMap对象
	 */
	private void initView() {
		if (aMap == null) {

			aMap = mapView.getMap();
			final MarkerOptions mark = new MarkerOptions();
			// mark.position(x);
			mark.title("我现在的位置:");
			
			mark.draggable(true);
			List<String> urlList = new ArrayList<String>();
			urlList.add("http://img4.imgtn.bdimg.com/it/u=4110197539,3083480809&fm=23&gp=0.jpg");
			urlList.add("http://img3.imgtn.bdimg.com/it/u=4025385378,768804433&fm=23&gp=0.jpg");
			urlList.add("http://img1.imgtn.bdimg.com/it/u=3424257611,4083333498&fm=23&gp=0.jpg");
			urlList.add("http://img0.imgtn.bdimg.com/it/u=515704445,2803782697&fm=23&gp=0.jpg");

			positionList.add(new LatLng(39.908127, 116.375257));
			positionList.add(new LatLng(39.918127, 116.385257));
			positionList.add(new LatLng(39.938127, 116.415257));
			positionList.add(new LatLng(39.948127, 116.425257));
			final ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration.createDefault(this));
			for (int i = 0; i < urlList.size(); i++) {
				final LatLng l = positionList.get(i);
				ImageLoader.getInstance().loadImage(urlList.get(i),
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingComplete(String imageUri,
									View view2, Bitmap loadedImage) {
								super.onLoadingComplete(imageUri, view2,
										loadedImage);

								LatLng latLng = new LatLng(l.latitude,
										l.longitude);
								View view = LayoutInflater.from(
										ImageMarkerAty.this).inflate(
										R.layout.marker, null);
								RoundImageView imageView = (RoundImageView) view
										.findViewById(R.id.iv);
								imageView.setImageBitmap(loadedImage);
								mark.position(latLng);
								mark.icon(BitmapDescriptorFactory
										.fromView(view));
								aMap.addMarker(mark);

							}
						});

			}
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (popWindow != null) {// 先把原来的给隐藏起来
			popWindow.dismiss();
		}
		List<String> imgUrlList = new ArrayList<String>();
		imgUrlList.add("http://img0.imgtn.bdimg.com/it/u=2491477770,1039059834&fm=23&gp=0.jpg");
		imgUrlList.add("http://img0.imgtn.bdimg.com/it/u=3970267017,4100156947&fm=23&gp=0.jpg");
		imgUrlList.add("http://img2.imgtn.bdimg.com/it/u=583474607,2116653117&fm=23&gp=0.jpg");
		imgUrlList.add("http://img5.imgtn.bdimg.com/it/u=3178625138,1197223927&fm=23&gp=0.jpg");
		String markerId = marker.getId();
		markerId = (String) markerId.subSequence(markerId.length()-1, markerId.length());
		int id = Integer.valueOf(markerId);
				popWindow = new InfoPopupWindow(this, imgUrlList.get(id-1));
		popWindow.showAsDropDown(mpop);
		return false;
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
		if (popWindow != null) {// 隐藏popwindow
			popWindow.dismiss();
		}
	}

}
