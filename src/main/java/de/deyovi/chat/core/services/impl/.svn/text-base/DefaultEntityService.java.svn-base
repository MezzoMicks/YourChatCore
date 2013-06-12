package de.deyovi.chat.core.services.impl;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import de.deyovi.chat.core.services.EntityService;

public class DefaultEntityService implements EntityService {

	private final static Logger logger = Logger.getLogger(DefaultEntityService.class);

	private final static EntityService instance = new DefaultEntityService();
	private volatile EntityManagerFactory emf = null;

	// = Persistence.createEntityManagerFactory("YourChatWeb")

	private DefaultEntityService() {
		
	}
	
	public static EntityService getInstance() {
		return instance;
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

	private static UserTransaction createTransaction() throws NamingException {
		return (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
	}
	
	
	private synchronized void createFactory() {
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory("YourChatWeb");
		}
	}

}
