package com.example.test;

import java.util.List;
import com.dropbox.sync.android.DbxPath;
import com.example.test.domain.DbBook;

/**
 * 
 * @author cjuega
 *
 * Simple helper class used by {@link DropboxManager.DropboxListingTask DropboxListingTask} to pass both 
 * the list of files found and the list of folders that are not explore yet.
 *
 */
public class ListResult {

	private List<DbxPath> mPaths;
	private List<DbBook> mFiles;
	
	public ListResult(List<DbxPath> paths, List<DbBook> files){
		mPaths = paths;
		mFiles = files;
	}
	
	public List<DbxPath> getPaths() {
		return mPaths;
	}
	public List<DbBook> getFiles() {
		return mFiles;
	}
}

