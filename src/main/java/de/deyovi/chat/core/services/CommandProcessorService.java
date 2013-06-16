package de.deyovi.chat.core.services;

import java.io.InputStream;

import de.deyovi.chat.core.constants.ChatConstants.ChatCommand;
import de.deyovi.chat.core.objects.ChatUser;

public interface CommandProcessorService {

	/**
	 * Processes a Users Input, looking for commands
	 * @param user
	 * @param cmd
	 * @param payload
	 * @param uploadStream
	 * @param uploadName
	 * @return true if the message has been processed
	 */
	public boolean process(ChatUser user, ChatCommand cmd, String payload, InputStream uploadStream, String uploadName);

	/**
	 * Let's a user join a certain room
	 * @param user
	 * @param roomName
	 */
	public void join(ChatUser user, String roomName);

	/**
	 * Sets the away-status of a user
	 * @param user
	 * @param away
	 */
	public void away(ChatUser user, boolean away);
	
}
