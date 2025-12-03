package com.techhispania.imperator.core.factories;

import com.techhispania.imperator.core.loader.Loader;
import com.techhispania.imperator.core.loader.LoaderImpl;

public class CoreFactory {

	public static Loader createLoader() {
		return new LoaderImpl();
	}
}
