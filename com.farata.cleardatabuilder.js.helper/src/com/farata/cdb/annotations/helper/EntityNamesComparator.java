package com.farata.cdb.annotations.helper;

import java.util.Comparator;

public class EntityNamesComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		if(o1 == null || o2 == null) {
			return 0;
		}
		if(o1.length() > o2.length()) {
			return -1;
		} else {
			return 1;
		}
	}
}