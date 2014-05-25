package de.deyovi.chat.core.objects.impl;

import de.deyovi.chat.core.objects.Segment;

/**
 * Represents a textual part of a message
 * @author Michi
 *
 */
public class TextSegment implements Segment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6109235839252072231L;
	private String content;
	private String user;
	
	public TextSegment(String user, String content) {
		this.user = user;
		this.content = content;
	}
	
	@Override
	public String getUser() {
		return user;
	}

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
	public ContentType getType() {
		return ContentType.TEXT;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public String getPreview() {
		return null;
	}
	
	@Override
	public String getPinky() {
		return null;
	}
	
	public void append(String content) {
		this.content += ' ' + content;
	}
	
	@Override
	public String getAlternateName() {
		return null;
	}
	

}
