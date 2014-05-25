package de.deyovi.chat.facades.impl;

import de.deyovi.chat.core.services.EntityService;
import de.deyovi.chat.core.services.impl.DefaultEntityService;
import de.deyovi.chat.facades.SetupFacade;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DefaultSetupFacade implements SetupFacade {

    @Inject
	private EntityService entityService;

	@Override
	public void initialize(String username, String password) {
	}
	
	@Override
	public void update() {
	}
	
	@Override
	public boolean isInitialized() {
		return true;
	}
	
}
