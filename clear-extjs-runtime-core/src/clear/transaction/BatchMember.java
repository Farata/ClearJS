/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package clear.transaction;
import java.io.Serializable;
import java.util.*;

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
