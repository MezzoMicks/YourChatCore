package de.deyovi.chat.core.dao;

import java.util.List;

import de.deyovi.chat.core.entities.ChatUserEntity;

/**
 * Access to persistence of ChatUsers
 * @author Michi
 *
 */
public interface ChatUserDAO {

	/**
	 * Finds a persisted ChatUser by its name
	 * @param username
	 * @return {@link ChatUserEntity} or null if none was found
	 */
	public ChatUserEntity findChatUserByName(String username);

	/**
	 * Find a persisted ChatUser by its id
	 * @param id
	 * @return {@link ChatUserEntity} or null if none was found
	 */
	public ChatUserEntity findChatUserById(long id);
	
	/**
	 * Find all persisted ChatUsers
	 * @return {@link List} of {@link ChatUserEntity} (at least empty)
	 */
	public List<ChatUserEntity> findAll();

	/**
	 * Deletes all persisted ChatUsers
	 */
	public void deleteAll();
	
}
