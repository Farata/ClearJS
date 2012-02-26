package com.farata.cleardatabuilder.tests;

import org.apache.tools.ant.Project;

public class CX_FillMethodTest extends BaseTest {
	public void testJPQLMethod() {
		executeCDBBuild("CX_FillMethod", Project.MSG_INFO);
	}
}
