package com.example.test;

import java.io.File;
import java.io.FilenameFilter;

public class FilterFile implements FilenameFilter{
	public FilterFile(){
	}

	@Override
	public boolean accept(File dir, String filename) {
		// TODO Auto-generated method stub
		return filename.endsWith(".epub") ;
	}

}
