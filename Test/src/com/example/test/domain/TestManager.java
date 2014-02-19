package com.example.test.domain;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.test.R;
import com.example.test.presentation.MainActivity;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileStatus;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import android.graphics.Bitmap;

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
	private SyncronizedDataFile dataFile;
	private SearchFiles files;
	private OpenBook openBook;
	private GetCover cover;
	
	private TestManager(){
		Context context = MainActivity.getAppContext();
		mAccManager=DbxAccountManager.getInstance(context,  context.getString(R.string.dropbox_app_key), 
				  context.getString(R.string.dropbox_secret_key));
		listBooks=new ArrayList<DbBook>();
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
		if(mSession==null){
			mSession=getSession();
			mAccManager=mSession.getManager();
		}
		if(!mAccManager.hasLinkedAccount()){
			mAccManager.startLink(act, requestCode);
		}
	}
	public void Logout(){
		result=false;
		if(mDtFs!=null){
			mDtFs.shutDown();
			mDtFs=null;
		}
		if(mAccManager.hasLinkedAccount()){
			mAccManager.unlink();
			mAccManager=null;
		}
		terminateThreads();
		mSession=null;
	}
	private void terminateThreads(){
		 if(dataFile!=null)dataFile.cancel(true);
		 if(files!=null)files.cancel(true);
		 if(openBook!=null)openBook.cancel(true);
		 if(cover!=null)cover.cancel(true);
	}
	/**<----------------------------------------------SYNCRONYZED DROPBOX TASK---------------------------------->*/
	/**This interface is use on main activity to show the status of the application*/
	public interface actualiceStatus{
		public void accSyncGone();
	}
	/**<------------------------------------------AUXILIARY METHODS--------------------------------------------->*/
	public void setmDtFs(DbxFileSystem res,actualiceStatus mCall){
		this.mDtFs=res;
		mCall.accSyncGone();
	}
	public boolean getFileSystem(actualiceStatus mCall){
		dataFile=new SyncronizedDataFile(mCall);
		dataFile.execute();
		return result;
	}
	/**<----------------------------------------ASYNCRONOUS CLASS--------------------------------------------->*/
	private class SyncronizedDataFile extends AsyncTask<Void, Void, DbxFileSystem>{
		private actualiceStatus mCall;
		private DbxFileSystem aux;
		public SyncronizedDataFile(actualiceStatus mCall){
			this.mCall=mCall;
		}
		@Override
		protected DbxFileSystem doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			if(mAccManager.hasLinkedAccount()){
				try {
					aux=DbxFileSystem.forAccount(mAccManager.getLinkedAccount());
					if(!aux.hasSynced())
						aux.syncNowAndWait();
				} catch (Unauthorized e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}catch (DbxException e2) {
					e2.printStackTrace();
				}
			}
			return aux;
		}
		@Override
		protected void onPostExecute(DbxFileSystem res) {
			if (res != null){
				getSession().setmDtFs(res,mCall);
			}
		}
	}
	/**<------------------------------------------SEARCH TASK-------------------------------------------------->
	 * The focus of this one is to search the books on the dropbox system. It's an asynchronous task to not 
	 * collapse the application.
	 * @author Antonio
	 *
	 */
	/**I use this interface to get the list of books when search finish*/
	public interface actualiceListBook{
		public void setListBook();
	}
	/**<----------------------------------------AUXILIARY METHODS--------------------------------------------->*/
	public void getFiles(DbxPath path,actualiceListBook mCall){
		try {
			if(mDtFs.hasSynced()){
				files=new SearchFiles(path,".epub",mCall);
				files.execute();
			}
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void CallForResults(ListResult completeList,actualiceListBook mCall) {
		List<DbBook> files = completeList.getFiles();
		if (!files.isEmpty()){
			for (DbBook dbBook : files) {
				if(!listBooks.contains(dbBook))
					listBooks.add(dbBook);
			}
			mCall.setListBook();
		}
	}
	/**<----------------------------------------ASYNCRONOUS CLASS--------------------------------------------->*/
	private class SearchFiles extends AsyncTask<Void, Void,List<DbBook>>{
		private List<DbxPath> mPaths;
		private String mExtension;
		private actualiceListBook mCall;
		
		public SearchFiles(DbxPath path,String extension,actualiceListBook mCall){
			mPaths=new ArrayList<DbxPath>();
			mPaths.add(path);
			mExtension=extension;
			this.mCall=mCall;
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
			CallForResults(new ListResult(listRes),mCall);
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
	/**<----------------------------------------AUXILIARY METHODS--------------------------------------------->*/
	public void DownloadBook(DbxPath path,ImageView image,showCoverBook call){
		DbxFileInfo info;
		try {
			info = mDtFs.getFileInfo(path);
			OpenDbBook(info,false,image,call);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void OpenDbBook(DbxFileInfo fileInfo,boolean sync,ImageView image,showCoverBook call) {
		openBook = new OpenBook(sync,image,call);
		openBook.execute(fileInfo);
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
	/**<----------------------------------------ASYNCRONOUS CLASS--------------------------------------------->*/
	private class OpenBook extends AsyncTask<DbxFileInfo, Void, Book>{
		private boolean mSync;
		private ImageView image;
		private showCoverBook mCall;
		public OpenBook(boolean sync,ImageView image,showCoverBook call){
			mSync = sync;
			this.image=image;
			this.mCall=call;
		}

		@Override
		protected Book doInBackground(DbxFileInfo... params) {
			DbxFileInfo fileInfo = params[0];
			Book book = null;
			DbxFile file = null;
			try {
				if (!mSync || getSession().isSyncronhized(fileInfo)){
					file = getSession().OpenFile(fileInfo.path);
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

		@Override
		protected void onPostExecute(Book result) {
			if (result != null){
				ShowBook(result,image,mCall);
			}
		}
	}
	/**<----------------------------------------------GET IMAGE TASK------------------------------------------>*/
	/**This interface is used to show the cover of the book*/
	public interface showCoverBook{
		public void showCover(ImageView iView,Bitmap result);
	}
	/**<----------------------------------------AUXILIARY METHODS--------------------------------------------->*/
	public void loadBitmap(ImageView imageView, InputStream input,showCoverBook call){
		cover = new GetCover(imageView,call);
		cover.execute(input);
	}
	public void ShowBook(Book book,ImageView cover,showCoverBook call){
		if (book != null) {
			if (book.getCoverImage() != null){
				try {
					loadBitmap(cover, book.getCoverImage().getInputStream(),call);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
	}
	/**<----------------------------------------ASYNCRONOUS CLASS--------------------------------------------->*/
	private class GetCover extends AsyncTask<InputStream, Void, Bitmap> {
		private WeakReference<ImageView> ref;
		private showCoverBook mCall;
		public GetCover (ImageView imageView,showCoverBook call) {
			ref = new WeakReference<ImageView>(imageView);
			this.mCall=call;
		}
		
		@Override
		protected Bitmap doInBackground(InputStream... params) {
			InputStream input = params[0];
			return BitmapFactory.decodeStream(input);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (ref != null && result != null) {
	            ImageView iView = ref.get();
	            if (iView != null) {
	                mCall.showCover(iView, result);
	            }
	        }
		}
	}
}
