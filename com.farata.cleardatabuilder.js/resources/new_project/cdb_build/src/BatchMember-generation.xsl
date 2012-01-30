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



import java.io.Serializable;
import java.util.List;

public class BatchMember implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String   methodName;
	public String   className;
	@SuppressWarnings("rawtypes")
	public List     parameters;
	public String autoSyncSubtopic;
	public Object   result;

	public String getClassName()	{
		return className;
	}
	
	public String getMethodName()	{
		return methodName;
	}

	
	@SuppressWarnings("rawtypes")
	public List getParameters()	{
		return parameters;
	}
	
	public String getAutoSyncSubtopic()	{
		return autoSyncSubtopic;
	}

	public Object getResult()	{
		return result;
	}
	public void setClassName(String s)	{
		className = s;
	}
	
	public void setMethodName(String s)	{
		methodName = s;
	}

	public void setAutoSyncSubtopic(String s)	{
		autoSyncSubtopic = s;
	}

	@SuppressWarnings("rawtypes")
	public void setParameters(List lst)	{
		parameters = lst;
	}

	public void setResult(Object obj)	{
		result = obj;
	}
	
	public final String __type__="batchMember";
	
}



	</xsl:template>
</xsl:stylesheet>