package com.farata.cdb.annotations.helper;

import java.util.Comparator;

import org.hibernate.hql.ParameterTranslations;

public class HQLParamersComparator implements Comparator<String> {

	private ParameterTranslations params;

	public HQLParamersComparator(ParameterTranslations params) {
		this.params = params;
	}

	@Override
	public int compare(String o1, String o2) {
		int[] loc1 = params.getNamedParameterSqlLocations(o1);
		int[] loc2 = params.getNamedParameterSqlLocations(o2);
		return loc1[0] < loc2[0]? -1 : 1;
	}

}
