package de.deyovi.chat.core.services;

import java.io.InputStream;

import de.deyovi.chat.core.constants.ChatConstants.ChatCommand;
import de.deyovi.chat.core.objects.ChatUser;

public interface CommandProcessorService {

	public boolean process(ChatUser user, ChatCommand cmd, String payload, InputStream uploadStream, String uploadName);
	
}
