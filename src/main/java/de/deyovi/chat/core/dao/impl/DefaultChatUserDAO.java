package de.deyovi.chat.core.dao.impl;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import de.deyovi.chat.core.dao.ChatUserDAO;
import de.deyovi.chat.core.entities.ChatUserEntity;
import de.deyovi.chat.core.services.EntityService;
import de.deyovi.chat.core.services.impl.DefaultEntityService;

@Stateless
public class DefaultChatUserDAO implements ChatUserDAO {

	private static final Logger logger = Logger.getLogger(DefaultChatUserDAO.class);

    @Inject
	private EntityService entityService;
	
	@Override
	public ChatUserEntity findChatUserByName(String username) {
		// Query the db for that user
		logger.debug("Fetching UserEntity by name " + username);
        EntityManager em = entityService.getFactory().createEntityManager();
		TypedQuery<ChatUserEntity> query = em.createNamedQuery("findUserByName", ChatUserEntity.class);
		query.setParameter("name", username.toLowerCase());
		ChatUserEntity userEntity = null;
		try {
			userEntity = query.getSingleResult();
			logger.debug("Found " + userEntity.getId() + " " + userEntity.getName());
		} catch (NoResultException nrex) {
			logger.debug("User " + username + " not found in Database");
		}
		return userEntity;
	}
	
	@Override
	public ChatUserEntity findChatUserById(long id) {
		logger.debug("Fetching UserEntity by id " + id);
		ChatUserEntity userEntity = null;
		try {
            EntityManager em = entityService.getFactory().createEntityManager();
			userEntity = em.find(ChatUserEntity.class, id);
			logger.debug("Found " + userEntity.getId() + " " + userEntity.getName());
		} catch (NoResultException nrex) {
			logger.debug("User " + id + " not found in Database");
		}
		return userEntity;
	}
	
	@Override
	public List<ChatUserEntity> findAll() {
		logger.debug("Fetching all ChatUsers");
		List<ChatUserEntity> userEntities = findAllWithEntityManager();
		return userEntities;
	}
	
	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteAll() {
		logger.debug("Fetching all ChatUsers");
        EntityManager em = entityService.getFactory().createEntityManager();
		try {
            em.joinTransaction();
			List<ChatUserEntity> userEntities = findAllWithEntityManager();
			for (ChatUserEntity entity : userEntities) {
                em.remove(entity);
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
            em.close();
		}
	}
	
	
	private List<ChatUserEntity> findAllWithEntityManager() {
		List<ChatUserEntity> userEntities = new LinkedList<ChatUserEntity>();
        EntityManager em = entityService.getFactory().createEntityManager();
		try {
			TypedQuery<ChatUserEntity> query = em.createNamedQuery("findAll", ChatUserEntity.class);
			userEntities = query.getResultList();
			logger.debug("Found " + userEntities.size() + " Users");
		} catch (NoResultException nrex) {
			logger.debug("No Users found in Database");
		}
		return userEntities;
	}
	
	
}
