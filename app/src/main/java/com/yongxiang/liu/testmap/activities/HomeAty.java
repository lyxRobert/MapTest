package com.yongxiang.liu.testmap.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.yongxiang.liu.testmap.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class HomeAty extends Activity implements OnClickListener{
	private Button btn_img_marker;
	private Button btn_marker_fixed;
	private Button btn_point_aggregation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initView();
		initEvent();
	}
	private void initEvent() {
		btn_img_marker.setOnClickListener(this);
		btn_marker_fixed.setOnClickListener(this);
		btn_point_aggregation.setOnClickListener(this);
	}
	private void initView() {
		btn_img_marker = (Button) findViewById(R.id.btn_img_marker);
		btn_marker_fixed = (Button) findViewById(R.id.btn_marker_fixed);
		btn_point_aggregation = (Button) findViewById(R.id.btn_point_aggregation);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_img_marker:
			intent(ImageMarkerAty.class);
			break;
		case R.id.btn_marker_fixed:
			intent(MarkerFixedAty.class);
			break;
		case R.id.btn_point_aggregation:
			intent(PointAggregationAty.class);
			break;

		}
	}
	public <T> void intent(Class<T> cls){
		Intent intent = new Intent(HomeAty.this,cls);
		startActivity(intent);
	}
}
