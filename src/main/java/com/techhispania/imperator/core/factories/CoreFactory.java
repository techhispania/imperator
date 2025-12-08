package com.techhispania.imperator.core.factories;

import com.techhispania.imperator.core.handlers.HandleFormParams;
import com.techhispania.imperator.core.handlers.HandleStandardFormParamsImpl;
import com.techhispania.imperator.core.handlers.HandleUploadFileFormParamsImpl;
import com.techhispania.imperator.core.loader.Loader;
import com.techhispania.imperator.core.loader.LoaderImpl;
import com.techhispania.imperator.core.reflection.MethodsExecutor;
import com.techhispania.imperator.core.reflection.MethodsExecutorImpl;
import com.techhispania.imperator.core.reflection.ReflectionUtils;
import com.techhispania.imperator.core.reflection.ReflectionUtilsImpl;

public class CoreFactory {

	public static Loader createLoader() {
		return new LoaderImpl();
	}
	
	public static HandleFormParams createHandleUpload() {
		return new HandleUploadFileFormParamsImpl();
	}
	
	public static HandleFormParams createHandleStandardFormParams() {
		return new HandleStandardFormParamsImpl();
	}
	
	public static MethodsExecutor createMethodsExecutor() {
		return new MethodsExecutorImpl();
	}
	
	public static ReflectionUtils createReflectionUtils() {
		return new ReflectionUtilsImpl();
	}
}
