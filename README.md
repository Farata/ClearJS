Clear Data Builder for Ext JS
=======

Clear Data Builder for Ext JS (CDB) is an open source and free productivity tool to build applications with HTML5, JavaScript and Java EE technologies. 

It supports [Ext JS](http://www.sencha.com/products/extjs/) with [Ext.Direct](http://www.sencha.com/products/extjs/extdirect)/JEE. 

CDB-H writes both JavaScript and Java code for you. All artifacts required for bringing together Ext JS, Java implementations of [ Ext.Direct](http://www.sencha.com/products/extjs/extdirect) (currently - [DirectJNGine](http://code.google.com/p/directjngine/)), [BlazeDS](http://opensource.adobe.com/wiki/display/blazeds/BlazeDS), and popular Java frameworks: [Spring](http://www.springsource.org/), [Hibernate](http://www.hibernate.org/docs), and [MyBatis](http://mybatis.org/) CDB-H generates automatically. 

[Source code](https://cleartoolkit.svn.sourceforge.net/svnroot/cleartoolkit/trunk/) is hosted on the SourceForge as part of the [Clear Toolkit Project](https://sourceforge.net/projects/cleartoolkit/) under [MIT license](http://www.opensource.org/licenses/mit-license.php). Commercial support of CDB-H is available from the source: [Farata Systems](http://www.faratasystems.com). 

## Why Clear Data Builder?

If your ever tried to create a combined Ext-JEE-DBMS project with or without Spring, you spent hours just to see a window populated with the simple data. Implementing data persistence and transaction processing is even more time consuming. How about batching all inserts, deletes and and updates done to your store as a single transaction? Want to incorporate changes to the associated stores to the same _sync()_? CDB-H substantially saves time by automating most of these efforts. 

You go through a set of simple steps to _declare_ what data do you need (using SQL or Hibernate) and let CDB-H _generate_ and _deploy_ all the artifacts: Java, JavaScript and configuration files. Below, _getCompanyAssociates_ is annotated to return all records from a _company_associate_ table1) and this declaration is all you write to build a BlazeDS service that returns a collection of employee data ([Run view-source enabled demo)](): 


  package com.farata.test.service;
	import java.util.List;
	import com.farata.test.entity.CompanyAssociate;
	import clear.cdb.annotations.*;
 
	@JSService
	public interface ICompanyService {
		@JSJPQL(query="SELECT a FROM CompanyAssociate a WHERE companyId=:companyId")
		List<CompanyAssociate> getCompanyAssociated(Integer companyId);
	}

Throw in one more Java annotation to generate a sample HTML application:

	@JSGenerateHTML5(arguments="1")
	@JSJPQLMethod(query="SELECT a FROM CompanyAssociate a WHERE companyId=:companyId")
	List<CompanyAssociate> getCompanyAssociated(Integer companyId);
