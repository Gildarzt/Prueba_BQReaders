package com.example.test;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	
	private int REQUEST_LINK_TO_DBX = 0;
	private Button changeAcc;
	private Button dbButton;
	private Button loginButton;
	private TextView accName;
	private TestManager manager;
	private static Context context;
	
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
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (manager.getManager().hasLinkedAccount()) {
			accName.setText(manager.getManager().getLinkedAccount().getAccountInfo().displayName);
		    showLinkedView();
		} else {
			showUnlinkedView();
		}
	}

    private void showLinkedView() {
        dbButton.setVisibility(View.GONE);
        changeAcc.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        accName.setVisibility(View.VISIBLE);
    }

    private void showUnlinkedView() {
        dbButton.setVisibility(View.VISIBLE);
        changeAcc.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        accName.setVisibility(View.GONE);
    }

	private void onClickDropbox(){
		manager.Login((Activity)this, REQUEST_LINK_TO_DBX);
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
            	showLinkedView();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
	public static Context getAppContext() {
		return MainActivity.context;
	}
}
