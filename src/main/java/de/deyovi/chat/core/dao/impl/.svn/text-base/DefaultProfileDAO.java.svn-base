package de.deyovi.chat.core.dao.impl;

import de.deyovi.chat.core.dao.ChatUserDAO;
import de.deyovi.chat.core.dao.ProfileDAO;
import de.deyovi.chat.core.entities.ChatUserEntity;
import de.deyovi.chat.core.entities.ProfileEntity;

public class DefaultProfileDAO implements ProfileDAO {

	private static final ProfileDAO instance = new DefaultProfileDAO();
	
	private final ChatUserDAO chatUserDAO = new DefaultChatUserDAO();
	
	public static ProfileDAO getInstance() {
		return instance;
	}
	
	@Override
	public ProfileEntity findProfileByChatUser(ChatUserEntity chatuser) {
		return chatuser !=null ? chatuser.getProfile() : null;
	}
	
	@Override
	public ProfileEntity findProfileById(long userId) {
		return findProfileByChatUser(chatUserDAO.findChatUserById(userId));
	}
	
	@Override
	public ProfileEntity findProfileByName(String username) {
		return findProfileByChatUser(chatUserDAO.findChatUserByName(username));
	}
	
}
