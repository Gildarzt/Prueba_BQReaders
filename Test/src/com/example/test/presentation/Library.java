package com.example.test.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.dropbox.sync.android.DbxPath;
import com.example.test.R;
import com.example.test.domain.DbBook;
import com.example.test.domain.TestManager;
import com.example.test.domain.TestManager.actualiceListBook;
import com.example.test.domain.TestManager.showCoverBook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
/**This activity has all the main functionality of the application, it does the search, sort the list and show
 * the cover
 * @author Antonio
 *
 */
public class Library extends Activity implements actualiceListBook,showCoverBook{
	
	private TextView busyText;
	private MyListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, List<DbBook>> listDataChild;
	private List<DbBook> listBook;
	private TestManager manager;
	private Context context;
	private Builder aDBuilder;
	private AlertDialog alertDialog;
	private AlertDialog aux;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		listBook=new ArrayList<DbBook>();
		setContentView(R.layout.library);
		busyText=(TextView)findViewById(R.id.tvSync);
		manager=TestManager.getSession();
		manager.getFiles(DbxPath.ROOT,this);
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
	/**<---------------------------------------------SORT METHODS----------------------------------------------->
	 * 
	 */
	private void sortByTitle(){
		Collections.sort(listBook, new Comparator<Object>(){
			@Override
			public int compare(Object arg0, Object arg1) {
				// TODO Auto-generated method stub
				if(arg0 instanceof DbBook && arg1 instanceof DbBook)
					return Integer.valueOf(((DbBook)arg0).getTitle().compareTo(((DbBook)arg1).getTitle()));
				else
					return 0;
			}
		});
		prepareListData();
	}
	private void sortByDate(){
		Collections.sort(listBook);
		prepareListData();
	}
	/**<-----------------------------------------FILL THE ExpandableListView--------------------------------------->
	 * 
	 */
	private void prepareListData() {
		busyText.setVisibility(View.GONE);
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<DbBook>>();
		// Adding child data
		listDataHeader.add("Libros");
		listDataChild.put(listDataHeader.get(0), listBook);
		listAdapter = new MyListAdapter(context, listDataHeader, listDataChild);
		expListView.setAdapter(listAdapter);
		expListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		expListView.setOnChildClickListener(new OnChildClickListener(){
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {
				// TODO Auto-generated method stub
				DbBook aux=(DbBook) expListView.getItemAtPosition(arg3+1);
				CreatePopUp(aux.getInfo().path);
				return false;
			}
		});
    }
	/**<--------------------------------------------POP UPS---------------------------------------------->
	 * 
	 * @param aux
	 */
	private void CreatePopUp(DbxPath aux){
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.pop_up,null);
		aDBuilder = new AlertDialog.Builder(this);
		aDBuilder.setView(layout);
		alertDialog=aDBuilder.create();
		((AlertDialog) alertDialog).setView(layout,0,0,0,0);
        ((AlertDialog) alertDialog).setInverseBackgroundForced(false);
        Button btnAceptar = (Button) layout.findViewById(R.id.button1);
        btnAceptar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	alertDialog.cancel(); 
            }
       });     
       ImageView image=(ImageView)layout.findViewById(R.id.imageView1); 
       manager.DownloadBook(aux,image,this);
       showAuxDlg();
	}
	private void showAuxDlg(){
		aux=new AlertDialog.Builder(this)
		.setTitle("¡Paciencia!")
		.setMessage("Se esta cargando la imagen").create();
		aux.show();
	}
	/**<---------------------------------INTERFACE METHODS----------------------------------------->
	 * 
	 */
	@Override
	public void setListBook(){
		listBook=TestManager.getSession().getListBook();
		prepareListData();
	}
	@Override
	public void showCover(ImageView iView,Bitmap result) {
		// TODO Auto-generated method stub
		iView.setImageBitmap(result);
		aux.cancel();
		alertDialog.show();
		
	}
}
