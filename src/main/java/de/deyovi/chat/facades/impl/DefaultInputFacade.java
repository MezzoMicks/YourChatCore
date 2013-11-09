package de.deyovi.chat.facades.impl;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.services.InputProcessorService;
import de.deyovi.chat.core.services.impl.DefaultInputProcessorService;
import de.deyovi.chat.facades.InputFacade;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
    private static volatile DefaultInputFacade instance = null;

    public static DefaultInputFacade getInstance() {
        if (instance == null) {
            createInstance();
        }
        return instance;
    }

    private static synchronized void createInstance() {
        if (instance == null) {
            instance = new DefaultInputFacade();
        }
    }

    private final InputProcessorService inputService = DefaultInputProcessorService.getInstance();

    private DefaultInputFacade() {
    }

    @Override
    public void talk(ChatUser user, String message, InputStream uploadStream, String uploadName) {
        Segment[] segments = inputService.process(user, message, uploadStream, uploadName);
        if (segments != null) {
            user.getCurrentRoom().talk(user, segments);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(user + " said " + message);
        }
    }
}
