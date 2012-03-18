package clear.transaction.identity;
import java.util.HashMap;
import java.util.Map;

public class IdentityRack  {
	private static ThreadLocal<Map<String, Long >> s_tlsIdentityMap = new ThreadLocal<Map<String,Long>>(){
		@Override
        protected Map<String, Long> initialValue() {
            return new HashMap<String, Long>();
        }
	};
		
	public static long getIdentity(String domain, long tempIdentity)	{
		Map<String, Long> map = s_tlsIdentityMap.get();
		String key = domain + "_" + tempIdentity;
		Long identity = (Long)map.get(key);		
		return ((identity==null)?tempIdentity:identity.longValue());
}
	public static void setIdentity(String domain, long tempIdentity, long identity )	{
		String key = domain + "_" + tempIdentity;
		Map<String, Long> map = s_tlsIdentityMap.get();
		map.put(key, new Long(identity));
	}

}
