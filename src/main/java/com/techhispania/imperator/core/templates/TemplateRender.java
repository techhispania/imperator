package com.techhispania.imperator.core.templates;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

public class TemplateRender {

	private static final PebbleEngine engine = new PebbleEngine.Builder().loader(new ClasspathLoader()).build();

	/**
	 * Method used to process the html templates and replace the placeholders with the 
	 * variables stored in the model Map.
	 * 
	 * @param templatePath The path of the html template to be parsed
	 * @param model The Map that contains the values of the placeholders to be replaced
	 * @return The html with all the placeholders replaced
	 * @throws Exception
	 */
	public static String render(String templatePath, Map<String, Object> model) throws Exception {
		PebbleTemplate compiledTemplate = engine.getTemplate(templatePath);
		Writer writer = new StringWriter();
		compiledTemplate.evaluate(writer, model);
		return writer.toString();
	}
}
