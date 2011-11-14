package com.mamewo.malarm_test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import com.jayway.android.robotium.solo.Solo;
import com.mamewo.malarm.*;

public class MalarmUITestActivity extends ActivityInstrumentationTestCase2<MalarmActivity> {

	private static final int PORT = 3333;

	private Solo solo;
	private InputStream in;
	private OutputStream out;
	
	public MalarmUITestActivity() {
		super("com.mamewo.malarm", MalarmActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		Socket sock = new Socket("localhost", PORT);
		in = sock.getInputStream();
		out = sock.getOutputStream();
	}
	
	//name of test case MUST begin with "test"
	@Smoke
	public void testSetAlarm() throws Exception {
		solo.clickOnButton("アラームをセットする");
		Thread.sleep(1000);
		Assert.assertTrue("Switch alarm button wording", solo.searchToggleButton("アラームをとめる"));
		Assert.assertTrue("Correct alarm toggle button state", solo.isToggleButtonChecked("アラームをとめる"));
		//TODO: check music?
		
	}
	
	@Override
	public void tearDown() throws Exception {
		try {
			//Robotium will finish all the activities that have been opened
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
		getActivity().finish();
		super.tearDown();
	} 
}