package de.deyovi.chat.core.objects;

import java.util.Date;



public interface ChatUser {

	/**
	 * The unique ID of this User
	 * @return id
	 */
	public Long getId();
	
	/**
	 * The Name of this User
	 * @return username
	 */
	public String getUserName();
	
	/**
	 * Returns the last known Date, when the User logged in
	 * @return last login-Date
	 */
	public Date getLastLogin();

	/**
	 * Sets the current login-date of the user 
	 * @param lastlogin
	 */
	public void setLastLogin(Date lastlogin);

	/**
	 * Returns the room this User is currently in
	 * @return {@link Room}
	 */
	public Room getCurrentRoom();

	/**
	 * Sets the Room this User is currently in
	 * @param room
	 * @param room
	 */
	public void setCurrentRoom(Room room);

	public boolean isAway();

	public void setAway(boolean away);

	public void alive();

	public long getLastActivity();

	public boolean isGuest();

	public boolean push(Message message);

	public String getAlias();
	
	public void setAlias(String alias);

	public String getListenId();
	
	public String getSessionId();

	/**
	 * The last room name, this user was invited to
	 * @return String roomname or null
	 */
	public String getLastInvite();

	/**
	 * Sets the last retrieved invitation (roomname) to a Chat-Room<br>
	 * Needed for convencie-Method
	 * @param name
	 */
	public void setLastInvite(String name);

	public void setListenerTime(long currentTimeMillis);

	/**
	 * Getter for {@link ChatUserSettings}-Object of this User
	 * @return {@link ChatUserSettings}
	 */
	public ChatUserSettings getSettings();

	/**
	 * Setter for {@link ChatUserSettings}-Object of this User
	 * @param settings
	 */
	public void setSettings(ChatUserSettings settings);

	/**
	 * Getter for {@link Profile}-Object for this User
	 * @return
	 */
	public Profile getProfile();

	/**
	 * Setter for {@link Profile}-Object for this User
	 * @param profile
	 */
	public void setProfile(Profile profile);

	/**
	 * Reads the current (in-chat) message for this User (fifo-principal)
	 * @return {@link Message}
	 */
	public Message read();

	/**
	 * adds a {@link MessageEventListener}-Instance to this object
	 * @param listener
	 */
	public void addMessageEventListener(MessageEventListener listener);

	/**
	 * Removes a {@link MessageEventListener}-Instance from this object
	 * @param listener
	 */
	public void removeMessageEventListener(MessageEventListener listener);
	
}