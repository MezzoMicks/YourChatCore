package de.deyovi.chat.facades;

import de.deyovi.chat.core.objects.ChatUser;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Michi
 * Date: 09.11.13
 * Time: 15:39
 * To change this template use File | Settings | File Templates.
 */
public interface InputFacade {

    public void talk(ChatUser user, String message, InputStream uploadStream, String uploadName);

}
