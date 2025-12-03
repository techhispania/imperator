package com.techhispania.imperator.core.loader;

import java.io.IOException;

public interface Loader {

	/**
	 * Loads the controller classes and initialize the
	 * http server
	 * 
	 * @throws IOException
	 */
	void run() throws IOException;
}
