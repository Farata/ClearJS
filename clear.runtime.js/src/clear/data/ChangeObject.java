/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package clear.data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;


@SuppressWarnings("unchecked")
public class ChangeObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ChangeObject( ) {
		
	}

	public ChangeObject(Object newObject, Object oldObject ) {
		if ( newObject == null && oldObject == null) return;
		if ( newObject == null && oldObject != null) {
			state = 2;
			this.setPreviousVersion(oldObject);
		}
		if ( newObject != null && oldObject == null) {
			this.setNewVersion(newObject);
			state = 1;
		}
	}
	public void addChangedPropertyName(String propertyName) {
		if(!isUpdate()&&!isCreate()) throw new IllegalArgumentException("addChangedPropertyName applicable to update and create statuses only");
		// Allow identity changes from doCreate() methods, when initial changedNames is null
		if(changedNames == null)  changedNames=new String[]{};
        	
        for(int i = 0; i < changedNames.length; i++) {
           if(changedNames[i].equals(propertyName)) return;
        }    

        String tempChangedNames[] = new String[changedNames.length + 1];
        for(int i = 0; i < changedNames.length; i++) {
            tempChangedNames[i] = changedNames[i];
        }    

        tempChangedNames[changedNames.length] = propertyName;
        changedNames = tempChangedNames;
        changedValues = null; //getChangedValue will update the map
	}
	
	public void conflict(Object arg0) {
		// TODO Auto-generated method stub

	}

	public void conflict(Object arg0, boolean arg1) {
		// TODO Auto-generated method stub
	}
	
	public void fail() {
		state = 100;
	}

	public void fail(String desc) {
		// TODO Auto-generated method stub
		fail();
        error = desc;
	}
	
	public String[] getChangedPropertyNames() {
		// TODO Auto-generated method stub
		if(changedNames == null) {
			if ((newVersion != null) && (previousVersion!=null)){
				if (newVersion instanceof ChangeSupport) {
					changedNames = ((ChangeSupport)newVersion).getChangedPropertyNames(previousVersion);
				}
				// TODO offer default changedNames computation??
			}
		}
		return changedNames;
	}
	
	public Map getChangedValues()
    {
    	if ((newVersion==null) || (previousVersion==null)) return null;
        if(changedValues == null)
        {
            if(changedNames == null)
            	changedNames = getChangedPropertyNames();

            changedValues = new HashMap();
            for(int i = 0; i < changedNames.length; i++)
            {
                String field = changedNames[i];
                changedValues.put(field, getNewValue(field));
            }

        }
        return Collections.unmodifiableMap(changedValues);
    }
	
    public String getError() {
    	return error;
    }
    
	public Object getObjectId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Object getNewValue( String propertyName) {
		Object propertyValue = null;
		if (newVersion instanceof ChangeSupport) {
			if (newMap == null)
        		newMap = ((ChangeSupport)newVersion).getProperties();
			propertyValue = newMap.get( propertyName);
		} else {
			Class cls = newVersion.getClass();
			try {
			    Field field = cls.getField(propertyName);
			    propertyValue = field.get(newVersion);
			} catch (Exception e) {
				new RuntimeException("Unknown property '" + propertyName +"' for object of class " + cls.getName() );
			}
		}
		return propertyValue;
	}
	
	public Object getPreviousValue(String propertyName) {
		Object propertyValue = null;
		if (previousVersion instanceof ChangeSupport) {
			if (previousMap == null)
				previousMap = ((ChangeSupport)previousVersion).getProperties();
			propertyValue = previousMap.get( propertyName);
		} else {
			Class cls = previousVersion.getClass();
			try {
			    Field field = cls.getField(propertyName);
			    propertyValue = field.get(previousVersion);
			} catch (Exception e) {
				new RuntimeException("Unknown property '" + propertyName +"' for object of class " + cls.getName() );				
			}
		}
		return propertyValue;	
	}
	
	
	public boolean isCreate() {
		return state == 1;
	}

	public boolean isDelete() {
		return state == 3;
	}
	
	public boolean isUpdate() {
		return state == 2;
	}

    public void setChangedPropertyNames(String [] columns)
    {
    	changedNames = columns;
    	changedValues = null;
    }
	
    public String error ="";
  	public void setError(String s) {
    	error = s;
    }
    
	public Object getNewVersion() {
        return newVersion;
    }

	public Object newVersion = null;
	public void setNewVersion(Object obj) {
	    newVersion = obj; 
        changedValues = null;
	}
	
	public Object previousVersion = null;
	public Object getPreviousVersion() {
		return previousVersion;
	}
	public void setPreviousVersion(Object obj) {
	    previousVersion = obj;
	}
	
	public int state = 0;
	public int getState() {
		return state;
	}
	public void setState(int s) {
		state = s;
	}
	
	
//---------------------- E X T E N S I O N S--------------------------
   
	protected Map newMap = null;
	protected Map previousMap = null;
	protected String[] changedNames = null;
	protected Map changedValues = null;
}