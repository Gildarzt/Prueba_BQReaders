package com.example.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nl.siegmann.epublib.domain.Book;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;

public class Library extends Activity{
	
	private Button titleButton;
	private Button dateButton;
	
	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, List<Book>> listDataChild;
	private List<Book> listBook;
	
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
		 expListView = (ExpandableListView) findViewById(R.id.expandableListView1);
		listBook=getListBook();
		prepareListData(listBook);
        
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
		expListView.setAdapter(listAdapter);
	}
	private void sortByTitle(){
		
	}
	private void sortByDate(){
		
	}
	private void prepareListData(List<Book> listBook) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Book>>();
 
        // Adding child data
        listDataHeader.add("Libros");
 
        listDataChild.put(listDataHeader.get(0), listBook);
    }
	private List<Book> getListBook(){
		List<Book> aux=new ArrayList<Book>();
		return aux;
	}
}
