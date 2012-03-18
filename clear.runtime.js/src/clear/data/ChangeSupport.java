package clear.data;

import java.util.Map;

public interface ChangeSupport {

	String[] getChangedPropertyNames(Object previousVersion);

	Map getProperties();

}
