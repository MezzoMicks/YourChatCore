package de.deyovi.chat.core.services;

import java.util.List;
import java.util.Locale;

public interface TranslatorService {

	/**
	 * Translates a Message for representation
	 * @param rawMessage
	 * @return translated Message
	 */
	String translate(String rawMessage, Locale locale);

	/**
	 * Translates a Message for representation
	 * @param parsedMessage
	 * @return translated Message
	 */
	String translate(List<String> parsedMessage, Locale locale);
	
	List<String> parse(String input);

}