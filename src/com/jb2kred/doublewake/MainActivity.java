package com.jb2kred.doublewake;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;

public class MainActivity extends Activity {

	String[] command_enable = {"mount -o rw,remount /system","sed -i 's/insmod.*/insmod \\/system\\/lib\\/modules\\/atmel_mxt_ts.ko dt2w_switch=1/' /system/bin/atmel_touch.sh","echo -n 1 > /sys/module/atmel_mxt_ts/parameters/dt2w_switch","mount -o ro,remount /system"};
	String[] command_disable = {"mount -o rw,remount /system","sed -i 's/insmod.*/insmod \\/system\\/lib\\/modules\\/atmel_mxt_ts.ko dt2w_switch=0/' /system/bin/atmel_touch.sh","echo -n 0 > /sys/module/atmel_mxt_ts/parameters/dt2w_switch","mount -o ro,remount /system"};
	SharedPreferences settings;
	SharedPreferences.Editor editor;

	public void RunAsRoot(String[] cmds) throws IOException{
		Process p = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(p.getOutputStream());            
		for (String tmpCmd : cmds) {
			os.writeBytes(tmpCmd+"\n");
		}           
		os.writeBytes("exit\n");  
		os.flush();
	}

	public void prefs(String s)
	{
		editor = settings.edit();
		editor.putString("set",s);
		editor.commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final RadioButton c = (RadioButton) findViewById(R.id.radioButton1);
		final RadioButton d = (RadioButton) findViewById(R.id.radioButton2);
		CheckBox check1 = (CheckBox) findViewById(R.id.checkBox1);
		CheckBox check2 = (CheckBox) findViewById(R.id.checkBox2);
		CheckBox check3 = (CheckBox) findViewById(R.id.checkBox3);

		File file = new File("/system/lib/modules/atmel_mxt_ts.ko");

		boolean  exists = file.exists();

		if(exists)
		{
			check1.setChecked(true);
		}

		file = new File("/sys/module/atmel_mxt_ts/parameters/dt2w_switch");

		exists = file.exists();

		if(exists)
		{
			check2.setChecked(true);
		}

		file = new File("/system/bin/atmel_touch.sh");

		exists = file.exists();

		if(exists)
		{
			check3.setChecked(true);
		}

		if(check1.isChecked() && check2.isChecked() && check3.isChecked())
		{
			c.setEnabled(true);
			d.setEnabled(true);
		}
		else
		{
			c.setEnabled(false);
			d.setEnabled(false);
		}

		settings = getSharedPreferences("UserInfo", 0);
		String ch = settings.getString("set", "").toString();

		if(ch.contains("enabled"))
		{
			c.setChecked(true);
		}
		else
		{
			d.setChecked(true);
		}

		c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				d.setChecked(false);

				try {
					prefs("enabled");
					RunAsRoot(command_enable);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				c.setChecked(false);

				try {
					prefs("disabled");
					RunAsRoot(command_disable);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
