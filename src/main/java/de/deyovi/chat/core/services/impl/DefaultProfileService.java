package de.deyovi.chat.core.services.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.constants.ChatConstants.ImageSize;
import de.deyovi.chat.core.dao.ChatUserDAO;
import de.deyovi.chat.core.dao.ImageDAO;
import de.deyovi.chat.core.dao.ProfileDAO;
import de.deyovi.chat.core.dao.impl.DefaultChatUserDAO;
import de.deyovi.chat.core.dao.impl.DefaultImageDAO;
import de.deyovi.chat.core.dao.impl.DefaultProfileDAO;
import de.deyovi.chat.core.entities.ChatUserEntity;
import de.deyovi.chat.core.entities.ImageEntity;
import de.deyovi.chat.core.entities.ProfileEntity;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Image;
import de.deyovi.chat.core.objects.Profile;
import de.deyovi.chat.core.objects.impl.DefaultImage;
import de.deyovi.chat.core.objects.impl.DefaultProfile;
import de.deyovi.chat.core.services.EntityService;
import de.deyovi.chat.core.services.ProfileService;
import de.deyovi.chat.core.utils.ChatUtils;

public class DefaultProfileService implements ProfileService {
	
	private final static Logger logger = Logger.getLogger(DefaultProfileService.class);

	private volatile static ProfileService _instance;
	
	private final ImageDAO imageDAO = DefaultImageDAO.getInstance();
	private final ProfileDAO profileDAO = DefaultProfileDAO.getInstance();
	private final ChatUserDAO chatUserDAO = DefaultChatUserDAO.getInstance();
	private final EntityService entityService = DefaultEntityService.getInstance();
	
	public static ProfileService getInstance() {
		if (_instance == null) {
			createInstance();
		}
		return _instance;
	}
	
	private static synchronized void createInstance() {
		if (_instance == null) {
			_instance = new DefaultProfileService();
		}
	}
	
	public byte[] getImageData(long id, ImageSize size) {
		ImageEntity imageEntity = imageDAO.findByID(id);
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
		ChatUserEntity chatUserEntity = chatUserDAO.findChatUserById(user.getId());
		if (chatUserEntity == null) {
			logger.error("Couldn't find entity for user " + user);
			return null;
		} else {
			ProfileEntity profile = getOrCreateEntity(chatUserEntity);
			ImageEntity newImage = createImageEntity(uploadStream, uploadName, title, null);
			if (newImage != null) {
				ImageEntity oldImage = profile.getAvatar();
				profile.setAvatar(newImage);
				entityService.persistOrMerge(profile, false);
				if (oldImage != null) {
					entityService.remove(oldImage);
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
			ImageEntity imageEntity = imageDAO.findByID(id);
			if (imageEntity != null) {
				ProfileEntity profileEntity = profileDAO.findProfileById(user.getId());
				if (profileEntity == null) {
					logger.warn("User " + user + " has no profile, won't delete anything");
					return false;
				} else {
					if (profileEntity.getPhotos().contains(imageEntity)) {
						profileEntity.getPhotos().remove(imageEntity);
						logger.info("removing image " + id + " from profile");
						entityService.persistOrMerge(profileEntity, false);
						logger.info("removing image " + id + " from db");
						entityService.remove(imageEntity);
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
	 * @see de.deyovi.chat.core.services.impl.ProfileService#getProfile(de.deyovi.chat.core.entities.ChatUserEntity)
	 */
	@Override
	public Profile getProfile(ChatUser user) {
		ProfileEntity profileEntity = profileDAO.findProfileById(user.getId());
		return convertFromEntity(user, profileEntity);
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
			entityService.persistOrMerge(user, false);
		}
		return profile;
	}
	
	@Override
	public Long addGalleryImage(ChatUser user, String uploadName, InputStream uploadStream, String title) {
		logger.info("adding image to userprofile " + user);
		ChatUserEntity chatUserEntity = chatUserDAO.findChatUserById(user.getId());
		if (chatUserEntity == null) {
			logger.error("Couldn't find entity for user " + user);
			return null;
		} else {
			ProfileEntity profile = getOrCreateEntity(chatUserEntity);
			ImageEntity newImage = createImageEntity(uploadStream, uploadName, title, null);
			if (newImage != null) {
				profile.getPhotos().add(newImage);
				entityService.persistOrMerge(profile, false);
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
	        ChatUtils.createResized(image, baos, previewSize, previewSize, null);
	        newEntity.setPreview(baos.toByteArray());
	        baos.reset();
	        // create thumbnail (160) and store it
	        int thumbSize = ImageSize.THUMBNAIL.getSize();
	        ChatUtils.createResized(image, baos, thumbSize, thumbSize, null);
	        newEntity.setThumbnail(baos.toByteArray());
	        baos.reset();
	        // create pinkynail (64) and store it
	        int pinkySize = ImageSize.PINKY.getSize();
	        ChatUtils.createResized(image, baos, pinkySize, pinkySize, null);
	        newEntity.setPinkynail(baos.toByteArray());
	        baos.close();
	        // write to db
			entityService.persistOrMerge(newEntity, true);
			return newEntity;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	
}
