package de.deyovi.chat.core.services;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Message;
import org.json.JSONObject;

import java.util.Locale;

public interface OutputService {

	OutputMeta processMessages(Message[] message, Locale locale, MessageConsumer consumer);
	
	JSONObject getRefreshData(ChatUser user, Locale locale);
	
	public interface OutputMeta {
		
		ChatUser getOrigin();
		
		boolean isRefreshRequired();
		
		boolean isInterrupted();
		
		int interruptionReason();
		
	}
	
}
