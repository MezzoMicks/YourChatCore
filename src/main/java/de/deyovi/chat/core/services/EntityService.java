package de.deyovi.chat.core.services;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;

/**
 * Service for all direct Database related interactions
 */
public interface EntityService {

    /**
     * closes the global EntityManagerFactory
     */
	public void closeEntityManagerFactory();

    /**
     * Inserts or updates an object into the database
     * @param entity
     * @param create
     */
	public void persistOrMerge(Object entity, boolean create);

    /**
     * Removes an object from database
     * @param entity
     */
	public void remove(Object entity);

    /**
     * Retrieves the EntityManagerFactory 
     * This Method should be used and preferred to prevent unclosed Factory-Resources on undeployment
     * @return {@link EntityManagerFactory}
     */
	public EntityManagerFactory getFactory();


}