package de.deyovi.chat.core.dao.impl;

import de.deyovi.chat.core.dao.ChatUserDAO;
import de.deyovi.chat.core.dao.ProfileDAO;
import de.deyovi.chat.core.entities.ChatUserEntity;
import de.deyovi.chat.core.entities.ProfileEntity;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DefaultProfileDAO implements ProfileDAO {

    @Inject
	private ChatUserDAO chatUserDAO;

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
