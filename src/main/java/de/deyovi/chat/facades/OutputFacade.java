package de.deyovi.chat.facades;

import java.util.Locale;

import org.json.JSONObject;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.services.MessageConsumer;
import de.deyovi.chat.core.services.OutputService.OutputMeta;

public interface OutputFacade {

	public OutputMeta listen(MessageConsumer consumer, Locale locale, ChatUser user, String listenId);

	public JSONObject refresh(ChatUser user, Locale locale);

}