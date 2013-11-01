package de.deyovi.chat.core.services.impl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import de.deyovi.chat.core.constants.ChatConstants;
import de.deyovi.chat.core.constants.ChatConstants.MessagePreset;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Image;
import de.deyovi.chat.core.objects.Message;
import de.deyovi.chat.core.objects.Profile;
import de.deyovi.chat.core.objects.Room.RoomInfo;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.services.ChatUserService;
import de.deyovi.chat.core.services.MessageConsumer;
import de.deyovi.chat.core.services.OutputService;
import de.deyovi.chat.core.services.RoomService;
import de.deyovi.chat.core.services.TranslatorService;
import de.deyovi.chat.core.utils.ChatUtils;

public class DefaultOutputService  implements OutputService {

	private static final Logger logger = LogManager.getLogger(DefaultOutputService.class);

	private final TranslatorService translatorService = ResourceTranslatorService.getInstance();
	private final RoomService roomServce = DefaultRoomService.getInstance();
	private final ChatUserService userService = DefaultChatUserService.getInstance();
	
	private static volatile OutputService instance = null;
	
	private DefaultOutputService() {
		// hidden
	}

	public static OutputService getInstance() {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}

	private static synchronized void createInstance() {
		if (instance == null) {
			instance = new DefaultOutputService();
		}
	}

	public OutputMeta processMessages(Message[] messages, Locale locale, MessageConsumer consumer) {
		MyOutputMeta meta = new MyOutputMeta();
		if (messages == null) {
			meta.stop = -1;
		} else {
			for (Message msg : messages) {
				if (logger.isDebugEnabled()) {
					logger.debug("decoding message : " + msg.toString());
				}
				MessagePreset code = MessagePreset.getByCode(msg.getCode());
				meta.origin = msg.getOrigin();
				if (code.getEvent() == ChatConstants.MESSAGE_EVENT_REFRESH) {
					meta.refresh = true;
				} else if (code.getEvent() == ChatConstants.MESSAGE_EVENT_STOP) {
					meta.stop = 1;
				}
				Segment[] segments = msg.getSegments();
				if (segments != null) {
					consumer.consume(segments, locale, meta);
				}
			}
		}
		return meta;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.deyovi.chat.facades.impl.OutputFacade#refresh(de.deyovi.chat.core.
	 * objects.ChatUser, java.util.Locale)
	 */
	@Override
	public JSONObject getRefreshData(ChatUser user, Locale locale) {
		JSONObject result = new JSONObject();
		try {
			result.put("away", user.isAway());

			String font = user.getSettings().getFont();
			result.put("font", (font != null ? font.trim() : null));
			RoomInfo info = user.getCurrentRoom().getInfoForUser(user);
			result.put("room", ChatUtils.escape(info.getName()));
			result.put("background", info.getBgColor());
			result.put("foreground", info.getFgColor());
			result.put("backgroundimage", info.getBgImage());
			ChatUser[] myRoomMates = info.getUsers();
			for (ChatUser roomUser : myRoomMates) {
				result.append("users", jsonifyUser(roomUser));
			}
			for (Segment media : info.getMedia()) {
				JSONObject jsonMedia = new JSONObject();
				jsonMedia.put("link", media.getContent());
				String name = media.getAlternateName();
				if (name == null) {
					name = media.getContent();
				} else {
					if (name.charAt(0) == '$') {
						name = translatorService.translate(name.substring(1), locale);
					}
					name = ChatUtils.escape(name);
				}
				jsonMedia.put("name", name);
				jsonMedia.put("preview", media.getPreview());
				jsonMedia.put("pinky", media.getPinky());
				jsonMedia.put("type", media.getType());
				jsonMedia.put("user", ChatUtils.escape(media.getUser()));
				result.append("medias", jsonMedia);
			}
			List<ChatUser> otherUsers = userService.getLoggedInUsers();
			for (ChatUser roomMate : myRoomMates) {
				otherUsers.remove(roomMate);
			}
			for (ChatUser otherUser : otherUsers) {
				result.append("others", jsonifyUser(otherUser));
			}
			List<RoomInfo> openRooms = roomServce.getOpenRooms();
			for (RoomInfo room : openRooms) {
				JSONObject jsonRoom = new JSONObject();
				jsonRoom.put("name", ChatUtils.escape(room.getName()));
				jsonRoom.put("color", room.getBgColor());
				ChatUser[] users = room.getUsers();
				jsonRoom.put("users", (users != null ? users.length : null));
				result.append("rooms", jsonRoom);
			}
		} catch (JSONException e) {
			logger.error("Problem while writing JSONOutput", e);
		}
		return result;
	}

	private JSONObject jsonifyUser(ChatUser user) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("username", ChatUtils.escape(user.getUserName()));
		json.put("alias", ChatUtils.escape(user.getAlias()));
		json.put("color", user.getSettings().getColor());
		json.put("guest", user.isGuest());
		json.put("away", user.isAway());
		Profile profile = user.getProfile();
		Image avatar = profile != null ? profile.getAvatar() : null;
		json.put("avatar", (avatar != null) ? avatar.getID() : null);
		return json;
	}
	
	private class MyOutputMeta implements OutputMeta {

		private ChatUser origin;
		private boolean refresh;
		private int stop;
		private String profile;
		
		@Override
		public ChatUser getOrigin() {
			return origin;
		}

		@Override
		public boolean isRefreshRequired() {
			return refresh;
		}

		@Override
		public boolean isInterrupted() {
			return stop != 0;
		}

		@Override
		public int interruptionReason() {
			return stop;
		}

	}
	
}
