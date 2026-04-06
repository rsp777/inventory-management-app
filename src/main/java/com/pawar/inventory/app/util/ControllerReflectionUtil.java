package com.pawar.inventory.app.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ControllerReflectionUtil {

	private static final ConcurrentMap<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();
	private static final Set<String> MISSING_METHOD_CACHE = ConcurrentHashMap.newKeySet();

	private ControllerReflectionUtil() {
	}

	public static <T> List<T> toList(Iterable<T> source) {
		List<T> result = new ArrayList<>();
		if (source != null) {
			source.forEach(result::add);
		}
		return result;
	}

	public static Object invokeGetter(Object source, String methodName) {
		if (source == null) {
			return null;
		}

		Method method = resolveMethod(source.getClass(), methodName);
		if (method == null) {
			return null;
		}

		try {
			return method.invoke(source);
		} catch (Exception ignored) {
			return null;
		}
	}

	public static Integer extractInt(Object source, String... methodNames) {
		if (source == null) {
			return null;
		}

		for (String methodName : methodNames) {
			Object value = invokeGetter(source, methodName);
			if (value instanceof Number number) {
				return number.intValue();
			}
			if (value instanceof String text && !text.isBlank()) {
				return Integer.valueOf(text.trim());
			}
		}

		return null;
	}

	public static String extractString(Object source, String... methodNames) {
		if (source == null) {
			return null;
		}

		for (String methodName : methodNames) {
			Object value = invokeGetter(source, methodName);
			if (value != null) {
				return String.valueOf(value);
			}
		}

		return null;
	}

	private static Method resolveMethod(Class<?> type, String methodName) {
		String cacheKey = type.getName() + "#" + methodName;
		Method cachedMethod = METHOD_CACHE.get(cacheKey);
		if (cachedMethod != null) {
			return cachedMethod;
		}
		if (MISSING_METHOD_CACHE.contains(cacheKey)) {
			return null;
		}

		try {
			Method resolvedMethod = type.getMethod(methodName);
			METHOD_CACHE.putIfAbsent(cacheKey, resolvedMethod);
			return resolvedMethod;
		} catch (NoSuchMethodException ignored) {
			MISSING_METHOD_CACHE.add(cacheKey);
			return null;
		}
	}
}
