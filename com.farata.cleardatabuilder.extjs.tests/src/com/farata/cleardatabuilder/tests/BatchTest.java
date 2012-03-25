package com.farata.cleardatabuilder.tests;

import java.io.File;

import org.apache.tools.ant.Project;

public class BatchTest extends BaseTest {
	public void testBatch() {
		File scenariosFolder = new File("scenarios");
		File[] scenarios = scenariosFolder.listFiles();
		for (File scenario : scenarios) {
			if (!scenario.getName().equals(".svn") && !scenario.getName().equals(".DS_Store")) {
				executeCDBBuild(scenario.getName(), Project.MSG_INFO);
				//executeCDBSpringBuild(scenario.getName(), Project.MSG_INFO);
			}
		}
	}
}
