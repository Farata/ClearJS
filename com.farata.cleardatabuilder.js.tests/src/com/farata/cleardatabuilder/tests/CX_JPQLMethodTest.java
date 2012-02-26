package com.farata.cleardatabuilder.tests;

import org.apache.tools.ant.Project;

public class CX_JPQLMethodTest extends BaseTest {
	public void testJPQLMethod() {
		executeCDBBuild("CX_JPQLMethod", Project.MSG_INFO);
		//executeCDBSpringBuild("CX_JPQLMethod", Project.MSG_INFO);
	}
}
