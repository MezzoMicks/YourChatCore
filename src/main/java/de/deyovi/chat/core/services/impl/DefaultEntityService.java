package de.deyovi.chat.core.services.impl;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import de.deyovi.chat.core.dao.ChatUserDAO;
import de.deyovi.chat.core.dao.impl.DefaultChatUserDAO;
import de.deyovi.chat.core.entities.ChatUserEntity;
import de.deyovi.chat.core.services.EntityService;
import de.deyovi.chat.core.utils.ChatConfiguration;
import de.deyovi.chat.core.utils.ChatUtils;
import de.deyovi.chat.core.utils.PasswordUtil;

public class DefaultEntityService implements EntityService {

	private final static Logger logger = Logger.getLogger(DefaultEntityService.class);

	private volatile static EntityService _instance = null;
	private volatile EntityManagerFactory emf = null;

	// = Persistence.createEntityManagerFactory("YourChatWeb")

	private DefaultEntityService() {
		
	}
	
	public static EntityService getInstance() {
		if (_instance == null) {
			createInstance();
		}
		return _instance;
	}
	
	private static synchronized void createInstance() {
		if (_instance == null) {
			_instance = new DefaultEntityService();
		}
	}
	

	public EntityManagerFactory getFactory() {
		if (emf == null) {
			createFactory();
		}
		return emf;
	}

	/* (non-Javadoc)
	 * @see de.deyovi.chat.core.service.impl.EntityService#closeEntityManagerFactory()
	 */
	public void closeEntityManagerFactory() {
		if (emf != null) {
			emf.close();
			emf = null;
			if (logger.isDebugEnabled()) {
				logger.debug("Persistence finished");
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.deyovi.chat.core.service.impl.EntityService#persistOrMerge(javax.persistence.EntityManager, java.lang.Object, boolean)
	 */
	public void persistOrMerge(Object entity, boolean create) {
		UserTransaction transaction;
		EntityManager em = emf.createEntityManager();
		try {
			transaction = createTransaction();
			transaction.begin();
			em.joinTransaction();
			logger.debug("persisting entity " + entity);
			if (create) {
				em.persist(entity);
			} else {
				em.merge(entity);
			}
			transaction.commit();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			em.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.deyovi.chat.core.service.impl.EntityService#remove(javax.persistence.EntityManager, java.lang.Object)
	 */
	public void remove(Object entity) {
		UserTransaction transaction;
		EntityManager em = emf.createEntityManager();
		try {
			transaction = createTransaction();
			transaction.begin();
			em.joinTransaction();
			logger.debug("removing entity " + entity);
			em.remove(entity);
			transaction.commit();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			em.close();
		}
	}

    @Override
    public void initialize(String username, String password) {
    	boolean allow;
    	logger.info("Initialization request with AdminUser:" + username);
    	if (isInitialized()) {
    		if (ChatConfiguration.isInitMode()) {
	    		allow = true;
		    	logger.warn("Allowing, on already initialized System (initmode)");
	    	} else {
	    		allow = false;
		    	logger.warn("Denying, becuase system already initialized");
	    	}
    	} else {
    		logger.info("Allowing, system not initialized");
    		allow = true;
    	}
    	
    	if (!isInitialized()) {
	    	logger.info("Creating AdminUser " + username);
	    	ChatUserDAO chatUserDao = DefaultChatUserDAO.getInstance();
	    	ChatUserEntity newUser = chatUserDao.findChatUserByName(username);
	    	if (newUser == null) {
	    		newUser = new ChatUserEntity();
				newUser.setName(username);
	    	}
			newUser.setPassword(PasswordUtil.encrypt(username, null));
			newUser.setTrusted(true);
			persistOrMerge(newUser, true);
    	}
    }

    @Override
    public boolean isInitialized() {
    	ChatUserDAO chatUser = DefaultChatUserDAO.getInstance();
    	// the system is not initialized if
    	List<ChatUserEntity> users = chatUser.findAll();
    	if (users.isEmpty()) {
    		// there are no users
    		return false;
    	} else {
    		// or nobody is trusted
    		boolean oneIsTrusted = false;
    		logger.info(users);
    		for (ChatUserEntity user : users) {
    			if ((oneIsTrusted = user.isTrusted())) {
    				break;
    			}
    		}
    		return oneIsTrusted;
    	}
    }

    private static UserTransaction createTransaction() throws NamingException {
		return (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
	}
	
	
	private synchronized void createFactory() {
		if (emf == null) {
			System.setProperty("javax.persistence.jdbc.driver", "org.hsql.jdbcDriver");
			emf = Persistence.createEntityManagerFactory("YourChatWeb");
		}
	}

}
