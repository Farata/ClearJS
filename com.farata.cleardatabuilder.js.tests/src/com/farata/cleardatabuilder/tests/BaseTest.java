package com.farata.cleardatabuilder.tests;

import org.apache.tools.ant.BuildFileTest;

public class BaseTest extends BuildFileTest {

	protected void executeCDBBuild(String name, int msgInfo) {
		System.out.println("=========== Start build '" + name + "'");
		long start = System.currentTimeMillis();
		configureProject("src/prepare.xml", msgInfo);
		project.setProperty("scenario.folder", name);
		executeTarget("main");
		configureProject(".tmp/cdb_build/build.xml", msgInfo);
		executeTarget("main");
		long end = System.currentTimeMillis();
		System.out.println("=========== End build '" + name + "'. Duration: "
				+ (end - start));
	}

	protected void executeCDBSpringBuild1(String name, int msgInfo) {
		System.out.println("=========== Start build '" + name + "' (Spring)");
		long start = System.currentTimeMillis();
		configureProject("src/prepare-spring.xml", msgInfo);
		project.setProperty("scenario.folder", name);
		executeTarget("main");
		configureProject(".tmp/cdb_build/build.xml", msgInfo);
		executeTarget("main");
		long end = System.currentTimeMillis();
		System.out.println("=========== End build '" + name + "' (Spring). Duration: "
				+ (end - start));
	}
}