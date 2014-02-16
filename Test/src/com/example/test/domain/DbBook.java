package com.example.test.domain;

import com.dropbox.sync.android.DbxFileInfo;

public class DbBook {
	
	private DbxFileInfo mInfo;
	private String mTitle;
	private boolean mTitleAvailable;
	
	public DbBook (DbxFileInfo fileInfo){
		mInfo = fileInfo;
		mTitle = null;
		mTitleAvailable = false;
	}
	
	public DbBook(DbxFileInfo fileInfo, String Title){
		mInfo = fileInfo;
		mTitle = Title;
		mTitleAvailable = false;
	}

	public String getEPubBookTitle() {
		return mTitle;
	}

	public void setEPubBookTitle(String mTitle) {
		mTitleAvailable = true;
		this.mTitle = mTitle;
	}

	public DbxFileInfo getInfo() {
		return mInfo;
	}
	
	public boolean isTitleAvailable(){
		return mTitleAvailable;
	}

}
