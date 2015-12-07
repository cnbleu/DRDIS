
package com.hedymed.engineer;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;

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
			Button button = new Button(this);
			button.setText(R.string.engineering_return_button_text);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(EngineerActivity.this, MainActivity.class);
					startActivity(intent);
				}
			});
			// ���ð�ť��ӵ��ý�����
			setListFooter(button);
		}
	}
	
	
	// ��д�ø÷������������ҳ�沼���ļ�
	@Override
	public void onBuildHeaders(List<Header> target)
	{
		// ����ѡ�������б�Ĳ����ļ�
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
