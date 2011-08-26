package com.jn.android.guvnor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Thread;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

class ServiceRequestException extends Throwable {
	
	private String errorMessage;
	
	public ServiceRequestException(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
}

class UnknownItemTypeException extends Throwable {
private String errorMessage;
	
	public UnknownItemTypeException(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
}

public class Processor extends Thread {
	
	private static final String _TAG = "Processor";
	private static Map<String, ParserHandler> handlers;
	private Context context;
	private String url;
	private String itemType;
	private ParserHandler parserHandler;
	private boolean useGzipCompression;
	
	static {
		handlers = new HashMap<String, ParserHandler>();
		handlers.put("packages", new PackageParserHandler());
	}
	
	public Processor(Context ctx, String url, String itemType, boolean useGzipCompression) {
		this.context = ctx;
		this.url = url;
		this.itemType = itemType;
		this.useGzipCompression = useGzipCompression;
	}
	
	public void setItemType(String it) throws UnknownItemTypeException {
		itemType = it;
		parserHandler = handlers.get(itemType);
	}
	
	public void setUrl(String address) {
		url = address;
	}
	
	private void insertPackageResourceToDB(Package packageResource, ContentResolver contentResolver) {
		ContentValues packageValues = packageResource.toContentValues();
		Uri resultUri = contentResolver.insert(DataProvider.CONTENT_URI_PACKAGE, packageValues);
		String packageId = resultUri.getPathSegments().get(1);
		packageResource.metaData.foreignId = packageId;
		ContentValues metaDataValues = packageResource.metaData.toContentValues();
		Uri insertResultUri = contentResolver.insert(DataProvider.CONTENT_URI_METADATA, metaDataValues);
		Log.v(_TAG, "metadata inserted with uri: " + insertResultUri.toString());
	}
	
	/** TODO */
	//private void updatePackageResourceToDB()
	//private void insertAssetResourceToDB()
	//private void updateAssetResourceToDB()
	
	@Override
	public void run() {
		try {
			String response = RestMethod.doGet(url, useGzipCompression);
			Package[] packages = (Package[])parserHandler.parse(response);
			if(packages.length > 0) {
				ContentResolver cr = context.getContentResolver();
				for(int i = 0; i < packages.length; i++) {
					insertPackageResourceToDB(packages[i], cr);
				}
			}
		} 
		catch(ClientProtocolException ce) {
	
		}
		catch(IOException ioe) {
			
		}
		catch(URISyntaxException use) {
		
		}
		catch(ParserException pe) {
			
		}
	}
	
}
