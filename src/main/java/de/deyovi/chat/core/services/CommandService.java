package de.deyovi.chat.core.services;

import de.deyovi.chat.core.constants.ChatConstants.ChatCommand;
import de.deyovi.chat.core.objects.ChatUser;

import java.io.InputStream;

public interface CommandService {

	/**
	 * Processes a Users Input, looking for commands
	 * @param user
	 * @param cmd
	 * @param payload
	 * @param uploadStream
	 * @param uploadName
	 * @return true if the message has been processed
	 */
	boolean process(ChatUser user, ChatCommand cmd, String payload, InputStream uploadStream, String uploadName);

	/**
	 * Let's a user join a certain room
	 * @param user
	 * @param roomName
	 */
	void join(ChatUser user, String roomName);

	/**
	 * Sets the away-status of a user
	 * @param user
	 * @param away
	 */
	void away(ChatUser user, boolean away);
	
}
