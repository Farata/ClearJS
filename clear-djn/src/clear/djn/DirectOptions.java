package clear.djn;

import java.util.HashMap;
import java.util.Map;

public class DirectOptions {
	
	private static ThreadLocal<Object> s_tlsOptions = new ThreadLocal<Object>();
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getOptions() {
		return (Map<String, Object>)s_tlsOptions.get();
	}
	public static void setOptions(Map<String, Object> options) {
		s_tlsOptions.set(options);
	}
	public static Object getOption(String key) {
		if (key==null) return null;
		Object option = null;
		Map<String, Object> options = getOptions();
		if (options!=null) {
			option = options.get(key);
		}
		return option;
	}
	public static Object setOption(String key, Object value){
		if (key==null) return null;
		Map<String, Object> options = getOptions();
		if (options==null) {
			options=new HashMap<String, Object>(); 
		}
		Object oldOption = options.get(key);
		options.put(key, value);
		return oldOption;
	}
	
}
