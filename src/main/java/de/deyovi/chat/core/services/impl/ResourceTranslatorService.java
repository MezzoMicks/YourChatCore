package de.deyovi.chat.core.services.impl;

import de.deyovi.chat.core.services.TranslatorService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

@Service
public class ResourceTranslatorService implements TranslatorService {

	private final static Logger logger = LogManager.getLogger(ResourceTranslatorService.class);

	private final Map<Locale, ResourceBundle> lang2resource = new HashMap<Locale, ResourceBundle>();
	
	private ResourceBundle getResource(Locale locale) {
		ResourceBundle instance = lang2resource.get(locale);
		if (instance == null) {
			instance = createInstance(locale);
		}
		return instance;
	}
	
	private synchronized ResourceBundle createInstance(Locale locale) {
		ResourceBundle instance = lang2resource.get(locale);
		if (instance == null) {
			instance = ResourceBundle.getBundle("de.deyovi.chat.messages", locale);
			lang2resource.put(locale, instance);
		}
		return instance;
	}

	public List<String> parse(String input) {
		LinkedList<String> result = new LinkedList<String>();
		String main = null;
		StringTokenizer tokenizer = new StringTokenizer(input, "{");
		while (tokenizer.hasMoreTokens()) {
			String paramString = tokenizer.nextToken();
			if (main == null) {
				main = paramString;
			} else {
				paramString = paramString.substring(0, paramString.length() - 1);
				int ixOfequals = paramString.indexOf('=');
				if (ixOfequals == -1) {
					result.add(null);
				} else {
					String unescaped = StringEscapeUtils.unescapeHtml4(paramString.substring(ixOfequals + 1));
					result.add(unescaped);
				}
			}
		}
		// Keine Parameter da gewesen, dann vollen String als Kommando nutzen
		result.addFirst(main);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see de.yovi.chat.client.TranslatorService#translate(java.lang.String, java.lang.String)
	 */
	@Override
	public String translate(String rawMessage, Locale locale) {
		// parse the input
		return translate(parse(rawMessage), locale);
	}
	
	@Override
	public String translate(List<String> arguments, Locale locale) {
		ResourceBundle messages = getResource(locale);
		// remove the first element (it's the message-id)
		String messageID = arguments.remove(0);
		String message = messages.getString(messageID.substring(1));
		// create a formatter with our message
		MessageFormat formatter = new MessageFormat(message, messages.getLocale());
		// and pass the remaining arguments to the function
		String output = formatter.format(arguments.toArray());
		// let's restore NewLines, if some where in the Message-Pattern
		output = StringEscapeUtils.unescapeJava(output);
		if (logger.isDebugEnabled()) {
			logger.debug("translated '" + messageID + "' to '" + output + "'");
		}
		return output;
	}
	
}
