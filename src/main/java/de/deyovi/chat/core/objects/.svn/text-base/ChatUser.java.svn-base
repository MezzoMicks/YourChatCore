package de.deyovi.chat.core.objects;

import java.util.Date;



public interface ChatUser {

	public Long getId();
	
	public String getUserName();
	
	public Date getLastLogin();

	public void setLastLogin(Date lastlogin);

	public Room getCurrentRoom();

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

	public String getLastInvite();

	public void setLastInvite(String name);

	public void setListenerTime(long currentTimeMillis);

	public ChatUserSettings getSettings();

	public void setSettings(ChatUserSettings settings);

	public Profile getProfile();

	public void setProfile(Profile profile);

}