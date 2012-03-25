package com.engage.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.engage.exception.EngageException;

import flex.data.ChangeObject;

public class DTOUtil {

	private static final Logger logger = Logger.getLogger(DTOUtil.class);
	
	/**
	 * Converts java bean based DTO to string showing class name and properties 
	 * with values.
	 * For example:
	 *   UserDTO {id: '123', first_name: 'Alex', ... }
	 *   
	 * @param obj - object to convert to String.
	 * @return - object string representation.
	 */
	@SuppressWarnings("unchecked")
	public static String toString(Object obj) {
		try {
			Map<String, Object> props = PropertyUtils.describe(obj);
			StringBuilder b = new StringBuilder();
			b.append(obj.getClass().getName()).append(" { ");
			int count = 0;
			for (String field : props.keySet()) {
				if (count++ != 0) {
					b.append(", ");
				}
				b.append(field).append(": '").append(
						PropertyUtils.getProperty(obj, field))
						.append("'");
			}
			b.append("}");
			return b.toString();
		} catch (IllegalAccessException e1) {
		    logger.error(ExceptionUtils.getStackTrace(e1));
		} catch (InvocationTargetException e1) {
		    logger.error(ExceptionUtils.getStackTrace(e1));
		} catch (NoSuchMethodException e1) {
		    logger.error(ExceptionUtils.getStackTrace(e1));
		}
		return "";
	}

	/**
	 * Copy specified properties from source to destination. Properties values 
	 * should be of the same type.
	 *  
	 * If no properties provided then copy property values from the src bean to 
	 * the destination bean for all cases where the property names are the same.
	 *  
	 * @param src - source bean.
	 * @param dest - destination bean.
	 * @param properties - properties list.
	 */
	public static void copyProperties(Object src, Object dest, 
			String...properties) {
		try {
			if (properties.length == 0) {
				PropertyUtils.copyProperties(dest, src);
			}
			else {
				for (String name : properties) {
					PropertyUtils.setProperty(src, name, 
						PropertyUtils.getProperty(dest, name));
				}
			}
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
	
}
