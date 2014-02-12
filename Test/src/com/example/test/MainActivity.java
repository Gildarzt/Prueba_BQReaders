package com.example.test;

import java.io.IOException;
import java.util.List;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	private DbxAccountManager dbAcc;
	private String appKey = "4rsxxjd1d0sczfu";
	private String appSecret = "hmmwmf5uzeduy37";
	private int REQUEST_LINK_TO_DBX = 0;
	private TextView mTestOutput;
	private Button loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		loginButton=(Button)findViewById(R.id.loginButton);
		mTestOutput = (TextView) findViewById(R.id.test_output);
		loginButton.setOnClickListener(new OnClickListener(){
			 @Override
	         public void onClick(View v) {
				 onClickDropbox();
	         }
		});
		 dbAcc = DbxAccountManager.getInstance(getApplicationContext(), appKey, appSecret);
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (dbAcc.hasLinkedAccount()) {
		    showLinkedView();
		    doDropboxTest();
		} else {
			showUnlinkedView();
		}
	}

    private void showLinkedView() {
        loginButton.setVisibility(View.GONE);
        mTestOutput.setVisibility(View.VISIBLE);
    }

    private void showUnlinkedView() {
        loginButton.setVisibility(View.VISIBLE);
        mTestOutput.setVisibility(View.GONE);
    }

	private void onClickDropbox(){
		dbAcc.startLink((Activity)this, REQUEST_LINK_TO_DBX);
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                doDropboxTest();
            } else {
                mTestOutput.setText("Link to Dropbox failed or was cancelled.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void doDropboxTest() {
       
    }
}
