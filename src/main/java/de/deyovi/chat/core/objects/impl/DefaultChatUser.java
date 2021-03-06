package de.deyovi.chat.core.objects.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.objects.Alert;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.ChatUserSettings;
import de.deyovi.chat.core.objects.Message;
import de.deyovi.chat.core.objects.MessageEventListener;
import de.deyovi.chat.core.objects.Profile;
import de.deyovi.chat.core.objects.Room;
import de.deyovi.chat.core.objects.Alert.Lifespan;

public class DefaultChatUser implements ChatUser, Comparable<ChatUser> {

	private final static Logger logger = LogManager.getLogger(DefaultChatUser.class);
	
	private final static int MAX_QUEUE_SIZE = 1000;

	private final AtomicLong messageIds = new AtomicLong(0l);
	
	private final Long id;
	private final String username;
	private final String sessionId;
	private final boolean guest;
	private final transient Queue<Message> queue = new ConcurrentLinkedQueue<Message>();
	private final Set<Alert> alerts = new TreeSet<Alert>();
	private final List<MessageEventListener> eventListener = new LinkedList<MessageEventListener>();
	private transient Room currentRoom = null;
	private String listenId = "";
	
	private boolean away = false;
	private transient long lastActivity = 0l;
	
	private String lastInvite = null;

	private String alias = null;
	private Date lastLogin;

	private ChatUserSettings settings;
	private Profile profile;
	
	public DefaultChatUser(Long id, String username, String sessionId, boolean guest) {
		this.id = id;
		this.username = username;
		this.sessionId = sessionId;
		this.guest = guest;
		alive();
	}
	
	@Override
	public Room getCurrentRoom() {
		return currentRoom;
	}

	@Override
	public void setCurrentRoom(Room room) {
		this.currentRoom = room;
	}
	
	@Override
	public int compareTo(ChatUser o) {
		if (o == null || o.getUserName() == null) {
			return 1;
		} else if (username == null) {
			return -1;
		} else {
			return username.compareTo(o.getUserName());
		}
	}

	@Override
	public String getUserName() {
		return username;
	}

	@Override
	public String toString() {
		return username;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}
	
	@Override
	public void setAway(boolean away) {
		this.away = away;
	}
	
	@Override
	public boolean isAway() {
		return away;
	}

	public void alive() {
		lastActivity = System.currentTimeMillis();
	}
	
	/* (non-Javadoc)
	 * @see de.deyovi.chat.core.objects.impl.ChatUser#getLastActivity()
	 */
	@Override
	public long getLastActivity() {
		return lastActivity;
	}
	
	public void setListenerTime(long listenerTime) {
		listenId = username + new Long(listenerTime).hashCode();
	}
	
	@Override
	public String getListenId() {
		return listenId;
	}

	@Override
	public boolean isGuest() {
		return guest;
	}
	
	@Override
	public synchronized boolean push(Message message) {
		if (queue.size() >= MAX_QUEUE_SIZE) {
			return false;
		} else {
			logger.debug("pushed Message " + message + " to user[" + this + "]");
			boolean accepted = queue.offer(new UserMessage(message, messageIds.getAndIncrement()));
			if (accepted) {
				notifyListeners();
			}
			return accepted;
		}
	}

	private void notifyListeners() {
		// do asynchronously, so we don't run into any blocking by stupid listeners
		Thread thread = new Thread() {
			public void run() {
				for (MessageEventListener listener : eventListener) {
					listener.messageRecieved(DefaultChatUser.this);
				}
			}
		};
		// fire and forget
		thread.start();
	}
	
	public Message read() {
		return queue.poll();
	}
	

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String getLastInvite() {
		return lastInvite;
	}

	@Override
	public void setLastInvite(String lastInvite) {
		this.lastInvite = lastInvite;
	}

	@Override
	public Date getLastLogin() {
		return lastLogin;
	}

	@Override
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	@Override
	public ChatUserSettings getSettings() {
		return settings;
	}

	@Override
	public void setSettings(ChatUserSettings settings) {
		this.settings = settings;
	}

	@Override
	public Profile getProfile() {
		return profile;
	}

	@Override
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	@Override
	public void addMessageEventListener(MessageEventListener listener) {
		eventListener.add(listener);
	}
	
	@Override
	public void removeMessageEventListener(MessageEventListener listener) {
		eventListener.remove(listener);
	}
	
	@Override
	public Collection<Alert> getAlerts() {
		List<Alert> result = new ArrayList<Alert>(alerts);
		for (Alert alert : result) {
			if (alert.getLifespan() != Lifespan.PERMANENT) {
				alerts.remove(alert);
			}
		}
		return result;
	}
	
	@Override
	public void addAlert(Alert alert) {
		alerts.add(alert);
	}
	
	@Override
	public void removeAlert(Alert alert) {
		alerts.remove(alert);
	}

	/**
	 * Private Implementation of a Message which can be supplied with an id and may acts duplicate of the original message
	 * @author Michi
	 *
	 */
	private class UserMessage extends AbstractMessage {

		private final Message original;
		private final long id;
		
		public UserMessage(Message original, long id) {
			super(original.getSegments());
			this.original = original;
			this.id = id;
		}
		
		@Override
		public long getID() {
			return id;
		}

		@Override
		public ChatUser getOrigin() {
			return original.getOrigin();
		}
		
		@Override
		public int getCode() {
			return original.getCode();
		}
		
	}


	@Override
	public Long getId() {
		return id;
	}

}
