package com.example.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
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
		try{
			listBook=getListBook(this,DbxPath.ROOT);
		}catch(Exception e){
			e.printStackTrace();
		}
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
	private List<Book> getListBook(Activity activity,DbxPath path){
		List<Book> auxList=new ArrayList<Book>();
		try{
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(TestAppConfig.getAccountManager(activity).getLinkedAccount());
			List<DbxFileInfo> infos = dbxFs.listFolder(path);
			for(int i=0;i<infos.size();i++){
				if(dbxFs.isFolder(infos.get(i).path)){
					getListBook(activity,infos.get(i).path);
				}
				else{
					if(dbxFs.isFile(infos.get(i).path)){
						DbxFileInfo info =dbxFs.getFileInfo(infos.get(i).path);
						if(info.path.toString().contains(".epub")){
							Book book = new Book();
							auxList.add(book);
						}
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return auxList;
	}
}
