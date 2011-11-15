package com.mamewo.malarm_test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jayway.android.robotium.solo.Solo;
import com.mamewo.malarm.*;

public class MalarmUITestActivity extends ActivityInstrumentationTestCase2<MalarmActivity> {

//	private static final int PORT = 3333;

	private final static String PACKAGE_NAME = "malarm_test";
	private Solo solo;
//	private InputStream in;
//	private OutputStream out;
	
	public MalarmUITestActivity() {
		super("com.mamewo.malarm", MalarmActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
//		Socket sock = new Socket("localhost", PORT);
//		in = sock.getInputStream();
//		out = sock.getOutputStream();
	}
	
	//name of test case MUST begin with "test"
	@Smoke
	public void testSetAlarm() throws Exception {
		solo.clickOnMenuItem(solo.getString(com.mamewo.malarm.R.string.pref_menu));
		//use MediaPlayer
		solo.getCurrentCheckBoxes().get(0).setChecked(false);
		solo.goBack();
		Date now = new Date(System.currentTimeMillis() + 60 * 1000);
		solo.setTimePicker(0, now.getHours(), now.getMinutes());
		solo.clickOnButton(solo.getString(com.mamewo.malarm.R.string.set_alarm));
		Assert.assertTrue("check label", solo.getText(0).getText().length() > 0);
		//TODO: go home screen
		solo.goBack();
		solo.sleep(61 * 1000);
		Assert.assertTrue("Switch alarm button wording", solo.searchToggleButton(solo.getString(com.mamewo.malarm.R.string.stop_alarm)));
		Assert.assertTrue("Correct alarm toggle button state", solo.isToggleButtonChecked(solo.getString(com.mamewo.malarm.R.string.stop_alarm)));
		//TODO: check music?
		//TODO: check vibration
		solo.clickOnButton(solo.getString(com.mamewo.malarm.R.string.stop_alarm));
		solo.sleep(1000);
		Assert.assertTrue("Alarm stopped", !solo.isToggleButtonChecked(solo.getString(com.mamewo.malarm.R.string.set_alarm)));
	}
	
	@Smoke
	public void testSetNow() throws Exception {
		Calendar now = new GregorianCalendar();
		TimePicker picker = solo.getCurrentTimePickers().get(0);
		solo.clickOnButton(solo.getString(com.mamewo.malarm.R.string.set_now_short));
		//umm... yeild to target activity
		solo.sleep(500);
		Assert.assertTrue("picker current hour", now.get(Calendar.HOUR_OF_DAY) == picker.getCurrentHour());
		Assert.assertTrue("picker current min", now.get(Calendar.MINUTE) == picker.getCurrentMinute());
	}
	
	@Smoke
	public void testPreference() throws Exception {
		solo.clickOnMenuItem(solo.getString(com.mamewo.malarm.R.string.pref_menu));
		//select site configuration
		solo.clickInList(0);
		Assert.assertTrue(true);
	}
	
	//TODO: add test of widget
	
	@Override
	public void tearDown() throws Exception {
		try {
			//Robotium will finish all the activities that have been opened
			
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
//			if (in != null) {
//				in.close();
//			}
//			if (out != null) {
//				out.close();
//			}
		}
		getActivity().finish();
		super.tearDown();
	} 
}