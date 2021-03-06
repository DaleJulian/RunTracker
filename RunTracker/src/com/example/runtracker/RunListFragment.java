package com.example.runtracker;

import com.example.runtracker.RunDatabaseHelper.RunCursor;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class RunListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	private static final int REQUEST_NEW_RUN = 0;

//	private RunCursor mRunCursor;
	
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		/*
		//query the list of runs
		mRunCursor = RunManager.get(getActivity()).queryRuns();
		
		//create an adapter to point at this cursor
		RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), mRunCursor);
		setListAdapter(adapter);
		*/
		//initialize the loader to load the list of runs
		getLoaderManager().initLoader(0, null, this);
	
	}
	
	/*
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	*/
	
	private static class RunCursorAdapter extends CursorAdapter{
		
		private RunCursor mRunCursor;
		
		public RunCursorAdapter(Context c, RunCursor cursor){
			super(c, cursor, 0);
			mRunCursor = cursor;
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			//get the run for the current row
			Run run = mRunCursor.getRun();
			
			//setup the start date textview
			TextView startDateTextView = (TextView)view;
			String cellText = context.getString(R.string.cell_text, run.getStartDate());
			startDateTextView.setText(cellText);
			
		}
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.run_list_options, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.menu_item_new_run:
				Intent i = new Intent(getActivity(), RunActivity.class);
				startActivityForResult(i, REQUEST_NEW_RUN);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(REQUEST_NEW_RUN == requestCode){
//			mRunCursor.requery();
//			((RunCursorAdapter)getListAdapter()).notifyDataSetChanged();
			
			//restart the loader to get any new run available
			getLoaderManager().restartLoader(0, null, this);
		}
	}
	
	@Override 
	public void onListItemClick(ListView l, View v, int position, long id){
		//the id argument will be the run ID; cursor adapter gives us this for free
		Intent i = new Intent(getActivity(), RunActivity.class);
		i.putExtra(RunActivity.EXTRA_RUN_ID, id);
		startActivity(i);
	}
	
	private static class RunListCursorLoader extends SQLiteCursorLoader{

		public RunListCursorLoader(Context context) {
			super(context);
		
		}
		
		@Override
		protected Cursor loadCursor(){
			//query the list of runs
			return RunManager.get(getContext()).queryRuns();
		}
		
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args){
		//you only ever load the runs, so assume this is the case
		return new RunListCursorLoader(getActivity());
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
		RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), (RunCursor)cursor);
		setListAdapter(adapter);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader){
		//stop using the cursor (via the adapter)
		setListAdapter(null);
	}
}
