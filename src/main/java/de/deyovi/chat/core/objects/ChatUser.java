package de.deyovi.chat.core.objects;

import java.util.Collection;
import java.util.Date;



public interface ChatUser {

	/**
	 * The unique ID of this User
	 * @return id
	 */
	Long getId();
	
	/**
	 * The Name of this User
	 * @return username
	 */
	String getUserName();
	
	/**
	 * Returns the last known Date, when the User logged in
	 * @return last login-Date
	 */
	Date getLastLogin();

	/**
	 * Sets the current login-date of the user 
	 * @param lastlogin
	 */
	void setLastLogin(Date lastlogin);

	/**
	 * Returns the room this User is currently in
	 * @return {@link Room}
	 */
	Room getCurrentRoom();

	/**
	 * Sets the Room this User is currently in
	 * @param room
	 * @param room
	 */
	void setCurrentRoom(Room room);

	boolean isAway();

	void setAway(boolean away);

	void alive();

	long getLastActivity();

	boolean isGuest();

	boolean push(Message message);

	String getAlias();
	
	void setAlias(String alias);

	String getListenId();
	
	String getSessionId();

	/**
	 * The last room name, this user was invited to
	 * @return String roomname or null
	 */
	String getLastInvite();

	/**
	 * Sets the last retrieved invitation (roomname) to a Chat-Room<br>
	 * Needed for convencie-Method
	 * @param name
	 */
	void setLastInvite(String name);

	void setListenerTime(long currentTimeMillis);

	/**
	 * Getter for {@link ChatUserSettings}-Object of this User
	 * @return {@link ChatUserSettings}
	 */
	ChatUserSettings getSettings();

	/**
	 * Setter for {@link ChatUserSettings}-Object of this User
	 * @param settings
	 */
	void setSettings(ChatUserSettings settings);

	/**
	 * Getter for {@link Profile}-Object for this User
	 * @return
	 */
	Profile getProfile();

	/**
	 * Setter for {@link Profile}-Object for this User
	 * @param profile
	 */
	void setProfile(Profile profile);

	/**
	 * Reads the current (in-chat) message for this User (fifo-principal)
	 * @return {@link Message}
	 */
	Message read();

	/**
	 * adds a {@link MessageEventListener}-Instance to this object
	 * @param listener
	 */
	void addMessageEventListener(MessageEventListener listener);

	/**
	 * Removes a {@link MessageEventListener}-Instance from this object
	 * @param listener
	 */
	void removeMessageEventListener(MessageEventListener listener);
	
	/**
	 * With this new Alerts can be added to the users Alert-Stack
	 * @param alert
	 */
	void addAlert(Alert alert);
	
	/**
	 * Permanent Alerts may be removed using this method
	 * @param alert
	 */
	void removeAlert(Alert alert);
	
	/**
	 * Retrieves available Alerts for this User<br>
	 * Alerts should be removed from Stack by this method (except for permanent Alerts)
	 */
	Collection<Alert> getAlerts();
	
}