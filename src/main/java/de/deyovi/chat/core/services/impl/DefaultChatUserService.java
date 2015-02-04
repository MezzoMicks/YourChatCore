package de.deyovi.chat.core.services.impl;

import de.deyovi.aide.Notice;
import de.deyovi.aide.Notice.Level;
import de.deyovi.aide.Outcome;
import de.deyovi.aide.impl.DefaultOutcome;
import de.deyovi.chat.core.constants.ChatConstants;
import de.deyovi.chat.core.constants.ChatConstants.MessagePreset;
import de.deyovi.chat.dao.ChatUserDAO;
import de.deyovi.chat.dao.entities.ChatUserEntity;
import de.deyovi.chat.core.objects.Alert.Lifespan;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Message;
import de.deyovi.chat.core.objects.Room;
import de.deyovi.chat.core.objects.impl.DefaultAlert;
import de.deyovi.chat.core.objects.impl.DefaultChatUser;
import de.deyovi.chat.core.objects.impl.DefaultChatUserSettings;
import de.deyovi.chat.core.objects.impl.SystemMessage;
import de.deyovi.chat.core.services.ChatUserService;
import de.deyovi.chat.core.services.ProfileService;
import de.deyovi.chat.core.services.RoomService;
import de.deyovi.chat.core.utils.ChatConfiguration;
import de.deyovi.chat.core.utils.PasswordUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default Implementation for ChatUserService
 * @author Michi
 *
 */
public class DefaultChatUserService implements ChatUserService, UserDetailsService {

	private final static Logger logger = Logger.getLogger(DefaultChatUserService.class);

	private final static long MINUTE = 1000 * 60;
	private final static long A_DAY_IN_MILLIS = MINUTE * 60 * 24;
	private final static long TIMEOUT = MINUTE * 60;
	private final static long AWAY_TIMEOUT = 6 * TIMEOUT;

    private final Map<String, ChatUser> names2users = new TreeMap<String, ChatUser>();
    private final Map<String, ChatInvitation> invitations = new HashMap<String, ChatInvitation>();

	private Room defaultRoom;

	private final AtomicLong ids = new AtomicLong(System.currentTimeMillis());

    private ChatUserDAO chatUserDAO;
    private ProfileService profileService;
    private RoomService roomService;

    @PostConstruct
    private void setup() {
        Thread timeoutThread = new MyTimeoutThread();
        timeoutThread.start();
        defaultRoom = roomService.getDefault();
    }
	
	/**
	 * Registers a user permanently at the system
	 * 
	 * @param username
	 * @param password
	 * @param invitationKey
	 *            (needed if invitation is required)
	 *            (needed if invitationKey is hashed!)
	 * @return boolean
	 */
	public Outcome<ChatUser> register(String username, String password, String invitationKey) {
		boolean ok = false;
		username = username != null ? username.trim() : null;
		Notice error = null;
		// Is the Username long enough?
		if (username.length() < 4) {
			error = new DefaultAlert("alert.register.nametooshort", Level.ERROR, Lifespan.NORMAL);
			logger.info(username + "'s registration rejected, username : '"	+ (username) + "' must be at least 4 chars");
		// or is it already given?
		} else if (chatUserDAO.findByName(username) != null) {
			error = new DefaultAlert("alert.register.namegiven", Level.ERROR, Lifespan.NORMAL);
			logger.info(username + "'s registration rejected, username '" + username + "' already given");
		// no problem then check if there's an invitation needed and available
		} else if (ChatConfiguration.isInvitationRequired()) {
			ChatInvitation invitation = getInvitation(username, invitationKey);
			if (invitation != null && !invitation.isTrial()) {
				ok = true;
			} else {
				error = new DefaultAlert("alert.register.invalidkey", Level.ERROR, Lifespan.NORMAL);
				logger.info(username + "'s registration revoked : " + (invitation == null ? "not invited" : "trial-invitation"));
			}
		} else {
			// no invitation required? no problem :)
			ok = true;
		}
		// all ok, lets create a new User
		if (ok) {
			logger.info("Registering User " + username);
			ChatUserEntity newUser = new ChatUserEntity();
			newUser.setName(username);
			newUser.setPassword(password);
			chatUserDAO.save(newUser);
			return new DefaultOutcome<ChatUser>(convert(newUser));
		} else {
			return new DefaultOutcome<ChatUser>(null, error);
		}
	}

	@Override
	public void logout(ChatUser user) {
		_logout(user);
	}
	
	private void _logout(ChatUser user) {
		String lcUserName = user.getUserName().trim().toLowerCase();
		names2users.remove(lcUserName);
		Room room = user.getCurrentRoom();
		if (room != null) {
			roomService.join(null, user);
		}
		_broadcast(new SystemMessage(null, 0l, MessagePreset.REFRESH));
	}

	@Override
	public boolean isActive(ChatUser user) {
		return names2users.containsValue(user);
	}
	
	public ChatUser getByName(String name) {
		ChatUserEntity userEntity = chatUserDAO.findByName(name);
		if (userEntity != null) {
			return convert(userEntity);
		} else {
			String lcUserName = name.trim().toLowerCase();
			return names2users.get(lcUserName);
		}
	}

	public Collection<ChatUser> getUsers() {
		return  names2users.values();
	}

	private String getSessionID() {
		String sessionId = Long.toHexString(ids.getAndIncrement());
		return sessionId;
	}

	public void update(ChatUser user, String color, String font, String room, boolean asyncmode) {
		ChatUserEntity userEntity = chatUserDAO.findByName(user.getUserName());
		if (userEntity != null) {
			if (isFontOk(font)) {
				userEntity.setFont(font);
				user.getSettings().setFont(font);
			}
			if (isColorOk(color)) {
				userEntity.setColor(color);
				user.getSettings().setColor(color);
			}
			if (isRoomOk(room)) {
				userEntity.setRoom(room);
				user.getSettings().setFavouriteRoom(room);
			}
			userEntity.setAsyncmode(asyncmode);
			user.getSettings().setAsyncmode(asyncmode);
			chatUserDAO.save(userEntity);
		} else {
			if (isFontOk(font)) {
				user.getSettings().setFont(font);
			}
			if (isColorOk(color)) {
				user.getSettings().setColor(color);
			}
			user.getSettings().setAsyncmode(asyncmode);
		}
	}

	public void broadcast(Message msg) {
		_broadcast(msg);
	}
	
	private void _broadcast(Message msg) {
		for (ChatUser user : getUsers()) {
			user.push(msg);
		}
	}
	
	public String createInvitation(ChatUser user, boolean trial) {
		String key =  PasswordUtil.createKey();
		invitations.put(key, new ChatInvitation(trial, user, key));
		return key;
	}

    @Override
    public Outcome<ChatUser> login(String username, String password) {
        return null;
    }

    public void changePassword(ChatUser user, String passwordHash) {
		ChatUserEntity userEntity = chatUserDAO.findOne(user.getId());
		userEntity.setPassword(passwordHash);
	}
	
	private ChatUser convert(ChatUserEntity entity) {
		// not logged in... then let's create the local user
		ChatUser result = names2users.get(entity.getName().toLowerCase());
		if (result == null) {
			result = createLocalUser(entity.getId(), entity.getName());
			result.getSettings().setColor(entity.getColor());
			result.getSettings().setFont(entity.getFont());
			result.getSettings().setFavouriteRoom(entity.getRoom());
			result.getSettings().setTrusted(entity.isTrusted());
			result.getSettings().setAsyncmode(entity.isAsyncmode());
			result.setLastLogin(entity.getLastlogin());
			result.setProfile(profileService.getProfile(result));
		}
		return result;
	}

	private ChatUser createLocalUser(Long id, String username) {
		ChatUser result;
		String sessionId = getSessionID();
		result = new DefaultChatUser(id, username, sessionId, id == null);
		names2users.put(username.toLowerCase(), result);
		result.setListenerTime(System.currentTimeMillis());
		result.setSettings(new DefaultChatUserSettings());
		return result;
	}

	private ChatInvitation getInvitation(String username, String key) {
		ChatInvitation invitation = null;
		for (String tmpKey : invitations.keySet()) {
			logger.debug("[" + tmpKey +"] versus " + key);
			if (tmpKey.equals(key)) {
				invitation = invitations.get(tmpKey);
				logger.info("Invitation found for key " + tmpKey);
				if (invitation.getInvitee() != null && !username.equals(invitation.getInvitee().getUserName())) {
					logger.warn("Someone (" + username + ") tried to reuse " + invitation.getInvitee().getUserName() + "'s invitation!");
				} else {
					invitation = invitations.get(tmpKey);
				}
			}
		}
		return invitation;
	}

	private boolean isRoomOk(String room) {
		if (room == null) {
			return true;
		} else {
			return roomService.isMainRoom(room);
		}
	}

	private boolean isFontOk(String font) {
		if (font == null) {
			return true;
		} else {
			for (char ch : font.toCharArray()) {
				if (!Character.isLetterOrDigit(ch)
						&& !Character.isWhitespace(ch)) {
					return false;
				}
			}
			return true;
		}
	}

	private static boolean isColorOk(String color) {
		if (color == null) {
			return true;
		} else {
			if (color.length() <= 8) {
				for (char ch : color.toCharArray()) {
					if (!Character.isLetterOrDigit(ch)) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}
	}
	
	@Override
	public List<ChatUser> getLoggedInUsers() {
		List<ChatUser> result = new ArrayList<ChatUser>(names2users.size());
		for (String name : names2users.keySet()) {
			result.add(names2users.get(name));
		}
		return result;
	}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ChatUserEntity chatUserEntity = chatUserDAO.findByNameIgnoreCase(username);
        if (chatUserEntity != null) {
            String name = chatUserEntity.getName();
            String password = chatUserEntity.getPassword();
            boolean enabled = true;
            boolean accountNonExpired = true;
            boolean credentialsNonExpired = true;
            boolean accountNonLocked = true;
            List<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
            authorities.add(ChatConstants.AUTHORITY_USER);
            if (chatUserEntity.isTrusted()) {
                authorities.add(ChatConstants.AUTHORITY_ADMIN);
            }
            User springUser = new User(name, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
            return springUser;
        } else {
            return null;
        }
    }

    /**
	 * An invitation to the chat
	 * @author Michi
	 *
	 */
	private class ChatInvitation {

		private final long creation;
		private final boolean trial;
		private final ChatUser inviter;
		private final String key;
		private ChatUser invitee;

		private ChatInvitation(boolean trial, ChatUser inviter, String key) {
			this.creation = System.currentTimeMillis();
			this.trial = trial;
			this.inviter = inviter;
			this.key = key;
		}

		/**
		 * Returns the LocalUser-Object to this invitation
		 * @return {@link ChatUser} if the user is present in chat
		 */
		public ChatUser getInvitee() {
			return invitee;
		}

		/**
		 * When an invitee logs in, his LocalUser-Instance will be stored here
		 * @param invitee
		 */
		public void setInvitee(ChatUser invitee) {
			this.invitee = invitee;
		}

		/**
		 * The person who invoked the Invitation
		 * @return {@link ChatUser}
		 */
		public ChatUser getInviter() {
			return inviter;
		}

		/**
		 * The invitation-Key
		 * @return {@link String}
		 */
		public String getKey() {
			return key;
		}
		
		/**
		 * The time when this invitation was invoked
		 * @return {@link Long}
		 */
		public long getCreation() {
			return creation;
		}
		
		/**
		 * Whether or not this is just a trial-invitation
		 * <br> if <b>true</b> the person may no register using this invitation!
		 * @return {@link Boolean}
		 */
		public boolean isTrial() {
			return trial;
		}
		
	}
	
	private class MyTimeoutThread extends Thread {
		
		@Override
		public void run() {
			logger.info("Timeout-Thread is running");
			while (!isInterrupted()) {
				try {
					Thread.sleep(MINUTE);
					long timeoutAgo = System.currentTimeMillis() - TIMEOUT;
					long awayTimeoutAgo = System.currentTimeMillis() - AWAY_TIMEOUT;
					Set<String> userNames = new HashSet<String>(names2users.keySet());
					for (String key : userNames) {
						ChatUser user = names2users.get(key);
						if (user != null) {
							boolean timeout = false;
							if (!user.isAway() && user.getLastActivity() < timeoutAgo) {
								timeout = true;
							} else if (user.isAway() && user.getLastActivity() < awayTimeoutAgo) {
								timeout = true;
							}
							if (timeout) {
								logger.info("Timeout for User " + user);
								user.push(new SystemMessage(null, 0l, MessagePreset.TIMEOUT));
								Thread.sleep(5000); // Timing-Problem, want to make sure the Client will fetch the Message
								_logout(user);
							}
						} else {
							names2users.remove(key);
						}
					}
					long dayAgo = System.currentTimeMillis() - A_DAY_IN_MILLIS;

					Set<String> inviKeys = new HashSet<String>(invitations.keySet());
					for (String key : inviKeys) {
						ChatInvitation invitation = invitations.get(key);
						if (invitation != null) {
							if (invitation.getCreation() <= dayAgo) {
								invitations.remove(key);
								logger.info("Clearing obsolete invitation");
							}
						} else {
							invitations.remove(key);
						}
					}
				} catch (InterruptedException ex) {
					logger.error("Error in Timeout-Thread: ", ex);
				}
			}
		}
		
	}

    @Required
    public void setChatUserDAO(ChatUserDAO chatUserDAO) {
        this.chatUserDAO = chatUserDAO;
    }

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Required
    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }
}
