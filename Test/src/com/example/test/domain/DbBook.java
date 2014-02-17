package com.example.test.domain;

import com.dropbox.sync.android.DbxFileInfo;

public class DbBook implements Comparable<Object>{
	
	private DbxFileInfo mInfo;
	private String mTitle;
	
	public DbBook (DbxFileInfo fileInfo){
		mInfo = fileInfo;
		mTitle = null;
	}
	
	public DbBook(DbxFileInfo fileInfo, String Title){
		mInfo = fileInfo;
		mTitle = Title;
	}

	public String getEPubBookTitle() {
		return mTitle;
	}

	public void setEPubBookTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public DbxFileInfo getInfo() {
		return mInfo;
	}
	@Override
	public int compareTo(Object other) {
		// TODO Auto-generated method stub
		if(this.mInfo.modifiedTime.compareTo(((DbBook)other).getInfo().modifiedTime)>0)
			return 1;
		else if(this.mInfo.modifiedTime.compareTo(((DbBook)other).getInfo().modifiedTime)<0)
			return -1;
		else
			return 0;
	}
	public int compareTo(String arg0) {
		return mTitle.compareTo(arg0);
	}
	@Override
	public boolean equals(Object arg0) {
		if(arg0 instanceof DbBook){
			String aux=((DbBook)arg0).getEPubBookTitle();
			return this.mTitle.equals(aux);
		}
		return false;
	}
}
