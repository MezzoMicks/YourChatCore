package de.deyovi.chat.core.services;

import java.util.List;
import java.util.Locale;

public interface TranslatorService {

	/**
	 * Translates a Message for representation
	 * @param rawMessage
	 * @return translated Message
	 */
	public String translate(String rawMessage, Locale locale);

	/**
	 * Translates a Message for representation
	 * @param parsedMessage
	 * @return translated Message
	 */
	public String translate(List<String> parsedMessage, Locale locale);
	
	public List<String> parse(String input);

}