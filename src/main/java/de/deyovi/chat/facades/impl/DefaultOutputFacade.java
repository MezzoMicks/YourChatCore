package de.deyovi.chat.facades.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.deyovi.chat.core.services.RoomService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import de.deyovi.chat.core.constants.ChatConstants.MessagePreset;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Message;
import de.deyovi.chat.core.objects.impl.SystemMessage;
import de.deyovi.chat.core.services.MessageConsumer;
import de.deyovi.chat.core.services.OutputService;
import de.deyovi.chat.core.services.OutputService.OutputMeta;
import de.deyovi.chat.core.services.impl.DefaultOutputService;
import de.deyovi.chat.facades.OutputFacade;

import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class DefaultOutputFacade implements OutputFacade {

	private static final Logger logger = LogManager.getLogger(DefaultOutputFacade.class);
	private static final Message[] NO_MESSAGES = new Message[0];
	@Inject
    private OutputService service;
    @Inject
    private RoomService roomService;

    /*
     * (non-Javadoc)
     *
     * @see
     * de.deyovi.chat.facades.impl.OutputFacade#listen(java.lang.Appendable,
     * java.util.Locale, boolean, de.deyovi.chat.core.objects.ChatUser,
     * java.lang.String)
     */
	@Override
	public OutputMeta listen(MessageConsumer consumer, Locale locale, ChatUser user, String listenId) {
		Message[] messages = listen(user, listenId);
		return service.processMessages(messages, locale, consumer);
	}

	private Message[] listen(ChatUser user, String listenId) {
		logger.debug("Called listen() on user[" + user + "]");
		if (user == null) {
			return null;
		} else if (listenId != null) {
			List<Message> result = new LinkedList<Message>();
			if (!listenId.equals(user.getListenId())) {
				SystemMessage message = new SystemMessage(null, 0l, MessagePreset.DUPLICATESESSION);
				result.add(message);
			} else {
				Message message;
				while ((message = user.read()) != null) {
					result.add(message);
				}
			}
			logger.debug("returning " + result.size() + " messages");
			return result.toArray(new Message[result.size()]);
		} else {
			return NO_MESSAGES;
		}
	}

	@Override
	public JSONObject refresh(ChatUser user, Locale locale) {
		return service.getRefreshData(user, locale);
	}

    @Override
    public String register(ChatUser user) {
        logger.info(user + 	" registers to Listen");
        user.setListenerTime(System.currentTimeMillis());
        user.alive();
        roomService.join(user.getCurrentRoom(), user);
        return user.getListenId();
    }

}
