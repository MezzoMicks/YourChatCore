package de.deyovi.chat.core.services.impl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.Segment.ContentType;
import de.deyovi.chat.core.services.MessageConsumer;
import de.deyovi.chat.core.services.OutputService.OutputMeta;
import de.deyovi.chat.core.services.TranslatorService;

public class JSONMessageConsumer implements MessageConsumer {

	private final static Logger logger = LogManager.getLogger(JSONMessageConsumer.class);

	private final TranslatorService translatorService = ResourceTranslatorService.getInstance();
	
	private JSONObject json = null;
	
	private boolean stop = false;
	
	public void consume(Segment[] segments, Locale locale, OutputMeta meta) {
		stop |= meta.isInterrupted();
		try {
			JSONObject message = null;;
			String profile = null;
			for (Segment seg : segments) {
				if (message == null) {
					message = new JSONObject();
					message.put("user", meta.getOrigin() != null ? meta.getOrigin().getId() : null);
				}
				String content = seg.getContent();
				JSONObject segment = new JSONObject();
				segment.put("type", seg.getType().toString());
				if (seg.getType() == ContentType.TEXT) {
					if (content.charAt(0) == '$') {
						if (content.startsWith("$PROFILE_OPEN")) {
							List<String> parsedMessage = translatorService.parse(content);
							profile = parsedMessage.get(1);
							content = translatorService.translate(parsedMessage, locale);
						} else {
							content = translatorService.translate(content, locale);
						}		
					}
					segment.put("content", content);
				} else {
					segment.put("content", seg.getContent());
					segment.put("alt", seg.getAlternateName());
					segment.put("pinky", seg.getPinky());
					segment.put("preview", seg.getPreview());
				}
				message.putOpt("segments", segment);
			}
			if (message != null) {
				message.put("open_profile", profile);
				if (json == null) {
					json = new JSONObject();
				}
				json.putOpt("messages", message);
			}
		} catch (JSONException e) {
			logger.error(e);
		}
	}
	
	@Override
	public void finish() {
		try {
			json.put("stop", stop);
		} catch (JSONException e) {
			logger.error(e);
		}
	}
	
	@Override
	public String getResult() {
		return json != null ? json.toString() : null;
	}

}
