package com.farata.cleardatabuilder.tests;

import org.apache.tools.ant.Project;

public class JSJPQLMethodTest extends BaseTest {
	public void testJPQLMethod() {
		executeCDBBuild("JSJPQLMethod", Project.MSG_INFO);
		//executeCDBSpringBuild("JSJPQLMethod", Project.MSG_INFO);
	}
}
