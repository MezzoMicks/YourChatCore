package de.deyovi.chat.core.objects.impl;

import java.util.Date;
import java.util.List;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Image;
import de.deyovi.chat.core.objects.Profile;

public class DefaultProfile implements Profile {

	private final ChatUser user;
	private Image avatar;
	private String about;
	private String additionalInfo;
	private Date dateOfBirth;
	private List<Image> gallery;
	private int gender;

	public DefaultProfile(ChatUser user) {
		this.user = user;
	}
	
	@Override
	public ChatUser getUser() {
		return user;
	}
	
	@Override
	public Image getAvatar() {
		return avatar;
	}

	@Override
	public void setAvatar(Image avatar) {
		this.avatar = avatar;
	}
	
	@Override
	public String getAbout() {
		return about;
	}
	
	@Override
	public void setAbout(String about) {
		this.about = about;
	}

	@Override
	public String getAdditionalInfo() {
		return additionalInfo;
	}

	@Override
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	@Override
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	@Override
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@Override
	public int getGender() {
		return gender;
	}

	@Override
	public void setGender(int gender) {
		this.gender = gender;
	}

	@Override
	public List<Image> getGallery() {
		return gallery;
	}

	@Override
	public void setGallery(List<Image> images) {
		this.gallery = images;
	}

}
