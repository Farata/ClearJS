<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper"
	xmlns:redirect="org.apache.xalan.lib.Redirect"
	extension-element-prefixes="redirect" exclude-result-prefixes="xalan redirect">

	<xsl:output omit-xml-declaration="yes" method="text" />

	<xsl:param name="package" />
	
	
	<xsl:template match="/">
	
package <xsl:value-of select="helper:replaceAll($package, '/', '.')"/>;

import java.util.ArrayList;
import java.util.Map;

import java.util.List;


import com.google.gson.Gson;
import com.softwarementors.extjs.djn.api.RegisteredAction;
import com.softwarementors.extjs.djn.api.RegisteredStandardMethod;
import com.softwarementors.extjs.djn.api.Registry;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;

import clear.transaction.UserTransactionManager;



import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;
import com.softwarementors.extjs.djn.router.processor.RequestException;


public class BatchGateway {


	
	public List&lt;BatchMember&gt; executeTransactionBatch(List&lt;BatchMember&gt; items) {

	
		RequestRouter router = RequestRouter.getRequestRouter();
		Registry registry = router.getRegistry();
		Dispatcher dispatcher = router.getDispatcher();
		Gson gson = new Gson();
		
		
		List&lt;BatchMember&gt; results = new ArrayList&lt;BatchMember&gt;(items.size());
		
		for (int i = 0; i &lt; items.size(); i++) {
			Object oo = items.get(i);
			Map&lt;String, String&gt; mapItem = (Map&lt;String, String&gt;)items.get(i);
			String jsonItem = gson.toJson(mapItem);
			BatchMember batchMember = gson.fromJson(jsonItem, BatchMember.class);
			RegisteredAction action = registry.getAction(batchMember.className); 
			if( action == null ) {
			      throw RequestException.forActionNotFound(batchMember.className);
			}

			RegisteredStandardMethod method = action.getStandardMethod(batchMember.methodName);
			if( method == null ) {
				throw RequestException.forActionMethodNotFound( action.getName(), batchMember.methodName );
			}
		
			batchMember.result = dispatcher.dispatch(method, batchMember.parameters.toArray());
			results.add(batchMember);
		}	
		
		return results;
	}
	
	@DirectMethod
	public List execute(List batch) throws Throwable {		
		List result = new ArrayList();
		try {
			UserTransactionManager.joinUserTransaction();
			result = executeTransactionBatch(batch);
			UserTransactionManager.commitUserTransaction();
			return result;
		} catch (Throwable e) {
			UserTransactionManager.rollbackUserTransaction();
			
			throw e;
		}
	}
	
}


	</xsl:template>
</xsl:stylesheet>