package de.deyovi.chat.core.services;

import java.util.Locale;

public interface TranslatorService {

	/**
	 * Translates a Message for representation
	 * @param rawMessage
	 * @return translated Message
	 */
	public abstract String translate(String rawMessage, Locale locale);

}