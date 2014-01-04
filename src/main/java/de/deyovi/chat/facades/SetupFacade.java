package de.deyovi.chat.facades;

public interface SetupFacade {

	/**
	 * Checks if the System has already been initialized
	 * @return
	 */
	public boolean isInitialized();
	
	/**
	 * Initializes the System, dropping all existing data
	 */
	public void initialize(String username, String password);
	
	/**
	 * Updates the System, refreshing Table-schema-files
	 */
	public void update();
	
}
