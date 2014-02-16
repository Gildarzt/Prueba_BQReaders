package com.example.test;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import com.example.test.R;
import com.example.test.domain.DbBook;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileStatus;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

public class TestManager {
	private static TestManager mSession;
	private DbxAccountManager mAccManager;
	private DbxFileSystem mDtFs;
	private List<DbBook> listBooks;
	
	private TestManager(){
		Context context = MainActivity.getAppContext();
		mAccManager=DbxAccountManager.getInstance(context,  context.getString(R.string.dropbox_app_key), 
				  context.getString(R.string.dropbox_secret_key));

		getFileSystem();
		listBooks=new ArrayList<DbBook>();
	}
	public boolean getFileSystem(){
		boolean res=false;
		if(mDtFs!=null)
			res=true;
		if(mAccManager.hasLinkedAccount()){
			try {
				mDtFs=DbxFileSystem.forAccount(mAccManager.getLinkedAccount());
				res=true;
			} catch (Unauthorized e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return res;
	}
	public List<DbBook> getListBook(){
		return listBooks;
	}
	public static TestManager getSession(){
		if(mSession==null)
			mSession=new TestManager();
		return mSession;
	}
	public DbxAccountManager getManager(){
		return mAccManager;
	}
	public void Login(Activity act,int requestCode){
		if(mAccManager.hasLinkedAccount())
			mAccManager.startLink(act, requestCode);
	}
	public void Logout(){
		if(mDtFs!=null)
			mDtFs.shutDown();
		if(mAccManager.hasLinkedAccount())
			mAccManager.unlink();
	}
	public void getFiles(DbxPath path){
		SearchFiles files=new SearchFiles(path,".epub");
		files.execute();
	}
	public boolean isSyncronhized(DbxFileInfo fileInfo){
		DbxFile file=null;
		file = OpenFile(fileInfo.path);
		DbxFileStatus status;
		try {
			status = file.getSyncStatus();
			return status.isCached;
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public DbxFile OpenFile(DbxPath path){
		DbxFile file=null;
		try{
			file=mDtFs.open(path);
		}catch(DbxException e){
			e.printStackTrace();
		}
		return file;
	}
	public void CallForResults(ListResult completeList) {
		List<DbBook> files = completeList.getFiles();
		if (!files.isEmpty()){
			for (DbBook dbBook : files) {
				listBooks.add(dbBook);
			}
		}
		
	}
	private class SearchFiles extends AsyncTask<Void, Void,List<DbBook>>{
		private List<DbxPath> mPaths;
		private String mExtension;
		
		public SearchFiles(DbxPath path,String extension){
			mPaths=new ArrayList<DbxPath>();
			mPaths.add(path);
			mExtension=extension;
		}
		@Override
		protected List<DbBook> doInBackground(Void... params){
			List<DbBook> listBook=null;
			try {
				if(!mDtFs.hasSynced())
					mDtFs.awaitFirstSync();
				listBook= getBooks(mPaths,mExtension);
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return listBook;
		}
		@Override
		protected void onPostExecute(List<DbBook> listRes){
			CallForResults(new ListResult(mPaths,listRes));
		}
		
		private List<DbBook> getBooks(List<DbxPath> paths, String fileExtension){
			ArrayList<DbBook> booksFound = new ArrayList<DbBook>();
			while (!paths.isEmpty()){
				DbxPath path = paths.remove(0);
				List<DbxFileInfo> files;
				try {
					files = mDtFs.listFolder(path);
					for (DbxFileInfo fileInfo : files) {
						if (fileInfo.isFolder)
							paths.add(fileInfo.path);
						else if (fileInfo.path.getName().contains(fileExtension)){
							DbBook newItem = new DbBook(fileInfo, fileInfo.path.getName());
							booksFound.add(newItem);
						}
					}
				}catch (DbxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return booksFound;
		}
	}
	/*private class DownloadFiles extends AsyncTask<DbxFileInfo, Void,DbxFileInfo>{
		public DownloadFiles(){}
		@Override
		protected DbxFileInfo doInBackground(DbxFileInfo... params) {
			DbxFileInfo fileInfo = params[0];
			DbxFile file = null;
			try {
				file = getSession().OpenFile(fileInfo.path);
				file.getReadStream();
				return params[0];		
			} catch (Exception e){
				return null;	
			}finally {
				if (file != null){
					file.close();
				}
			}
		}
		@Override
		protected void onPostExecute(DbxFileInfo result) {
			if (result != null && mCallback != null){
				if (getSession().isSyncronhized(result)){
					EPubHelper.getInstance().openBookFromFileInfo(result, this, false);
				}
			}
		}
		}*/
}
