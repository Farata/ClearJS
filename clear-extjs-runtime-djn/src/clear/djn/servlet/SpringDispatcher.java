package clear.djn.servlet;

import java.util.HashMap;
import java.util.Map;

import com.softwarementors.extjs.djn.api.RegisteredAction;
import com.softwarementors.extjs.djn.servlet.ssm.SsmDispatcher;

/**
 * Dispatcher used to retrieve DirectAction
 * 
 */
public class SpringDispatcher extends SsmDispatcher {

	private final Map<Class<?>, Object> mapClassBeanName = new HashMap<Class<?>, Object>();

	@Override
	protected Object createActionInstance(RegisteredAction action) {
		Object actionInstance = null;
		try {
			if (this.mapClassBeanName.containsKey(action.getActionClass())) {
				actionInstance = this.mapClassBeanName.get(action.getActionClass());
			} else {
				actionInstance = SpringLoaderHelper.getBeanOfType(action.getActionClass());
				if (actionInstance == null) {
					actionInstance = action.getActionClass().newInstance();
				}
				this.mapClassBeanName.put(action.getActionClass(), actionInstance);
			}
		} catch (Exception e) {
			throw createUnableToCreateActionInstanceException(action, e);
		}

		return actionInstance;
	}
}