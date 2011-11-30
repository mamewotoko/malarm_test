package com.mamewo.malarm_test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.widget.TimePicker;

import com.jayway.android.robotium.solo.Solo;
import com.mamewo.malarm.*;

public class MalarmUITestActivity extends ActivityInstrumentationTestCase2<MalarmActivity> {

	private static final int PORT = 3333;

	private final static String PACKAGE_NAME = "malarm_test";
	private Solo solo;
	private BufferedWriter _bw;
	private Socket _sock;
	private String _hostname = "192.168.0.20";
	//set true to capture screen (it requires CaptureServer in mimicj)
	private boolean _support_capture = false;
	
	public void initCapture() throws IOException {
		if (!_support_capture) {
			return;
		}
		_sock = new Socket(_hostname, PORT);
		_bw = new BufferedWriter(new OutputStreamWriter(_sock.getOutputStream()));
	}
	
	public void finalizeCapture() {
		if (!_support_capture) {
			return;
		}
		Log.i("malam_test", "finalizeCapture is called");
		try {
			_bw.close();
			_sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void captureScreen(String filename) {
		if (!_support_capture) {
			return;
		}
		try {
			_bw.write(filename + "\n");
			_bw.flush();
			//TODO: wait until captured
			Thread.sleep(1000);
		} catch (IOException e) {
			Log.i ("malarm_test", "capture failed: " + filename);
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public MalarmUITestActivity() {
		super("com.mamewo.malarm", MalarmActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		System.out.println("setUp is called");
		solo = new Solo(getInstrumentation(), getActivity());
		initCapture();
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
		captureScreen("test_setAlarmTest.png");
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
		captureScreen("test_setNow.png");
	}
	
	@Smoke
	public void testPreference() throws Exception {
		solo.clickOnMenuItem(solo.getString(com.mamewo.malarm.R.string.pref_menu));
		//select site configuration
		solo.clickInList(0);
		Assert.assertTrue(true);
		captureScreen("test_Preference.png");
	}
	
	//TODO: add test of widget
	
	@Override
	public void tearDown() throws Exception {
		System.out.println("tearDown is called");
		try {
			//Robotium will finish all the activities that have been opened
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		finalizeCapture();
		getActivity().finish();
		super.tearDown();
	} 
}