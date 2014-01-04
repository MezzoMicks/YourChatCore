package de.deyovi.chat.core.dao.impl;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import de.deyovi.chat.core.dao.ChatUserDAO;
import de.deyovi.chat.core.entities.ChatUserEntity;
import de.deyovi.chat.core.services.EntityService;
import de.deyovi.chat.core.services.impl.DefaultEntityService;

public class DefaultChatUserDAO implements ChatUserDAO {

	private static final Logger logger = Logger.getLogger(DefaultChatUserDAO.class);

	private static ChatUserDAO _instance = null;
	
	public static ChatUserDAO getInstance() {
		if (_instance == null) {
			createInstance();
		}
		return _instance;
	}
	
	private static synchronized void createInstance() {
		if (_instance == null) {
			_instance = new DefaultChatUserDAO();
		}
	}
	
	private EntityService entityService = DefaultEntityService.getInstance();
	
	@Override
	public ChatUserEntity findChatUserByName(String username) {
		// Query the db for that user
		logger.debug("Fetching UserEntity by name " + username);
		EntityManager entityManager = entityService.getFactory().createEntityManager();
		TypedQuery<ChatUserEntity> query = entityManager.createNamedQuery("findUserByName", ChatUserEntity.class);
		query.setParameter("name", username.toLowerCase());
		ChatUserEntity userEntity = null;
		try {
			userEntity = query.getSingleResult();
			logger.debug("Found " + userEntity.getId() + " " + userEntity.getName());
		} catch (NoResultException nrex) {
			logger.debug("User " + username + " not found in Database");
		}
		entityManager.close();
		return userEntity;
	}
	
	@Override
	public ChatUserEntity findChatUserById(long id) {
		logger.debug("Fetching UserEntity by id " + id);
		EntityManager entityManager = entityService.getFactory().createEntityManager();
		ChatUserEntity userEntity = null;
		try {
			userEntity = entityManager.find(ChatUserEntity.class, id);
			logger.debug("Found " + userEntity.getId() + " " + userEntity.getName());
		} catch (NoResultException nrex) {
			logger.debug("User " + id + " not found in Database");
		}
		entityManager.close();
		return userEntity;
	}
	
	@Override
	public List<ChatUserEntity> findAll() {
		logger.debug("Fetching all ChatUsers");
		EntityManager entityManager = entityService.getFactory().createEntityManager();
		List<ChatUserEntity> userEntities = new LinkedList<ChatUserEntity>();
		try {
			TypedQuery<ChatUserEntity> query = entityManager.createNamedQuery("findAll", ChatUserEntity.class);
			userEntities = query.getResultList();
			logger.debug("Found " + userEntities.size() + " Users");
		} catch (NoResultException nrex) {
			logger.debug("No Users found in Database");
		}
		entityManager.close();
		return userEntities;
	}
	
	
	
}
