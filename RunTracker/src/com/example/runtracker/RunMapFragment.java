package com.example.runtracker;

import java.util.Date;

import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.runtracker.RunDatabaseHelper.LocationCursor;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class RunMapFragment extends SupportMapFragment implements LoaderCallbacks<Cursor>{

	
	
	private static final String ARG_RUN_ID = "RUN_ID";
	private static final int LOAD_LOCATIONS = 0;
	
	private GoogleMap googleMap;
	private LocationCursor mLocationCursor;
	
	public static RunMapFragment newInstance(long runId){
		Bundle args = new Bundle();
		args.putLong(ARG_RUN_ID, runId);
		RunMapFragment rf = new RunMapFragment();
		rf.setArguments(args);
		return rf;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		
		View v = super.onCreateView(inflater, parent, savedInstanceState);
		
		//stash a reference to the google map
		googleMap = getMap();
		//show the user's location
		googleMap.setMyLocationEnabled(true);
		
		return v;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		long runId = args.getLong(ARG_RUN_ID, -1);
		return new LocationListCursorLoader(getActivity(), runId);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mLocationCursor = (LocationCursor)cursor;
		updateUI();
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mLocationCursor.close();
		mLocationCursor = null;
		
	}
	
	private void updateUI(){
		if(googleMap == null || mLocationCursor == null) return;
		
		
		//setup an overlay on the map for this run's locations
		//create a polyline with all of the points
		PolylineOptions line = new PolylineOptions();
		//also create a LatLngBounds so you can zoom to fit
		LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
		//iterate over the locations
		while (!mLocationCursor.isAfterLast()) {
			Location loc = mLocationCursor.getLocation();
			LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
			
			Resources r = getResources();
			// If this is the first location, add a marker for it
			if (mLocationCursor.isFirst()) {
				String startDate = new Date(loc.getTime()).toString();
				MarkerOptions startMarkerOptions = new MarkerOptions()
				.position(latLng)
				.title(r.getString(R.string.run_start))
				.snippet(r.getString(R.string.run_started_at_format, startDate));
				googleMap.addMarker(startMarkerOptions);
			} else if (mLocationCursor.isLast()) {
				// If this is the last location, and not also the first, add a marker
				String endDate = new Date(loc.getTime()).toString();
				MarkerOptions finishMarkerOptions = new MarkerOptions()
				.position(latLng)
				.title(r.getString(R.string.run_finish))
				.snippet(r.getString(R.string.run_finished_at_format, endDate));
				googleMap.addMarker(finishMarkerOptions);

			}

			line.add(latLng);
			latLngBuilder.include(latLng);
			mLocationCursor.moveToNext();
		}
		// Add the polyline to the map
		googleMap.addPolyline(line);
		// Make the map zoom to show the track, with some padding
		// Use the size of the current display in pixels as a bounding box
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		// Construct a movement instruction for the map camera
		LatLngBounds latLngBounds = latLngBuilder.build();
		CameraUpdate movement = CameraUpdateFactory.newLatLngBounds(latLngBounds,
		display.getWidth(), display.getHeight(), 15);
		googleMap.moveCamera(movement);

		
	}
}
