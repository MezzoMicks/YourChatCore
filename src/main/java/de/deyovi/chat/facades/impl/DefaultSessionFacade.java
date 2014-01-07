package de.deyovi.chat.facades.impl;

import org.apache.log4j.Logger;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.services.ChatUserService;
import de.deyovi.chat.core.services.impl.DefaultChatUserService;
import de.deyovi.chat.core.utils.PasswordUtil;
import de.deyovi.chat.facades.SessionFacade;

public class DefaultSessionFacade implements SessionFacade {

	private final static Logger logger = Logger.getLogger(DefaultSessionFacade.class);
	
	private static volatile DefaultSessionFacade instance = null;
	
	private final ChatUserService chatUserService = DefaultChatUserService.getInstance();
	
	private DefaultSessionFacade() {
		// hidden
	}
	
	public static DefaultSessionFacade getInstance() {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}
	
	private static synchronized void createInstance() {
		if (instance == null) {
			instance = new DefaultSessionFacade();
		}
	}
	
	@Override
	public ChatUser login(String username, String password, String sugar) {
		ChatUser newUser;
		logger.debug("login -> user:" + username + " pass: " + password + " sugar: " + sugar);
		sugar = sugarCheck(username, sugar);
		if (sugar != null) {
			newUser = chatUserService.login(username, password, sugar);
			logger.debug("user: " + username + " sucessfully logged in");
		} else {
			newUser = null;
		}
		return newUser;
	}
	
	@Override
	public ChatUser register(String username, String password, String inviteKey, String sugar) {
		logger.debug("register -> user:" + username + " pass: " + password + " sugar: " + sugar);
		ChatUser newUser = chatUserService.register(username, password, inviteKey, sugar);
		return newUser;
	}
	
	@Override
	public void logout(ChatUser user) {
		logger.info(user + " logged out");
		chatUserService.logout(user);
	}
	
	@Override
	public String getSugar() {
		return PasswordUtil.getSugar();
	}
	
	private String sugarCheck(String username, String sugar) {
		if (sugar != null && sugar.endsWith(username)) {
			return sugar.substring(0, sugar.length() - username.length());
		} else {
			logger.error("Sugar didn't match user: got username '" + username + "' expected sugar '" + sugar + "'. Mixed up sessions?");
			return null;
		}
	}
}
