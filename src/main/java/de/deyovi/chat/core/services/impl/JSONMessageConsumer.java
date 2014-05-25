package de.deyovi.chat.core.services.impl;

import de.deyovi.chat.core.constants.ChatConstants;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.Segment.ContentType;
import de.deyovi.chat.core.objects.impl.CommandSegment;
import de.deyovi.chat.core.services.MessageConsumer;
import de.deyovi.chat.core.services.OutputService.OutputMeta;
import de.deyovi.chat.core.services.TranslatorService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Stateless
public class JSONMessageConsumer implements MessageConsumer {

	private final static Logger logger = LogManager.getLogger(JSONMessageConsumer.class);

    @Inject
	private TranslatorService translatorService;
	
	private JSONObject json = null;
    private List<JSONObject> jsonMessages = new LinkedList<JSONObject>();
	
	private boolean stop = false;
    private boolean refresh = false;
	
	public void consume(Segment[] segments, Locale locale, OutputMeta meta) {
		stop |= meta.isInterrupted();
        refresh |= meta.isRefreshRequired();
		try {
			JSONObject message = null;
            List<JSONObject> jsonSegments = new ArrayList<JSONObject>(segments.length);
			String profile = null;
			for (Segment seg : segments) {
				if (message == null) {
					message = new JSONObject();
					message.put("user", meta.getOrigin() != null ? meta.getOrigin().getUserName() : null);
				}
				String content = seg.getContent();
				JSONObject segment = new JSONObject();
                jsonSegments.add(segment);
				segment.put("type", seg.getType().toString());
				if (seg.getType() == ContentType.TEXT) {
					segment.put("content", content);
				} else if (seg.getType() == ContentType.COMMAND) {
                    if (((CommandSegment) seg).getChatCommand() == ChatConstants.ChatCommand.PROFILE) {
                        profile = ((CommandSegment) seg).getPayload();
                    }
                } else {
					segment.put("content", seg.getContent());
					segment.put("alt", seg.getAlternateName());
					segment.put("pinky", seg.getPinky());
					segment.put("preview", seg.getPreview());
				}
			}
			if (message != null) {
                message.put("segments", jsonSegments);
				message.put("open_profile", profile);
                jsonMessages.add(message);
			}
		} catch (JSONException e) {
			logger.error(e);
		}
	}
	
	@Override
	public void finish(OutputMeta meta) {
		refresh |= meta.isRefreshRequired();
		stop |= meta.isInterrupted();
		try {
            if (json == null && (!jsonMessages.isEmpty() || stop || refresh)) {
                json = new JSONObject();
                json.put("stop", stop);
                json.put("refresh", refresh);
                json.put("messages", jsonMessages);
            }
        } catch (JSONException e) {
			logger.error(e);
		}
	}
	
	@Override
	public String getResult() {
		return json != null ? json.toString() : null;
	}

}
