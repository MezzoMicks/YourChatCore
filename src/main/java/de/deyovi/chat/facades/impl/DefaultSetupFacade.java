package de.deyovi.chat.facades.impl;

import de.deyovi.chat.core.services.EntityService;
import de.deyovi.chat.core.services.impl.DefaultEntityService;
import de.deyovi.chat.facades.SetupFacade;

public class DefaultSetupFacade implements SetupFacade {
	
	private volatile static DefaultSetupFacade instance = null;
	
	private DefaultSetupFacade() {
		// hidden
	}
	
	public static DefaultSetupFacade getInstance() {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}
	
	private static synchronized void createInstance() {
		if (instance == null) {
			instance = new DefaultSetupFacade();
		}
	}
	
	private final EntityService entityService = DefaultEntityService.getInstance();
	
	@Override
	public void initialize(String username, String password) {
		entityService.initialize(username, password);
	}
	
	@Override
	public void update() {
	}
	
	@Override
	public boolean isInitialized() {
		return entityService.isInitialized();
	}
	
}
