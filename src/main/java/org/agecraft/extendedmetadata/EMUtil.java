package org.agecraft.extendedmetadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EMUtil {

	public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... params) throws Exception {
		Constructor<T> constructor = clazz.getDeclaredConstructor(params);
		constructor.setAccessible(true);
		return constructor;
	}

	public static Field getField(Class<?> clazz, String name, String srgName, String obfName) throws Exception {
		Field field = null;
		try {
			field = clazz.getDeclaredField(obfName);
		} catch(Exception e1) {
			try {
				field = clazz.getDeclaredField(srgName);
			} catch(Exception e2) {
				try {
					field = clazz.getDeclaredField(name);
				} catch(Exception e3) {
					throw e3;
				}
			}
		}
		field.setAccessible(true);
		return field;
	}

	public static Method getMethod(Class<?> clazz, String name, String srgName, String obfName, Class<?>... params) throws Exception {
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(obfName, params);
		} catch(Exception e1) {
			try {
				method = clazz.getDeclaredMethod(srgName, params);
			} catch(Exception e2) {
				try {
					method = clazz.getDeclaredMethod(name, params);
				} catch(Exception e3) {
					throw e3;
				}
			}
		}
		method.setAccessible(true);
		return method;
	}
}
