package clear.transaction.identity;

import java.util.HashMap;
import java.util.Map;

public class PropertyRack {
	private static ThreadLocal<Map<String, Object>>	s_tlsEntityMap	= new ThreadLocal<Map<String, Object>>() {
																		protected synchronized Map<String, Object> initialValue() {
																			return new HashMap<String, Object>();
																		}
																	};

	public static Object getEntity(	String domain,
									String property,
									Object oldValue) {
		Map<String, Object> map = s_tlsEntityMap.get();
		String key = domain + "_" + property + "_" + oldValue;
		Object newValue = map.get(key);
		return (newValue == null) ? oldValue : newValue;
	}

	public static void setEntity(	String domain,
									String property,
									Object oldValue,
									Object newValue) {
		String key = domain + "_" + property + "_" + oldValue;
		Map<String, Object> map = s_tlsEntityMap.get();
		map.put(key, newValue);
	}
}