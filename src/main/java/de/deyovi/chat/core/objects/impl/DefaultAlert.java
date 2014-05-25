package de.deyovi.chat.core.objects.impl;

import de.deyovi.aide.impl.AbstractNotice;
import de.deyovi.chat.core.objects.Alert;

/**
 * Default Implementation for {@link Alert}-Interface
 * @author Michi
 *
 */
public class DefaultAlert extends AbstractNotice implements Alert {

	private final Lifespan lifespan;
	
	public DefaultAlert(String messageCode, Level level, Lifespan lifespan) {
		super(messageCode, level);
		this.lifespan = lifespan;
	}
	
	public DefaultAlert(String messageCode) {
		this(messageCode, Level.INFO, Lifespan.NORMAL);
	}
	
	@Override
	public Lifespan getLifespan() {
		return lifespan;
	}
	
}
