package com.example.test.domain;

import com.dropbox.sync.android.DbxFileInfo;
/**This class is my book, I used to store the data from dropbox instead of Book class from epub lib
 * 
 * @author Antonio
 *
 */
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

	public String getTitle() {
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
		int res=0;
		// TODO Auto-generated method stub
		if(other instanceof DbBook){
			if(this.mInfo.modifiedTime.compareTo(((DbBook)other).getInfo().modifiedTime)>0)
				res=1;
			else if(this.mInfo.modifiedTime.compareTo(((DbBook)other).getInfo().modifiedTime)<0)
				res=-1;
		}
		return res;
	}

	@Override
	public boolean equals(Object arg0) {
		if(arg0 instanceof DbBook){
			String aux=((DbBook)arg0).getTitle();
			return this.mTitle.equals(aux);
		}
		return false;
	}
}
