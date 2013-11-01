package de.deyovi.chat.facades;

import de.deyovi.chat.core.objects.ChatUser;

public interface SessionFacade {

	public ChatUser login(String username, String password, String sugar);
	
	public ChatUser register(String username, String password, String inviteKey, String sugar);
	
	public void logout(ChatUser user);
	
	public String getSugar();
	
}
