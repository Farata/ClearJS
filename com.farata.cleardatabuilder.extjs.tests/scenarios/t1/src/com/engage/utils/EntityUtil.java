package com.engage.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.engage.exception.EngageException;

public class EntityUtil {

	private static final Logger logger = Logger.getLogger(EntityUtil.class);

	/**
	 * Convert from Oracle boolean database field to boolean.
	 * @param value - Oracle string representing boolean value.
	 * @return boolean value.
	 */
	public static boolean getBooleanValue(String value) {
		return (value != null) && value.equals("Y"); 
	}
	
	/**
	 * Convert from boolean to Oracle string representing boolean value.
	 * @param value - boolean value.
	 * @return Oracle string representing boolean value.
	 */
	public static String getBooleanValue(boolean value) {
		return value ? "Y" : "N";
	}
	
	/**
	 * Create map with specified property value used as a key.
	 * @param <T> - bean type.
	 * @param list - list.
	 * @param keyPropertyName - key property name must be Long type.
	 * @return generated map.
	 */
	public static <T, K> Map<K, T> getIdMap(List<T> list, 
			String keyPropertyName) {
		try {
			Map<K, T> result = new HashMap<K, T>();
			for (T bean : list) {
				result.put((K)PropertyUtils.getProperty(bean, keyPropertyName), 
					bean);
			}
			return result;
		}
		catch (NoSuchMethodException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new EngageException(e);
		} 
		catch (InvocationTargetException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new EngageException(e);
		} 
		catch (IllegalAccessException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new EngageException(e);
		}
	}
	
	public static Long getZeroNull(Long value) {
		return value != null && value == 0 ? null : value;
	}
}
