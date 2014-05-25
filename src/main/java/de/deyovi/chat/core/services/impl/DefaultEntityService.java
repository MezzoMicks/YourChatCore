package de.deyovi.chat.core.services.impl;

import de.deyovi.chat.core.services.EntityService;
import org.apache.log4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.swing.text.html.parser.Entity;
import javax.transaction.UserTransaction;
import javax.xml.ws.Action;

@Stateless
public class DefaultEntityService implements EntityService {

	private final static Logger logger = Logger.getLogger(DefaultEntityService.class);

	private volatile static EntityService _instance = null;
	private volatile Boolean initialized = null;
    @PersistenceUnit(unitName = "YourChatWeb")
    private EntityManagerFactory emf;
    @PersistenceContext
    private EntityManager entityManager;

	public EntityManagerFactory getFactory() {
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
    @Action
	public void persistOrMerge(Object entity, boolean create) {
        logger.debug("persisting entity " + entity);
        if (create) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
	}
	
	/* (non-Javadoc)
	 * @see de.deyovi.chat.core.service.impl.EntityService#remove(javax.persistence.EntityManager, java.lang.Object)
	 */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove(Object entity) {
		EntityManager em = emf.createEntityManager();
		try {
			em.joinTransaction();
			logger.debug("removing entity " + entity);
			em.merge(entity);
			em.remove(entity);
            em.flush();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			em.close();
		}
	}

}
