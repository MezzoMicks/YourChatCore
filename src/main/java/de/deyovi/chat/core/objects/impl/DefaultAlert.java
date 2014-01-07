package de.deyovi.chat.core.objects.impl;

import java.util.concurrent.atomic.AtomicLong;

import de.deyovi.chat.core.objects.Alert;

/**
 * Default Implementation for {@link Alert}-Interface
 * @author Michi
 *
 */
public class DefaultAlert implements Alert {

	private static final AtomicLong ID_SEQUENCE = new AtomicLong();
	private final String messageCode;
	private final Lifespan lifespan;
	private final Level level;
	private final long id;
	
	public DefaultAlert(String messageCode, Level level, Lifespan lifespan) {
		this.messageCode = messageCode;
		this.level = level;
		this.lifespan = lifespan;
		this.id = ID_SEQUENCE.get();
	}
	
	public DefaultAlert(String messageCode) {
		this(messageCode, Level.INFO, Lifespan.NORMAL);
	}
	
	@Override
	public int compareTo(Alert o) {
		if (o == null) {
			return -1;
		} else {
			Level otherLevel = o.getLevel();
			if (otherLevel == level) {
				return 0;
			} else {
				return Integer.compare(level.getPriority(), otherLevel.getPriority());
			}
		}
	}

	@Override
	public Level getLevel() {
		return level;
	}

	@Override
	public Lifespan getLifespan() {
		return lifespan;
	}

	@Override
	public String getMessageCode() {
		return messageCode;
	}

	@Override
	public long getID() {
		return id;
	}
	
}
