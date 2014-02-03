package com.jb2kred.doublewake;

import java.io.DataOutputStream;
import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

public class MainActivity extends Activity {

	Process p;

	public void RunAsRoot(String[] cmds) throws IOException{
		Process p = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(p.getOutputStream());            
		for (String tmpCmd : cmds) {
			os.writeBytes(tmpCmd+"\n");
		}           
		os.writeBytes("exit\n");  
		os.flush();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String ch = "";


		final RadioButton c = (RadioButton) findViewById(R.id.radioButton1);
		final RadioButton d = (RadioButton) findViewById(R.id.radioButton2);

		SharedPreferences settings = getSharedPreferences("UserInfo", 0);
		ch = settings.getString("set", "").toString();

		if(ch.contains("enabled"))
		{
			c.setChecked(true);
			String[] commands = {"mount -o rw,remount /system","sed -i 's/insmod.*/insmod \\/system\\/lib\\/modules\\/atmel_mxt_ts.ko dt2w_switch=1/' /system/bin/atmel_touch.sh","echo -n 1 > /sys/module/atmel_mxt_ts/parameters/dt2w_switch","mount -o ro,remount /system"};
			try {
				//	SharedPreferences settings = getSharedPreferences("UserInfo", 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("set","enabled");
				editor.commit();
				RunAsRoot(commands);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			d.setChecked(true);
			String[] commands = {"mount -o rw,remount /system","sed -i 's/insmod.*/insmod \\/system\\/lib\\/modules\\/atmel_mxt_ts.ko dt2w_switch=0/' /system/bin/atmel_touch.sh","echo -n 0 > /sys/module/atmel_mxt_ts/parameters/dt2w_switch","mount -o ro,remount /system"};
			try {
				//	SharedPreferences settings = getSharedPreferences("UserInfo", 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("set","disabled");
				editor.commit();
				RunAsRoot(commands);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				d.setChecked(false);

				try {
					SharedPreferences settings = getSharedPreferences("UserInfo", 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("set","enabled");
					editor.commit();
					String[] commands = {"mount -o rw,remount /system","sed -i 's/insmod.*/insmod \\/system\\/lib\\/modules\\/atmel_mxt_ts.ko dt2w_switch=1/' /system/bin/atmel_touch.sh","echo -n 1 > /sys/module/atmel_mxt_ts/parameters/dt2w_switch","mount -o ro,remount /system"};
					RunAsRoot(commands);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				c.setChecked(false);
				//Process p = null;
				try {
					SharedPreferences settings = getSharedPreferences("UserInfo", 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("set","enabled");
					editor.commit();
					String[] commands = {"mount -o rw,remount /system","sed -i 's/insmod.*/insmod \\/system\\/lib\\/modules\\/atmel_mxt_ts.ko dt2w_switch=0/' /system/bin/atmel_touch.sh","echo -n 0 > /sys/module/atmel_mxt_ts/parameters/dt2w_switch","mount -o ro,remount /system"};
					RunAsRoot(commands);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
