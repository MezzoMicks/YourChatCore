package de.deyovi.chat.dao;

import de.deyovi.chat.dao.entities.ChatUserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Access to persistence of ChatUsers
 * @author Michi
 *
 */
@Repository
public interface ChatUserDAO extends CrudRepository<ChatUserEntity, Long> {

	/**
	 * Finds a persisted ChatUser by its name
	 * @param username
	 * @return {@link ChatUserEntity} or null if none was found
	 */
	public ChatUserEntity findByName(String username);


    /**
     * Finds a persisted ChatUser by its name (case-INsensitive)
     * @param username
     * @return {@link ChatUserEntity} or null if none was found
     */
    public ChatUserEntity findByNameIgnoreCase(String username);

}
