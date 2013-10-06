package de.deyovi.chat.core.services;

import java.util.List;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Message;

public interface ChatUserService {

	/**
	 * Checks if an user is currently active an logged in
	 * @param user
	 * @return
	 */
	public boolean isActive(ChatUser user);

	/**
	 * Sends a {@link Message} to all users
	 * @param message
	 */
	public void broadcast(Message message);
	
	public void logout(ChatUser user);
	
	public ChatUser getByName(String target);
	
	public ChatUser getBySessionId(String sessionId);
	
	public String createInvitation(ChatUser inviter, boolean trial);

	public ChatUser login(String username, String pwHash, String sugar);

	public ChatUser register(String username, String password, String invitationKey, String sugar);

	public List<ChatUser> getLoggedInUsers();
	
}
