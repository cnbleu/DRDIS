package com.hedymed.drdissys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class disPictureActivity extends Activity {
	private ImageView mFpdImage;
	private Bitmap mbitmap;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.picture_activity_layout);
		
		mFpdImage = (ImageView)findViewById(R.id.fpd_picture);
		Parcelable parcelable = getIntent().getParcelableExtra("netRecvBitmap");
		
		if(parcelable instanceof Bitmap) {
			mbitmap = (Bitmap)parcelable;
			mFpdImage.setImageBitmap(mbitmap);
		}
		
		mFpdImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mbitmap != null && !mbitmap.isRecycled())
					mbitmap.recycle();
				
				Intent intent = new Intent().setClassName(disPictureActivity.this, "com.hedymed.drdissys.MainActivity");
				startActivity(intent);
				
			}
		});
	}
}
