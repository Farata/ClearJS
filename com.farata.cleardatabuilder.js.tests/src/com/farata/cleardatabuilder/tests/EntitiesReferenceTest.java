package com.farata.cleardatabuilder.tests;

import org.apache.tools.ant.Project;

public class EntitiesReferenceTest extends BaseTest {
	public void testEntityDTOs() {
		executeCDBBuild("EntitiesReference", Project.MSG_INFO);
	}
}
