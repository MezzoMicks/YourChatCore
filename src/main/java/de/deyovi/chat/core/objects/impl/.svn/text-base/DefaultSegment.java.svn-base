package de.deyovi.chat.core.objects.impl;

import de.deyovi.chat.core.objects.Segment;

public class DefaultSegment implements Segment {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2786199409351908730L;
	private String user;
	private String content;
	private ContentType type;
	private String preview;
	private String pinky;
	private String alternateName = null;
	
	public DefaultSegment(String user, String content, ContentType type, String preview, String pinky, String alternateName) {
		this.user = user;
		this.content = content;
		this.type = type;
		this.preview = preview;
		this.pinky = pinky;
		this.alternateName = alternateName;
	}

	@Override
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	@Override
	public ContentType getType() {
		return type;
	}

	public void setType(ContentType type) {
		this.type = type;
	}
	
	@Override
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String getPreview() {
		return preview;
	}
	
	public void setPreview(String preview) {
		this.preview = preview;
	}
	
	@Override
	public String getPinky() {
		return pinky;
	}
	
	public void setPinky(String pinky) {
		this.pinky = pinky;
	}
	
	@Override
	public String getAlternateName() {
		return alternateName;
	}
	
	public void setAlternateName(String alternateName) {
		this.alternateName = alternateName;
	}
	
	@Override
	public void append(String content) {
		this.content += ' ' + content;
	}
	
}
