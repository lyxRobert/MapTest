package com.yongxiang.liu.testmap.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.yongxiang.liu.testmap.R;
import com.yongxiang.liu.testmap.utils.ScreenUtils;

public class MarkerFixedAty extends Activity implements AMapLocationListener,
		AMap.OnCameraChangeListener, View.OnClickListener, LocationSource,
		OnGeocodeSearchListener {
	private MapView mapView;
	private AMap aMap;
	private LocationManagerProxy mLocationManagerProxy;
	private Handler handler = new Handler();
	private OnLocationChangedListener listener;
	private LatLng myLocation = null;
	private Marker centerMarker;
	private boolean isMovingMarker = false;
	private BitmapDescriptor movingDescriptor, chooseDescripter,
			successDescripter;
	private ValueAnimator animator = null;
	private GeocodeSearch geocodeSearch;
	private FrameLayout containerLayout;
	private TextView tv_center;
	private PopupWindow popupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_marker_fixed);
		mapView = (MapView) findViewById(R.id.map_marker_fixed);
		mapView.onCreate(savedInstanceState);
		initUI();
		initAmap();
		setUpLocationStyle();
	}

	private void initUI() {
		findViewById(R.id.myLocation).setOnClickListener(this);
		containerLayout = (FrameLayout) findViewById(R.id.container);
		tv_center = (TextView) findViewById(R.id.tv_center);
	}

	private void initAmap() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.setLocationSource(this);// 设置定位监听
		aMap.setMyLocationEnabled(true);
		aMap.getUiSettings().setZoomControlsEnabled(false);

		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
		CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(15);
		aMap.moveCamera(cameraUpdate);

		movingDescriptor = BitmapDescriptorFactory
				.fromResource(R.drawable.location_succeed);
		chooseDescripter = BitmapDescriptorFactory
				.fromResource(R.drawable.loaction_moving);
		successDescripter = BitmapDescriptorFactory
				.fromResource(R.drawable.location_succeed);

		geocodeSearch = new GeocodeSearch(this);
		geocodeSearch.setOnGeocodeSearchListener(this);
	}

	private void setUpLocationStyle() {
		// 自定义系统定位蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_now));
		myLocationStyle.strokeWidth(0);
		myLocationStyle.radiusFillColor(Color.TRANSPARENT);
		aMap.setMyLocationStyle(myLocationStyle);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		mapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
		if (aMapLocation != null
				&& aMapLocation.getAMapException().getErrorCode() == 0) {
			if (listener != null) {
				listener.onLocationChanged(aMapLocation);// 显示系统小蓝点
			}
			myLocation = new LatLng(aMapLocation.getLatitude(),
					aMapLocation.getLongitude());
			fixedMarker();
		}
	}

	private void fixedMarker() {
		MarkerOptions centerMarkerOption = new MarkerOptions().position(
				myLocation).icon(chooseDescripter);
		centerMarker = aMap.addMarker(centerMarkerOption);
		centerMarker.setPositionByPixels(mapView.getWidth() / 2,
				mapView.getHeight() / 2);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				CameraUpdate update = CameraUpdateFactory.zoomTo(17f);
				aMap.animateCamera(update, 1000, new AMap.CancelableCallback() {
					@Override
					public void onFinish() {
						aMap.setOnCameraChangeListener(MarkerFixedAty.this);
					}

					@Override
					public void onCancel() {
					}
				});
			}
		}, 1000);
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void activate(OnLocationChangedListener onLocationChangedListener) {
		listener = onLocationChangedListener;
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, -1, 100, this);
	}

	public void deactivate() {
		if (mLocationManagerProxy != null) {
			mLocationManagerProxy.removeUpdates(this);
			mLocationManagerProxy.destroy();
		}
		mLocationManagerProxy = null;
	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		if (centerMarker != null) {
			setMovingMarker();
		}
	}

	@Override
	public void onCameraChangeFinish(CameraPosition cameraPosition) {
		LatLonPoint point = new LatLonPoint(cameraPosition.target.latitude,
				cameraPosition.target.longitude);
		RegeocodeQuery query = new RegeocodeQuery(point, 50, GeocodeSearch.AMAP);
		geocodeSearch.getFromLocationAsyn(query);
		if (centerMarker != null) {
			animMarker();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.myLocation:
			CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
			aMap.animateCamera(update);
			break;
		}
	}

	private void setMovingMarker() {
		if (isMovingMarker)
			return;

		isMovingMarker = true;
		centerMarker.setIcon(movingDescriptor);
	}

	@SuppressLint("NewApi")
	private void animMarker() {
		isMovingMarker = false;
		if (animator != null) {
			animator.start();
			return;
		}
		animator = ValueAnimator.ofFloat(mapView.getHeight() / 2,
				mapView.getHeight() / 2 - 30);
		animator.setInterpolator(new DecelerateInterpolator());
		animator.setDuration(150);
		animator.setRepeatCount(1);
		animator.setRepeatMode(ValueAnimator.REVERSE);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Float value = (Float) animation.getAnimatedValue();
				centerMarker.setPositionByPixels(mapView.getWidth() / 2,
						Math.round(value));
			}
		});
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				centerMarker.setIcon(chooseDescripter);
			}
		});
		animator.start();
	}

	@SuppressLint("NewApi")
	private void endAnim() {
		if (animator != null && animator.isRunning())
			animator.end();
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
		if (i == 0) {
			if (regeocodeResult != null
					&& regeocodeResult.getRegeocodeAddress() != null) {
				endAnim();
				centerMarker.setIcon(successDescripter);
				RegeocodeAddress regeocodeAddress = regeocodeResult
						.getRegeocodeAddress();
				String formatAddress = regeocodeResult.getRegeocodeAddress()
						.getFormatAddress();
				String shortAdd = formatAddress
						.replace(regeocodeAddress.getProvince(), "")
						.replace(regeocodeAddress.getCity(), "")
						.replace(regeocodeAddress.getDistrict(), "");
				showPopupWindow(shortAdd);
			} else {
			}
		} else {
		}
	}

	public void showPopupWindow(String content) {
		View popupWindow_view = getLayoutInflater().inflate(
				R.layout.pop_scroll, null, false);
		TextView tv_pop_title = (TextView) popupWindow_view
				.findViewById(R.id.tv_pop_title);
		int screenWidth = ScreenUtils.getScreenWidth(this);
		popupWindow = new PopupWindow(popupWindow_view,
				(int) (screenWidth * 0.6), LayoutParams.WRAP_CONTENT, true);
		popupWindow.setTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 设置动画效果
		// popupWindow.setAnimationStyle(R.style.PopupWindowAnimStyle);
		popupWindow.showAtLocation(tv_center, Gravity.TOP, 0,
				tv_center.getTop()-165);
		tv_pop_title.setText("我的位置:" + content);
	}
	@Override
	public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
	}

}
