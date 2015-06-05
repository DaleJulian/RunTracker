package com.example.runtracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

public class DataLoader<D> extends AsyncTaskLoader<D> {

	private D mData;
	
	public DataLoader(Context context) {
		super(context);
	}

	@Override
	protected void onStartLoading(){
		if(mData != null){
			deliverResult(mData);
		} else {
			forceLoad();
		}
	}

	@Override 
	public void deliverResult(D data){
		mData = data;
		if(isStarted()){
			super.deliverResult(data);
		}
	}
	
	@Override
	public D loadInBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
