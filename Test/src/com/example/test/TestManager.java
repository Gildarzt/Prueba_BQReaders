package com.example.test;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
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
/**This is the main class, it's function is to use dropbox API-sync to connect with dropbox
 * 
 * @author Antonio
 *
 */
public class TestManager {
	private static TestManager mSession;
	private DbxAccountManager mAccManager;
	private DbxFileSystem mDtFs;
	private List<DbBook> listBooks;
	private boolean result;//This variable is used in syncronizedDataFile class.
	
	private TestManager(){
		Context context = MainActivity.getAppContext();
		mAccManager=DbxAccountManager.getInstance(context,  context.getString(R.string.dropbox_app_key), 
				  context.getString(R.string.dropbox_secret_key));

		getFileSystem();
		listBooks=new ArrayList<DbBook>();
	}
	public boolean getFileSystem(){
		SyncronizedDataFile dataFile=new SyncronizedDataFile();
		dataFile.execute();
		return result;
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
		if(!mAccManager.hasLinkedAccount()){
			mAccManager.startLink(act, requestCode);
			getFileSystem();
		}
	}
	public void Logout(){
		if(mDtFs!=null)
			mDtFs.shutDown();
		if(mAccManager.hasLinkedAccount())
			mAccManager.unlink();
	}
	public void getFiles(DbxPath path){
		try {
			if(mDtFs.hasSynced()){
				SearchFiles files=new SearchFiles(path,".epub");
				files.execute();
			}
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public boolean getSyncResult(){
		return result;
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
				if(!listBooks.contains(dbBook))
					listBooks.add(dbBook);
			}
			Library.setListBook();
		}
	}
	public void OpenDbBook(DbxFileInfo fileInfo,boolean sync) {
		OpenBook openBook = new OpenBook(sync);
		openBook.execute(fileInfo);
	}
	/**<--------------------------------SEARCH CLASS---------------------------------------------------------->
	 * The focus of this one is to search the books on the dropbox system. It's an asynchronous task to not 
	 * collapse the application.
	 * @author Antonio
	 *
	 */
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
							if(!booksFound.contains(newItem))
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
	/**<------------------------------------------DOWNLOAD TASK---------------------------------------------->
	 * Here I download the file and read it to convert into a BdBook.
	 * @author Antonio
	 *
	 */
	private class DownloadFiles extends AsyncTask<DbxFileInfo, Void,DbxFileInfo>{
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
			if (result != null){
				if (getSession().isSyncronhized(result)){
					OpenDbBook(result,false);
				}
			}
		}
	}
	/**<---------------------------------------GET THE DATA FROM THE RESULT----------------------------------->
	 * This class is used to get the data after download the file from the previous task.
	 * @author Antonio
	 */
	private class OpenBook extends AsyncTask<DbxFileInfo, Void, Book>{
		private boolean mSync;
		
		public OpenBook(boolean sync){
			mSync = sync;
		}

		@Override
		protected Book doInBackground(DbxFileInfo... params) {
			DbxFileInfo fileInfo = params[0];
			Book book = null;
			DbxFile file = null;
			try {
				if (!mSync || TestManager.getSession().isSyncronhized(fileInfo)){
					file = TestManager.getSession().OpenFile(fileInfo.path);
					EpubReader reader = new EpubReader();
					book = reader.readEpub(file.getReadStream());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (file != null){
					file.close();
				}
			}
			return book;
		}

		/*@Override
		protected void onPostExecute(Book result) {
			if (result != null){
				mBookListener.OnBookReady(result);
			}
		}*/
	}
	/**<----------------------------------------------SYNCRONYZED DROPBOX---------------------------------->*/
	private class SyncronizedDataFile extends AsyncTask<Void, Void, Boolean>{
		public SyncronizedDataFile(){}
		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			boolean res=false;
			if(mDtFs!=null)
				res=true;
			if(mAccManager.hasLinkedAccount()){
				try {
					mDtFs=DbxFileSystem.forAccount(mAccManager.getLinkedAccount());
					if(!mDtFs.hasSynced())
						mDtFs.syncNowAndWait();
					
					res=true;
				} catch (Unauthorized e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}catch (DbxException e2) {
					e2.printStackTrace();
				}
			}
			return res;
		}
		@Override
		protected void onPostExecute(Boolean res) {
			if (res != null){
				MainActivity.accSyncGone();
				result=res;
			}
		}
	}
}
