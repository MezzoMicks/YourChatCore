package de.deyovi.chat.core.objects.impl;

import de.deyovi.chat.core.objects.Image;

public class DefaultImage implements Image {

	private Long id;
	private String title;
	
	public DefaultImage(Long id, String title) {
		this.id = id;
		this.title = title;
	}

	@Override
	public Long getID() {
		return id;
	}
	
	@Override
	public void setID(Long id) {
		this.id = id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}	
	
	
}
