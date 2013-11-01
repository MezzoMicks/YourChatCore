package de.deyovi.chat.core.services;

import java.util.Locale;

import org.json.JSONObject;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Message;

public interface OutputService {

	public OutputMeta processMessages(Message[] message, Locale locale, MessageConsumer consumer);
	
	public JSONObject getRefreshData(ChatUser user, Locale locale);
	
	public interface OutputMeta {
		
		public ChatUser getOrigin();
		
		public boolean isRefreshRequired();
		
		public boolean isInterrupted();
		
		public int interruptionReason();
		
	}
	
}
