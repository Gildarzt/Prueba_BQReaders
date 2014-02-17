package com.example.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.dropbox.sync.android.DbxPath;
import com.example.test.domain.DbBook;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class Library extends Activity{
	
	private static TextView busyText;
	private static MyListAdapter listAdapter;
	private static ExpandableListView expListView;
	private static List<String> listDataHeader;
	private static HashMap<String, List<DbBook>> listDataChild;
	private static List<DbBook> listBook;
	private TestManager manager;
	private static Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		listBook=new ArrayList<DbBook>();
		setContentView(R.layout.library);
		
		busyText=(TextView)findViewById(R.id.tvSync);
		manager=TestManager.getSession();
		manager.getFiles(DbxPath.ROOT);
		//prepareListData();
		expListView = (ExpandableListView) findViewById(R.id.expandableListView1);
		if(listBook.size()>0){
			busyText.setVisibility(View.VISIBLE);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int itemId=item.getItemId();
		switch(itemId){
		case R.id.titleButton:
			sortByTitle();
			break;
		case R.id.dateButton:
			sortByDate();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	private void sortByTitle(){
		
	}
	private void sortByDate(){
		Collections.sort(listBook);
		prepareListData();
	}
	private static void prepareListData() {
		busyText.setVisibility(View.GONE);
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<DbBook>>();
		// Adding child data
		listDataHeader.add("Libros");
		listDataChild.put(listDataHeader.get(0), listBook);
		listAdapter = new MyListAdapter(context, listDataHeader, listDataChild);
		expListView.setAdapter(listAdapter);
    }
	public static void setListBook(){
		listBook=TestManager.getSession().getListBook();
		prepareListData();
	}
}
