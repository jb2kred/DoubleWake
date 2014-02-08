package com.jb2kred.doublewake;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends Activity {

	String[] command_enable_s2w = {"mount -o rw,remount /system","sed -i 's/insmod.*/insmod \\/system\\/lib\\/modules\\/atmel_mxt_ts.ko wake_switch=2/' /system/bin/atmel_touch.sh","echo -n 2 > /sys/module/atmel_mxt_ts/parameters/wake_switch","mount -o ro,remount /system"};
	String[] command_enable_dt2w = {"mount -o rw,remount /system","sed -i 's/insmod.*/insmod \\/system\\/lib\\/modules\\/atmel_mxt_ts.ko wake_switch=1/' /system/bin/atmel_touch.sh","echo -n 1 > /sys/module/atmel_mxt_ts/parameters/wake_switch","mount -o ro,remount /system"};
	String[] command_disable = {"mount -o rw,remount /system","sed -i 's/insmod.*/insmod \\/system\\/lib\\/modules\\/atmel_mxt_ts.ko wake_switch=0/' /system/bin/atmel_touch.sh","echo -n 0 > /sys/module/atmel_mxt_ts/parameters/wake_switch","mount -o ro,remount /system"};
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	long initialTime = 0;
	long endTime = 0;
	float x,x1;
	float y,y1;
	boolean firstClick = false;
	boolean secondClick = false;

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
		final RadioButton e = (RadioButton) findViewById(R.id.radioButton3);
		final TextView view1 = (TextView) findViewById(R.id.textView4);

		CheckBox check1 = (CheckBox) findViewById(R.id.checkBox1);
		CheckBox check2 = (CheckBox) findViewById(R.id.checkBox2);
		CheckBox check3 = (CheckBox) findViewById(R.id.checkBox3);

		File file = new File("/system/lib/modules/atmel_mxt_ts.ko");

		boolean  exists = file.exists();

		if(exists)
		{
			check1.setChecked(true);
		}

		file = new File("/sys/module/atmel_mxt_ts/parameters/wake_switch");

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

		if(ch.contains("enabled_dt2w"))
		{
			c.setChecked(true);
			e.setChecked(false);
			d.setChecked(false);
		}
		if(ch.contains("enabled_s2w"))
		{
			e.setChecked(true);
			c.setChecked(false);
			d.setChecked(false);
			view1.setText("When using Sweep2Wake\nSweep from middle points of axis\n");
		}
		if(ch.contains("disabled"))
		{
			d.setChecked(true);
			c.setChecked(false);
			e.setChecked(false);
		}

		view1.setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View v ,MotionEvent ev)
			{
						
				if(secondClick == true)
				{
					x1 = ev.getX();
					y1 = ev.getY();	
				}
				else
				{
					x = ev.getX();
					y = ev.getY();	
				}
				
				return false;
			}
		});
		
		view1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {


				if(e.isChecked())
				{
				}
				if(c.isChecked())
				{
					if(firstClick == false && secondClick ==false)
					{
						firstClick = true;
					}

					TextView tmp = (TextView)v;

					if(firstClick == true)
					{
						initialTime = System.currentTimeMillis();
						firstClick = false;
						secondClick =true;
						return;
					}

					if(secondClick ==true) {
						int distance = (int)Math.sqrt((x-x1)*(x-x1) + (y-y1)*(y-y1));
						secondClick =false;
						
						endTime = System.currentTimeMillis();
						long diff = endTime - initialTime;
						initialTime = endTime;
					
						if (diff > 100 && diff< 400)
						{
							if (distance < 65)
							{
								tmp.setTextColor(Color.WHITE);
								tmp.setText(String.valueOf(diff + " milliseconds between taps\n Taps are Between 100 - 400 milliseconds\n"));	
							}
							else
							{
								tmp.setTextColor(Color.RED);
								tmp.setText(String.valueOf(diff + " milliseconds between taps\n Taps are Between 100 - 400 milliseconds\n"  + "Taps are too far Apart"));
							}
						}
						else
						{
							if (distance < 65)
							{
								tmp.setTextColor(Color.RED);
								tmp.setText(String.valueOf(diff + " milliseconds between taps\n Taps need to be within 100 - 400 milliseconds\n"));	
							}
							else
							{
								
								tmp.setTextColor(Color.RED);
								tmp.setText(String.valueOf(diff + " milliseconds between taps\n Taps need to be within 100 - 400 milliseconds\n"  + "Taps are too far Apart"));
							}
						}
					}
				}
				
				
			}
		});

		e.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				c.setChecked(false);
				d.setChecked(false);
				
				view1.setText("When using Sweep2Wake\nSweep from middle points of axis\n");
				
				try {
					prefs("enabled_s2w");
					RunAsRoot(command_enable_s2w);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				d.setChecked(false);
				e.setChecked(false);

				try {
					prefs("enabled_dt2w");
					RunAsRoot(command_enable_dt2w);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				c.setChecked(false);
				e.setChecked(false);
				
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
