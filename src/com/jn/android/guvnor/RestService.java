package com.jn.android.guvnor;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

interface RestResultReceiver {
	public void onResultReceived(int resultCode);
}

public class RestService extends Service implements RestResultReceiver {
	
	public static final String _TAG = "RestService";
	
	/** Identifier for callback in the bundle extra */
	public static final String CALLBACK_ID = "callback";
	
	/** State flags for service */
	public enum States {
		STATE_RUNNING,	/** Service is running */
		STATE_COMPLETE	/** Service completed the task */
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public synchronized int onStartCommand(Intent intent, int flags, int startId) {
	
		/** Run the worker thread here */
		Log.v(_TAG,"onStartCommand");
		ServiceHelper.ServiceCallback bc = intent.getParcelableExtra(CALLBACK_ID);
	
		Processor p = new Processor("http://192.168.1.101:8080/guvnor-5.2.0.Final-tomcat-6.0/rest/packages", "application/json", false);

		try {
			p.setMediaType("application/json");
			
		}
		catch(UnknownMediaTypeException umte) {
			
		}
		p.start();
		bc.doSomething(0);
		return Service.START_STICKY;
	}

	/** From RestResultReceiver (maybe called by multiple threads, so make synchronized) */
	public synchronized void onResultReceived(int resultCode) {
		
	}
	
}
