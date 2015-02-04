package de.deyovi.chat.facades.impl;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.impl.CommandSegment;
import de.deyovi.chat.core.services.CommandService;
import de.deyovi.chat.core.services.InputProcessorService;
import de.deyovi.chat.facades.InputFacade;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Michi
 * Date: 09.11.13
 * Time: 15:45
 * To change this template use File | Settings | File Templates.
 */
public class DefaultInputFacade implements InputFacade {

    private static final Logger logger = LogManager.getLogger(DefaultInputFacade.class);

    private InputProcessorService inputService;
    private CommandService commandService;


    @Override
    public void talk(ChatUser user, String message, InputStream uploadStream, String uploadName) {
        Segment[] segments = inputService.process(user, message, uploadStream, uploadName);
        if (segments != null) {
            if (segments.length == 1 && segments[0] instanceof CommandSegment) {
                CommandSegment cmd = (CommandSegment) segments[0];
                commandService.process(cmd.getChatUser(), cmd.getChatCommand(), cmd.getPayload(), cmd.getUploadStream(), cmd.getUploadName());
            } else {
                user.getCurrentRoom().talk(user, segments);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(user + " said " + message);
        }
    }

    @Override
    public void away(ChatUser user, boolean away) {
        commandService.away(user, !user.isAway());
    }

    @Override
    public void join(ChatUser user, String room) {
        commandService.join(user, room);
    }

    @Required
    public void setInputService(InputProcessorService inputService) {
        this.inputService = inputService;
    }

    @Required
    public void setCommandService(CommandService commandService) {
        this.commandService = commandService;
    }
}
