package com.jn.android.guvnor;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

interface RestResultReceiver {
	public void onResultReceived(int resultCode);
}

public class RestService extends IntentService implements RestResultReceiver {
	
	public static final String _TAG = "RestService";
	
	/** Identifier for callback in the bundle extra */
	public static final String CALLBACK_ID = "callback";
	
	/** State flags for service */
	public enum States {
		STATE_RUNNING,	/** Service is running */
		STATE_COMPLETE	/** Service completed the task */
	}
	
	public RestService() {
		super("RESTService");
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
	protected void onHandleIntent(Intent intent) 
	{
		/** Run the worker thread here */
		Log.v(_TAG,"onStartCommand");
		ServiceHelper.ServiceCallback bc = intent.getParcelableExtra(CALLBACK_ID);
	
		Processor p = new Processor(getBaseContext(), "http://192.168.1.101:8080/guvnor-5.2.0.Final-tomcat-6.0/rest/packages", "application/json", false);

		try {
			p.setMediaType("application/json");
			
		}
		catch(UnknownMediaTypeException umte) {
			
		}
		p.start();
		bc.doSomething(0);
	}

	public void onResultReceived(int resultCode) {
		
	}
	
}
