package de.deyovi.chat.core.services;

import javax.persistence.EntityManagerFactory;


public interface EntityService {

	public void closeEntityManagerFactory();

	public void persistOrMerge(Object entity, boolean create);

	public void remove(Object entity);

	public EntityManagerFactory getFactory();

}