package de.deyovi.chat.core.services.impl;

import de.deyovi.chat.core.constants.ChatConstants.ImageSize;
import de.deyovi.chat.dao.ChatUserDAO;
import de.deyovi.chat.dao.ImageDAO;
import de.deyovi.chat.dao.ProfileDAO;
import de.deyovi.chat.dao.entities.ChatUserEntity;
import de.deyovi.chat.dao.entities.ImageEntity;
import de.deyovi.chat.dao.entities.ProfileEntity;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Image;
import de.deyovi.chat.core.objects.Profile;
import de.deyovi.chat.core.objects.impl.DefaultImage;
import de.deyovi.chat.core.objects.impl.DefaultProfile;
import de.deyovi.chat.core.services.ProfileService;
import de.deyovi.chat.core.utils.ChatUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class DefaultProfileService implements ProfileService {
	
	private final static Logger logger = Logger.getLogger(DefaultProfileService.class);

    public void setChatUtils(ChatUtils chatUtils) {
        this.chatUtils = chatUtils;
    }

    private ChatUtils chatUtils;
	private ImageDAO imageDAO;
	private ProfileDAO profileDAO;
	private ChatUserDAO chatUserDAO;

	public byte[] getImageData(long id, ImageSize size) {
		ImageEntity imageEntity = imageDAO.findOne(id);
		if (imageEntity != null) {
			switch (size) {
			default:
			case ORIGINAL:
				return imageEntity.getOriginal();
			case PREVIEW:
				return imageEntity.getPreview();
			case THUMBNAIL:
				return imageEntity.getThumbnail();
			case PINKY:
				return imageEntity.getPinkynail();
			}
		} else {
			return null;
		}
	}
	

	@Override
	public Long setAvatarImage(ChatUser user, String uploadName, InputStream uploadStream, String title) {
		logger.info("setting avatar for userprofile " + user);
		ChatUserEntity chatUserEntity = chatUserDAO.findOne(user.getId());
		if (chatUserEntity == null) {
			logger.error("Couldn't find entity for user " + user);
			return null;
		} else {
			ProfileEntity profile = getOrCreateEntity(chatUserEntity);
			ImageEntity newImage = createImageEntity(uploadStream, uploadName, title, null);
			if (newImage != null) {
				ImageEntity oldImage = profile.getAvatar();
				profile.setAvatar(newImage);
				profileDAO.save(profile);
				if (oldImage != null) {
					imageDAO.delete(oldImage);
				}
				return newImage.getId();
			} else {
				return null;
			}
		}
	}
	
	
	@Override
	public boolean deleteImage(ChatUser user, long id) {
		if (user != null) {
			ImageEntity imageEntity = imageDAO.findOne(id);
			if (imageEntity != null) {
                ProfileEntity profileEntity = getProfileEntity(user);
				if (profileEntity == null) {
					logger.warn("User " + user + " has no profile, won't delete anything");
					return false;
				} else {
					if (profileEntity.getPhotos().contains(imageEntity)) {
						profileEntity.getPhotos().remove(imageEntity);
						logger.info("removing image " + id + " from profile");
						profileDAO.save(profileEntity);
						logger.info("removing image " + id + " from db");
						imageDAO.save(imageEntity);
						return true;
					} else {
						logger.warn("Image " + id + " doesn't belong to " + user + " won't delete!");
						return false;
					}
				}
			} else {
				logger.warn("Image " + id + " doesn't exist, can't delete");
				return false;
			}
		} else {
			return false;
		}
	}
	

	@Override
	public void storeProfile(Profile profile) {
		if (profile != null) {
			getProfile(profile.getUser());
		}
	}
	
	/* (non-Javadoc)
	 * @see de.deyovi.chat.core.services.impl.ProfileService#getProfile(de.deyovi.chat.dao.entities.ChatUserEntity)
	 */
	@Override
	public Profile getProfile(ChatUser user) {
        ProfileEntity profileEntity = getProfileEntity(user);
		return convertFromEntity(user, profileEntity);
	}

    private ProfileEntity getProfileEntity(ChatUser user) {
        ChatUserEntity chatUserEntity = chatUserDAO.findOne(user.getId());
        return chatUserEntity != null ? chatUserEntity.getProfile() : null;
    }

    private Profile convertFromEntity(ChatUser user, ProfileEntity profileEntity) {
		Profile profile;
		if (profileEntity == null) {
			profile = null;
		} else {
			profile = new DefaultProfile(user);
			profile.setAdditionalInfo(profileEntity.getOneliner());
			profile.setAvatar(imageEntityToObject(profileEntity.getAvatar()));
			profile.setGender(profileEntity.getGender());
			profile.setDateOfBirth(profileEntity.getBirthday());
			List<Image> images = new LinkedList<Image>();
			for (ImageEntity imageEntity : profileEntity.getPhotos()) {
				images.add(imageEntityToObject(imageEntity));
			}
			profile.setGallery(images);
		}
		return profile;
	}
	

	private Image imageEntityToObject(ImageEntity entity) {
		Image object = new DefaultImage(entity.getId(), entity.getTitle());
		return object;
	}

	private ProfileEntity getOrCreateEntity(ChatUserEntity user) {
		ProfileEntity profile = user.getProfile();
		if (profile == null) {
			profile = new ProfileEntity();
			user.setProfile(profile);
			chatUserDAO.save(user);
		}
		return profile;
	}
	
	@Override
	public Long addGalleryImage(ChatUser user, String uploadName, InputStream uploadStream, String title) {
		logger.info("adding image to userprofile " + user);
		ChatUserEntity chatUserEntity = chatUserDAO.findOne(user.getId());
		if (chatUserEntity == null) {
			logger.error("Couldn't find entity for user " + user);
			return null;
		} else {
			ProfileEntity profile = getOrCreateEntity(chatUserEntity);
			ImageEntity newImage = createImageEntity(uploadStream, uploadName, title, null);
			if (newImage != null) {
				profile.getPhotos().add(newImage);
				profileDAO.save(profile);
				return newImage.getId();
			} else {
				return null;
			}
		}
	}

	private ImageEntity createImageEntity(InputStream uploadStream, String uploadname, String title, String description) {
		final ImageEntity newEntity = new ImageEntity();
		try {
			// Get Name, Title and Description
			newEntity.setFilename(uploadname);
			newEntity.setTitle(title);
			newEntity.setDescription(description);
			logger.info("creating imageentity for file " + uploadname);
			// store the original
//			final byte[] originalAsByte = new byte[(int) file.length()];
	        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(uploadStream, baos);
			final byte[] originalAsByte = baos.toByteArray();
//			FileInputStream fis = new FileInputStream(file);
			newEntity.setOriginal(originalAsByte);
			// make a Image for the file
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(originalAsByte));
			// create preview (280) and store it
	        baos.reset();
	        int previewSize = ImageSize.PREVIEW.getSize();
	        chatUtils.createResized(image, baos, previewSize, previewSize, null);
	        newEntity.setPreview(baos.toByteArray());
	        baos.reset();
	        // create thumbnail (160) and store it
	        int thumbSize = ImageSize.THUMBNAIL.getSize();
	        chatUtils.createResized(image, baos, thumbSize, thumbSize, null);
	        newEntity.setThumbnail(baos.toByteArray());
	        baos.reset();
	        // create pinkynail (64) and store it
	        int pinkySize = ImageSize.PINKY.getSize();
	        chatUtils.createResized(image, baos, pinkySize, pinkySize, null);
	        newEntity.setPinkynail(baos.toByteArray());
	        baos.close();
	        // write to db
			imageDAO.save(newEntity);
			return newEntity;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

    @Required
    public void setImageDAO(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }

    @Required
    public void setProfileDAO(ProfileDAO profileDAO) {
        this.profileDAO = profileDAO;
    }

    @Required
    public void setChatUserDAO(ChatUserDAO chatUserDAO) {
        this.chatUserDAO = chatUserDAO;
    }

}
