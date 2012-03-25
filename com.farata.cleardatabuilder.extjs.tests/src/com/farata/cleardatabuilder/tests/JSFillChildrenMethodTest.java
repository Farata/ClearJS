package com.farata.cleardatabuilder.tests;

import org.apache.tools.ant.Project;

public class JSFillChildrenMethodTest extends BaseTest {
	public void testEntityDTOs() {
		executeCDBBuild("JSFillChildrenMethod", Project.MSG_INFO);
	}
}