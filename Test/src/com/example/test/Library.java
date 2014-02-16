package com.example.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.dropbox.sync.android.DbxPath;
import com.example.test.domain.DbBook;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;

public class Library extends Activity{
	
	private Button titleButton;
	private Button dateButton;
	private MyListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, List<DbBook>> listDataChild;
	private List<DbBook> listBook;
	private TestManager manager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.library);
		titleButton=(Button)findViewById(R.id.titleButton);
		titleButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				sortByTitle();
			}
		});
		dateButton=(Button)findViewById(R.id.dateButton);
		dateButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				sortByDate();
			}
		});
		manager=TestManager.getSession();
		manager.getFiles(DbxPath.ROOT);
		prepareListData();
		expListView = (ExpandableListView) findViewById(R.id.expandableListView1);
        listAdapter = new MyListAdapter(this, listDataHeader, listDataChild);
		expListView.setAdapter(listAdapter);
	}
	private void sortByTitle(){
		
	}
	private void sortByDate(){
		
	}
	private void prepareListData() {
		
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<DbBook>>();
 
        // Adding child data
        listDataHeader.add("Libros");
 
        listDataChild.put(listDataHeader.get(0), listBook);
    }
	// Container Activity must implement this interface
    public interface OnFileSelectedListener {
        public void onFileSelected(DbxPath path);
    }
}
