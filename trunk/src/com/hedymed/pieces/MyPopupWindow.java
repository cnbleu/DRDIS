package com.hedymed.pieces;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.hedymed.drdissys.MainActivity;
import com.hedymed.drdissys.R;

public class MyPopupWindow {
	private PopupWindow mPopupWindow;
	private Context mContext;
	
	public MyPopupWindow(Context context) {
		mContext = context;
		View root = View.inflate(context, R.layout.popup_window, null);
		mPopupWindow = new PopupWindow(root, 280, 360, true);
	}
	
	public void show() {
		mPopupWindow.showAtLocation(((MainActivity)mContext).findViewById(R.id.body_picture), Gravity.CENTER, 0, 0);
	}
	
	public void dismiss() {
		mPopupWindow.dismiss();
	}
}
