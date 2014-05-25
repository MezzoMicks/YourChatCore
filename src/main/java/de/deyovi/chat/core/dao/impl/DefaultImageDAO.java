package de.deyovi.chat.core.dao.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import de.deyovi.chat.core.dao.ImageDAO;
import de.deyovi.chat.core.entities.ImageEntity;
import de.deyovi.chat.core.services.EntityService;
import de.deyovi.chat.core.services.impl.DefaultEntityService;

@Stateless
public class DefaultImageDAO implements ImageDAO {
	
	private static final Logger logger = Logger.getLogger(DefaultImageDAO.class);

    @Inject
	private EntityService entityService;
	
	@Override
	public ImageEntity findByID(long id) {
		EntityManager entityManager = entityService.getFactory().createEntityManager();
		ImageEntity imageEntity = entityManager.find(ImageEntity.class, id);
		return imageEntity;
	}
	
}
