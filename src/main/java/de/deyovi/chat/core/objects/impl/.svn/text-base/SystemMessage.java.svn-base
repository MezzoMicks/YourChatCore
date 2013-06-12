package de.deyovi.chat.core.objects.impl;

import de.deyovi.chat.core.constants.ChatConstants.MessagePreset;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Message;
import de.deyovi.chat.core.objects.Segment;

public class SystemMessage extends AbstractMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5892914947569606185L;
	private final ChatUser origin;
	private final long id;
	
	public SystemMessage(ChatUser origin, long id, Segment... segments) {
		super(segments);
		this.origin = origin;
		this.id = id;
		if (origin == null) {
			setCode(-1);
		}
	}
	
	public SystemMessage(ChatUser origin, long id, MessagePreset template, Object... args) {
		this(origin, id, template.getContent() != null ? new TextSegment(origin != null ? origin.getUserName() : null, String.format(template.getContent(), args)) : null);
		setCode(template.getCode());
	}
	

	public SystemMessage(long id, Message original) {
		this(original.getOrigin(), id, original.getSegments());
		setCode(original.getCode());
	}


	@Override
	public long getID() {
		return id;
	}

	@Override
	public ChatUser getOrigin() {
		return origin;
	}

	
}
