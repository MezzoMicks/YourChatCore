package de.deyovi.chat.core.services;

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
     * Initializes the System, erasing all current data
     */
	public void initialize(String username, String password);

    /**
     * Checks if the System has already been initialized
     * @return true if System is initialized
     */
    public boolean isInitialized();

    /**
     * Retrieves the EntityManagerFactory 
     * This Method should be used and preferred to prevent unclosed Factory-Resources on undeployment
     * @return {@link EntityManagerFactory}
     */
	public EntityManagerFactory getFactory();


}