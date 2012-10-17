package clear.djn.servlet;

import java.util.HashMap;
import java.util.Map;

import com.softwarementors.extjs.djn.api.RegisteredMethod;
import com.softwarementors.extjs.djn.servlet.ssm.SsmDispatcher;

/**
 * Dispatcher used to retrieve DirectAction
 * 
 */
public class SpringDispatcher extends SsmDispatcher {

	private final Map<Class<?>, Object> mapClassBeanName = new HashMap<Class<?>, Object>();

	@Override
	protected Object getInvokeInstanceForNonStaticMethod(RegisteredMethod method) throws Exception {
		Object actionInstance = null;
		if (this.mapClassBeanName.containsKey(method.getActionClass())) {
			actionInstance = this.mapClassBeanName.get(method.getActionClass());
		} else {
			actionInstance = SpringLoaderHelper.getBeanOfType(method.getActionClass());
			if (actionInstance == null) {
				actionInstance = method.getActionClass().newInstance();
			}
			this.mapClassBeanName.put(method.getActionClass(), actionInstance);
		}
		return actionInstance;
	}
}