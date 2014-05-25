package de.deyovi.aide.impl;

import java.util.concurrent.atomic.AtomicLong;

import de.deyovi.aide.Notice;

public abstract class AbstractNotice implements Notice {
	
	private static final AtomicLong ID_SEQUENCE = new AtomicLong();
	private final String messageCode;
	private final Level level;
	private final long id;

	public AbstractNotice(String messageCode, Level level) {
		this.messageCode = messageCode;
		this.level = level;
		this.id = ID_SEQUENCE.get();
	}
	
	@Override
	public int compareTo(Notice o) {
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
	public String getMessageCode() {
		return messageCode;
	}

	@Override
	public long getID() {
		return id;
	}
}
