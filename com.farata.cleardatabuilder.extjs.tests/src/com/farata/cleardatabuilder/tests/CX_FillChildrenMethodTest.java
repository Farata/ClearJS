package com.farata.cleardatabuilder.tests;

import org.apache.tools.ant.Project;

public class CX_FillChildrenMethodTest extends BaseTest {
	public void testEntityDTOs() {
		executeCDBBuild("CX_FillChildrenMethod", Project.MSG_INFO);
	}
}