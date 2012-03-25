package com.farata.cleardatabuilder.tests;

import org.apache.tools.ant.Project;

public class JSGetMethodTest extends BaseTest {
	public void testEntityDTOs() {
		executeCDBBuild("JSGetMethod", Project.MSG_INFO);
	}
}
