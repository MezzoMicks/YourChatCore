package de.deyovi.chat.facades.impl;

import org.apache.log4j.Logger;

import de.deyovi.aide.Notice.Level;
import de.deyovi.aide.Outcome;
import de.deyovi.aide.impl.DefaultOutcome;
import de.deyovi.chat.core.objects.Alert;
import de.deyovi.chat.core.objects.Alert.Lifespan;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.impl.DefaultAlert;
import de.deyovi.chat.core.services.ChatUserService;
import de.deyovi.chat.core.services.impl.DefaultChatUserService;
import de.deyovi.chat.core.utils.PasswordUtil;
import de.deyovi.chat.facades.SessionFacade;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DefaultSessionFacade implements SessionFacade {

	private final static Logger logger = Logger.getLogger(DefaultSessionFacade.class);
	
	private static volatile DefaultSessionFacade instance = null;

    @Inject
	private ChatUserService chatUserService;
	
	@Override
	public Outcome<ChatUser> login(String username, String password, String sugar) {
		logger.debug("login -> user:" + username + " pass: " + password + " sugar: " + sugar);
		sugar = sugarCheck(username, sugar);
		if (sugar != null) {
			logger.debug("login -> extracted sugar :" + sugar);
			return chatUserService.login(username, password, sugar);
		} else {
			Alert error = new DefaultAlert("alert.authentication.internal", Level.ERROR, Lifespan.NORMAL);
			return new DefaultOutcome<ChatUser>(null, error);
		}
	}
	
	@Override
	public Outcome<ChatUser> register(String username, String password, String inviteKey, String sugar) {
		logger.debug("register -> user:" + username + " pass: " + password + " sugar: " + sugar);
		sugar = sugarCheck(username, sugar);
		if (sugar != null) {
			return chatUserService.register(username, password, inviteKey, sugar);
		} else {
			Alert error = new DefaultAlert("alert.authentication.internal", Level.ERROR, Lifespan.NORMAL);
			return new DefaultOutcome<ChatUser>(null, error);
		}
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
