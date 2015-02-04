package de.deyovi.chat.facades;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.services.MessageConsumer;
import de.deyovi.chat.core.services.OutputService.OutputMeta;
import org.json.JSONObject;

import java.util.Locale;

public interface OutputFacade {

	OutputMeta listen(MessageConsumer consumer, Locale locale, ChatUser user, String listenId);

	JSONObject refresh(ChatUser user, Locale locale);

    String register(ChatUser user);

}