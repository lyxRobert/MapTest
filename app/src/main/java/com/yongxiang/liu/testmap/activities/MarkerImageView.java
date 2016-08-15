package com.yongxiang.liu.testmap.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.yongxiang.liu.testmap.R;

public class MarkerImageView {
	//上下文
	private Context context;
	//marker类
	private MarkerOptions options;
	//当前可观区域里的 聚合过之后的集合
	private ArrayList<MarkerOptions> includeMarkers;
	// 创建区域
	private LatLngBounds bounds;

	/**
	 * 头像加载完监听
	 */
	public MarkerImageView(Context context, MarkerOptions firstMarkers,
						   Projection projection, int gridSize) {
		this.context = context;
		options = new MarkerOptions();
		
		Point point = projection.toScreenLocation(firstMarkers.getPosition());
		//范围类
		Point southwestPoint = new Point(point.x - gridSize, point.y + gridSize);
		//范围类
		Point northeastPoint = new Point(point.x + gridSize, point.y - gridSize);
		
		bounds = new LatLngBounds(projection.fromScreenLocation(southwestPoint),
				projection.fromScreenLocation(northeastPoint));
		//设置初始化marker属性
		options.anchor(0.5f, 0.5f).title(firstMarkers.getTitle()).position(firstMarkers.getPosition())
				.icon(firstMarkers.getIcon())
				.snippet(firstMarkers.getSnippet());
		includeMarkers = new ArrayList<MarkerOptions>();
		includeMarkers.add(firstMarkers);
	}

	public MarkerImageView(Context context) {
		this.context = context;
	}

	public LatLngBounds getBounds() {
		return bounds;
	}

	public MarkerOptions getOptions() {
		return options;
	}

	public void setOptions(MarkerOptions options) {
		this.options = options;
	}

	/**
	 * 添加marker
	 */
	public void addMarker(MarkerOptions markerOptions) {
		includeMarkers.add(markerOptions);// 添加到列表中
	}

	/**
	 * 设置聚合点的中心位置以及图标
	 */
	public void setpositionAndIcon() {
		int size = includeMarkers.size();
		double lat = 0.0;
		double lng = 0.0;
		// 一个的时候
		if (size == 1) {//设置marker单个属性
			// 设置marker位置
			options.position(new LatLng(
					includeMarkers.get(0).getPosition().latitude,
					includeMarkers.get(0).getPosition().longitude));
			options.title("聚合点");
			options.icon(BitmapDescriptorFactory
					.fromBitmap(getViewBitmap(getView(size))));

		} else {// 聚合的时候
			//设置marker聚合属性
			for (MarkerOptions op : includeMarkers) {
				lat += op.getPosition().latitude;
				lng += op.getPosition().longitude;
			}
			// 设置marker的位置为中心位置为聚集点的平均位置
			options.position(new LatLng(lat / size, lng / size));
			options.title("聚合点");
			options.icon(BitmapDescriptorFactory
					.fromBitmap(getViewBitmap(getView(size))));
		}
	}

	/**
	 * marker试图
	 */
	public View getView(int num) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_gaode_img, null);
		/** 数量 */
		TextView txt_num = (TextView) view
				.findViewById(R.id.view_gaode_txt_num);
		/** 头像 */
		ImageView img_portrait = (ImageView) view
				.findViewById(R.id.view_gaode_img_portrait);

		img_portrait.setPadding(8, 8, 8, 12);
		if (num > 1) {
			txt_num.setText(num + "");
		} else if (num == 1) {
			txt_num.setText(num + "");
			img_portrait.setBackgroundResource(R.drawable.green_pin);
		}
		return view;
	}

	/**
	 * 把一个view转化成bitmap对象
	 */
	public static Bitmap getViewBitmap(View view) {
		Bitmap bitmap = null;
		try {
			if (view != null) {
				view.measure(
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				view.layout(0, 0, view.getMeasuredWidth(),
						view.getMeasuredHeight());
				view.buildDrawingCache();
				bitmap = view.getDrawingCache();
			}
		} catch (Exception e) {
		}

		return bitmap;
	}
}