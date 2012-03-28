package clear.djn.servlet;

import java.util.Map;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * Helper used to retrieve bean in spring context
 * 
 */
public class SpringLoaderHelper {

	/**
	 * Allow to autowired a non spring instanciated object
	 * 
	 * @param objToLoad
	 * @param prefixBeanName
	 */
	public static void autowireBean(Object objToLoad, String prefixBeanName) {
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ContextLoader
				.getCurrentWebApplicationContext().getAutowireCapableBeanFactory();
		beanFactory.autowireBeanProperties(objToLoad, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
		beanFactory.initializeBean(objToLoad, prefixBeanName);
	}

	/**
	 * Return the spring instance of a bean from is className.
	 * 
	 * Return null if no bean found.
	 * 
	 * @param instanceClass
	 * 
	 * @return
	 */
	public static Object getBeanOfType(Class<?> instanceClass) {
		Object bean = null;
		Map<String, ?> beansMap = getBeansOfType(instanceClass);
		if (beansMap == null) {
			return null;
		} else if (beansMap.size() > 1) {
			throw new IllegalStateException("Only one instance of " + instanceClass + "is expected");
		} else if (beansMap.size() == 1) {
			bean = beansMap.values().iterator().next();
		}
		return bean;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, ?> getBeansOfType(Class<?> instanceClass) {
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		return context == null ? null : context.getBeansOfType(instanceClass);
	}
}
