package com.sanjoyghosh.company.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtils {

	public static File getLatestFileWithName(String fileNameRegex) {
		File[] files = new File(Constants.DOWNLOADS_FOLDER).listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().matches(fileNameRegex);
			}
		});
		if (files == null || files.length == 0) {
			return null;
		}
		
		List<File> fileList = Arrays.asList(files);
		Collections.sort(fileList, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return -(new Long(o1.lastModified()).compareTo(new Long(o2.lastModified())));
			}
		});
		return fileList.get(0);
	}
}
