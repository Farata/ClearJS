package com.farata.cdb.annotations.helper;

import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.hql.QueryTranslator;
import org.w3c.dom.Document;

public class HQLCompiler {
	private SessionFactoryImplementor factory;

	public SessionFactoryImplementor getFactory() {
		return factory;
	}

	public void setFactory(SessionFactoryImplementor factory) {
		this.factory = factory;
	}

	public HQLCompiler(Document confDocument) {
		factory = HQLHelper.createSessionFactoryFromDocument(confDocument);
	}

	public QueryTranslator compileQuery(String query) throws Exception {
		return HQLHelper.compileQuery(query);
	}
}