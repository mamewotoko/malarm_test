package com.mamewo.malarm_test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jayway.android.robotium.solo.Solo;
import com.mamewo.malarm24.*;

import com.mamewo.malarm24.R;

public class MalarmUITestActivity
	extends ActivityInstrumentationTestCase2<MalarmActivity>
{
	private static final int PORT = 3333;

	private final static String PACKAGE_NAME = "malarm_test";
	private Solo solo;
	private BufferedWriter _bw;
	private Socket _sock;
	private String _hostname = "192.168.0.20";
	//set true to capture screen (it requires CaptureServer in mimicj)
	private boolean _support_capture = false;
	private static class Name2Index {
		String _name;
		int _index;
		
		public Name2Index(String name, int index) {
			_name = name;
			_index = index;
		}
	}

	private static Name2Index[] PREF_TABLE = null;

	private int lookup(Name2Index[] table, String name) {
		for (Name2Index entry : table) {
			if (entry._name.equals(name)) {
				return entry._index;
			}
		}
		return -1;
	}
	
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
		super("com.mamewo.malarm24", MalarmActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		Log.i("malarm_test", "setUp is called " + PREF_TABLE);
		solo = new Solo(getInstrumentation(), getActivity());
		initCapture();
		//static field is cleared to null, why?
		PREF_TABLE = new Name2Index[] {
			new Name2Index("site", 0),
			new Name2Index("default_time", 3),
			new Name2Index("sleep_volume", 5),
			new Name2Index("sleep_playlist", 8),
			new Name2Index("help", 13),
			new Name2Index("version", 14)
		};
	}

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

	//name of test case MUST begin with "test"
	@Smoke
	public void testSetAlarm() {
		//olo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		//use MediaPlayer
		//solo.getCurrentCheckBoxes().get(0).setChecked(false);
		//solo.goBack();
		Date now = new Date(System.currentTimeMillis() + 60 * 1000);
		solo.setTimePicker(0, now.getHours(), now.getMinutes());
		solo.clickOnButton(solo.getString(R.string.set_alarm));
		Assert.assertTrue("check label", solo.getText(0).getText().length() > 0);
		//TODO: go home screen
		solo.goBack();
		solo.sleep(61 * 1000);
		Assert.assertTrue("Switch alarm button wording", solo.searchToggleButton(solo.getString(R.string.stop_alarm)));
		Assert.assertTrue("Correct alarm toggle button state", solo.isToggleButtonChecked(solo.getString(R.string.stop_alarm)));
		//TODO: check music?
		//TODO: check vibration
		solo.clickOnButton(solo.getString(R.string.stop_alarm));
		solo.sleep(1000);
		captureScreen("test_setAlarmTest.png");
		Assert.assertTrue("Alarm stopped", !solo.isToggleButtonChecked(solo.getString(R.string.set_alarm)));
	}
	
	@Smoke
	public void testSetNow() {
		Calendar now = new GregorianCalendar();
		TimePicker picker = solo.getCurrentTimePickers().get(0);
		solo.clickOnButton(solo.getString(R.string.set_now_short));
		//umm... yield to target activity
		solo.sleep(200);
		Assert.assertTrue("picker current hour", now.get(Calendar.HOUR_OF_DAY) == picker.getCurrentHour());
		Assert.assertTrue("picker current min", now.get(Calendar.MINUTE) == picker.getCurrentMinute());
		captureScreen("test_setNow.png");
	}

	//TODO: voice button?
	public void testNextTuneShort() {
		View nextButton = solo.getView(R.id.next_button);
		solo.clickOnView(nextButton);
		solo.sleep(2000);
		//speech recognition dialog
		//capture
		solo.sendKey(Solo.DELETE);
	}

	public void testNextTuneLong() {
		View nextButton = solo.getView(R.id.next_button);
		solo.clickLongOnView(nextButton);
		solo.sleep(2000);
		TextView view = (TextView)solo.getView(R.id.sleep_time_label);
		String text = view.getText().toString();
		Log.i("malrm_test", "LongPressNext: text = " + text);
		Assert.assertTrue(text != null);
		//TODO: check preference value...
		Assert.assertTrue(text.startsWith("60"));
		solo.clickOnMenuItem(solo.getString(R.string.stop_music));
		solo.sleep(2000);
		String afterText = view.getText().toString();
		Assert.assertTrue(afterText == null || afterText.length() == 0);
	}

	/////////////////
	//test menu
	public void testStopVibrationMenu() {
		solo.clickOnMenuItem(solo.getString(R.string.stop_vibration));
		solo.sleep(2000);
	}
	
	public void testPlayMenu() {
		solo.clickOnMenuItem(solo.getString(R.string.play_wakeup));
		solo.sleep(5000);
		solo.clickOnMenuItem(solo.getString(R.string.stop_music));
		solo.sleep(1000);
	}
	
	/////////////////
	//config screen
	@Smoke
	public void testSitePreference() {
		solo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		//select site configuration
		//TODO: make function...
		solo.clickInList(lookup(PREF_TABLE, "site"));
		Assert.assertTrue(true);
		captureScreen("test_Preference.png");
	}
	
	@Smoke
	public void testCreatePlaylists() {
		solo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		solo.clickInList(lookup(PREF_TABLE, "create_playlist"));
		solo.sleep(5000);
		Assert.assertTrue(true);
	}
	
	@Smoke
	public void testSleepVolume() {
		solo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		solo.clickInList(lookup(PREF_TABLE, "sleep_volume"));
		//TODO: push plus/minus
		Assert.assertTrue(true);
	}

	@Smoke
	public void testWakeupVolume() {
		solo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		solo.clickInList(lookup(PREF_TABLE, "wakeup_volume"));
		//TODO: push plus/minus
		Assert.assertTrue(true);
	}

	//add double tap test of webview
	
	@Smoke
	public void testDefaultTimePreference() {
		solo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		solo.clickInList(lookup(PREF_TABLE, "default_time"));
		Assert.assertTrue(true);
	}

	@Smoke
	public void testVibration() {
		solo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		solo.clickInList(lookup(PREF_TABLE, "vibrate"));
		solo.sleep(1000);
		solo.clickInList(lookup(PREF_TABLE, "vibrate"));
	}
	
	public void testSleepPlaylist() {
		solo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		solo.clickInList(lookup(PREF_TABLE, "sleep_playlist"));
		solo.scrollDown();
	}
	
	public void testClearCache() {
		solo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		solo.clickInList(lookup(PREF_TABLE, "clear_webview_cache"));
		solo.sleep(500);
	}
	
	public void testHelp() {
		solo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		solo.clickInList(lookup(PREF_TABLE, "help"));
		solo.sleep(4000);
		captureScreen("test_Help.png");
	}
	
	public void testVersion() {
		solo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		solo.clickInList(lookup(PREF_TABLE, "version"));
		captureScreen("test_Version.png");
	}

	public void testPlaylistLong() {
		solo.clickOnMenuItem(solo.getString(R.string.pref_menu));
		solo.clickInList(lookup(PREF_TABLE, "sleep_playlist"));
		solo.sleep(500);
		solo.clickLongInList(0);
		captureScreen("test_playlistlong.png");
	}
	
	//TODO: default config test	
	//TODO: add test of widget
	//TODO: playlist edit
}