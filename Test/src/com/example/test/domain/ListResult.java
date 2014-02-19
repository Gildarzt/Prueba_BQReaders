package com.example.test.domain;

import java.util.List;
/**Auxiliary class to store the list of files to the search
 * 
 * @author Antonio
 *
 */
public class ListResult {
	private List<DbBook> mFiles;
	public ListResult(List<DbBook> files){
		mFiles = files;
	}
	public List<DbBook> getFiles() {
		return mFiles;
	}
}

