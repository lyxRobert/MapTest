package com.yongxiang.liu.testmap.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yongxiang.liu.testmap.R;
import com.yongxiang.liu.testmap.activities.RouteActivity;

public class InfoPopupWindow implements View.OnClickListener {
	private Context context;
	private PopupWindow popupWindow;
	private ImageView img_pop;
	private String imgUrl;

	public InfoPopupWindow(final Context context, String imgUrl) {
		this.context = context;
		this.imgUrl = imgUrl;
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_map_popup_window, null);
		img_pop = (ImageView) view.findViewById(R.id.img_pop);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(context));
		imageLoader.displayImage(imgUrl, img_pop);
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		popupWindow.setBackgroundDrawable(new BitmapDrawable(context
				.getResources()));
		img_pop.setOnClickListener(this);
	}

	// 下拉式 弹出 pop菜单 parent 右下角
	public void showAsDropDown(View parent) {
		// 保证尺寸是根据屏幕像素密度来的
		popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		// 使其聚集
		popupWindow.setFocusable(false);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(false);
		// 设置动画
		popupWindow.setAnimationStyle(R.style.PopupWindowAnimStyle);
		// 刷新状态
		popupWindow.update();
	}

	public void setDismissListener(OnDismissListener onDismissListener) {
		popupWindow.setOnDismissListener(onDismissListener);
	}

	// 隐藏菜单
	public void dismiss() {
		popupWindow.dismiss();
	}

	// 是否显示
	public boolean isShowing() {
		return popupWindow.isShowing();
	}

	@Override
	public void onClick(View v) {
		if (v == img_pop) {
			Intent intent = new Intent(context, RouteActivity.class);
			context.startActivity(intent);
		}
	}
}
