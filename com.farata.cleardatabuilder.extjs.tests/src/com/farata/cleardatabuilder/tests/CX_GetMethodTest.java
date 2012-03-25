package com.farata.cleardatabuilder.tests;

import org.apache.tools.ant.Project;

public class CX_GetMethodTest extends BaseTest {
	public void testEntityDTOs() {
		executeCDBBuild("CX_GetMethod", Project.MSG_INFO);
	}
}
