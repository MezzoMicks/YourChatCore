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

    /**
     * Logs the supplied ChatUser out
     * removing all runtime references
     * @param user
     */
	public void logout(ChatUser user);

    /**
     * Retrieves a ChatUser by the name
     * @param name
     * @return ChatUser
     */
	public ChatUser getByName(String name);

    /**
     * Retrieves a Chatuser by it's sessionId
     * @param sessionId
     * @return ChatUser
     */
	public ChatUser getBySessionId(String sessionId);

    /**
     * Creates an Invitation key on bases of the ChatUser's rights
     * @param inviter
     * @param trial
     * @return String (the invitation key)
     */
	public String createInvitation(ChatUser inviter, boolean trial);

    /**
     * Logs a ChatUser in. Using the supplied password hash and using the sugar to counter-hash the saved password.
     * @param username
     * @param pwHash
     * @param sugar
     * @return ChatUser (if login was successful, otherwise null)
     */
	public ChatUser login(String username, String pwHash, String sugar);

	public ChatUser register(String username, String password, String invitationKey, String sugar);

    /**
     * Retrieves a List of all currently logged in ChatUsers
     * @return List of ChatUser
     */
	public List<ChatUser> getLoggedInUsers();
	
	public void changePassword(ChatUser user, String passwordHash);
	
}
