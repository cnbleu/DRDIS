
package com.hedymed.engineer;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.hedymed.drdissys.MainActivity;
import com.hedymed.drdissys.R;


public class EngineerActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		if (hasHeaders())
		{
//			Button videoButton = new Button(this);
//			videoButton.setText(R.string.video_play_group_title);
//			videoButton.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
//				    intent.setType("video/*");  
//				    intent.addCategory(Intent.CATEGORY_OPENABLE);  
//				    try {  
//				        startActivityForResult(Intent.createChooser(intent, null), 2);  
//				    } 
//				    catch (android.content.ActivityNotFoundException ex) {  
//				        Toast.makeText(EngineerActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT).show();  
//				    }  
//				}
//			});
			
			Button button = new Button(this);
			button.setText(R.string.engineering_return_button_text);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(EngineerActivity.this, MainActivity.class);
					startActivity(intent);
				}
			});
			// 将该按钮添加到该界面上
//			setListFooter(videoButton);
			setListFooter(button);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {  
			Uri uri = data.getData();  
		    Intent intent = new Intent(Intent.ACTION_VIEW);
		    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    intent.setDataAndType(uri, "video/*");
		    startActivity(intent);
		}  
		
		super.onActivityResult(requestCode, resultCode, data);  
	}
	
	
	// 重写该该方法，负责加载页面布局文件
	@Override
	public void onBuildHeaders(List<Header> target)
	{
		// 加载选项设置列表的布局文件
		loadHeadersFromResource(R.xml.preference_headers, target);
	}

	public static class Prefs1Fragment extends PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}
	}

	
}
