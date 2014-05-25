package de.deyovi.chat.core.services;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Profile;

import java.io.InputStream;

/**
 * Service for all Profile-Modification
 *
 * @author Michi
 *
 */
public interface ProfileService {

	/**
	 * Retrieves a users profile
	 * @param user
	 * @return {@link Profile}
	 */
	public Profile getProfile(ChatUser user);
	
	/**
	 * Deletes an Image, that should be assigned to this {@link Profile}
	 * @param user
	 * @param id
	 * @return boolean
	 */
	public boolean deleteImage(ChatUser user, long id);

	/**
	 * Adds an Image to the users Profile
	 * @param user
	 * @param imageName
	 * @param imageStream
	 * @param title
	 * @return {@link Long} the new Image Id
	 */
	public Long addGalleryImage(ChatUser user, String imageName, InputStream imageStream, String title);

	/**
	 * Adds an Avatar to the users Profile, and removes their old avatar
	 * @param user
	 * @param imageName
	 * @param imageStream
	 * @param title
	 * @return {@link Long} the new Avatar Id
	 */
	public Long setAvatarImage(ChatUser user, String imageName, InputStream imageStream, String title);
	
	/**
	 * Persists general changes to the {@link Profile}
	 * @param profile
	 */
	public void storeProfile(Profile profile);

}