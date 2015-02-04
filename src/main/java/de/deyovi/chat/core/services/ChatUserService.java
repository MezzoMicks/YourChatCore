package de.deyovi.chat.core.services;

import de.deyovi.aide.Outcome;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Message;

import java.util.List;

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
     * Creates an Invitation key on bases of the ChatUser's rights
     * @param inviter
     * @param trial
     * @return String (the invitation key)
     */
	public String createInvitation(ChatUser inviter, boolean trial);

    /**
     * Logs a ChatUser in. Using the supplied password hash and using the sugar to counter-hash the saved password.
     * @param username
     * @param password
     * @return ChatUser (if login was successful, otherwise null)
     */
	public Outcome<ChatUser> login(String username, String password);

	public Outcome<ChatUser> register(String username, String password, String invitationKey);

    /**
     * Retrieves a List of all currently logged in ChatUsers
     * @return List of ChatUser
     */
	public List<ChatUser> getLoggedInUsers();
	
	public void changePassword(ChatUser user, String passwordHash);
	
}
