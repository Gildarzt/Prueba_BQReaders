package com.example.test.presentation;

import com.example.test.R;
import com.example.test.domain.TestManager;
import com.example.test.domain.TestManager.actualiceStatus;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
/**Main Activity
 * 
 * @author Antonio
 *
 */
public class MainActivity extends Activity implements actualiceStatus{
	
	private int REQUEST_LINK_TO_DBX = 0;
	private Button changeAcc;
	private Button dbButton;
	private Button loginButton;
	private TextView accName;
	private TestManager manager;
	private static Context context;
	private TextView accSync;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=getApplicationContext();
		setContentView(R.layout.activity_main);
		manager=TestManager.getSession();
		dbButton=(Button)findViewById(R.id.dbButton);
		dbButton.setOnClickListener(new OnClickListener(){
			 @Override
	         public void onClick(View v) {
				 onClickDropbox();
	         }
		});
		changeAcc=(Button)findViewById(R.id.changeAcc);
		changeAcc.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				manager.Logout();
				showUnlinkedView();
			}
		});
		loginButton=(Button)findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent=new Intent(MainActivity.this,Library.class);
				startActivityForResult(intent,1);
			}
		});
		accName=(TextView)findViewById(R.id.accName);
		accSync=(TextView)findViewById(R.id.tvSync);
		/**This part of the code was first in onResume method, but i only want that it executes once, so i put here
		 * 
		 */
		if(manager!=null){
			if (manager.getManager().hasLinkedAccount()) {
				manager.getFileSystem(this);
				showActView();
			} else {
				showUnlinkedView();
			}
		}
		else
			showUnlinkedView();
	}
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
            	manager.getFileSystem(this);
            	showActView();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
	private void onClickDropbox(){
		manager.Login((Activity)this, REQUEST_LINK_TO_DBX);
	}
	/**This method is used in the TestManager class to get the context of the application
	 * 
	 * @return
	 */
	public static Context getAppContext() {
		return MainActivity.context;
	}
	/**<------------------------------------METHODS TO SHOW THE DIFERENT STATE---------------------------->*/
	private void showActView(){
		accSync.setVisibility(View.VISIBLE);
		dbButton.setVisibility(View.GONE);
        changeAcc.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        accName.setVisibility(View.GONE);
		
	}
    private void showLinkedView() {
        dbButton.setVisibility(View.GONE);
        changeAcc.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        accName.setVisibility(View.VISIBLE);
        accSync.setVisibility(View.GONE);
    }

    private void showUnlinkedView() {
        dbButton.setVisibility(View.VISIBLE);
        changeAcc.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        accName.setVisibility(View.GONE);
        accSync.setVisibility(View.GONE);
    }
	/**<-------------------------------------------INTERFACE------------------------------------------------->*/
	@Override
	public void accSyncGone(){
		if(manager.getManager().getLinkedAccount().getAccountInfo()!=null){	
			accName.setText(manager.getManager().getLinkedAccount().getAccountInfo().displayName);
			showLinkedView();
		}
	}
}
