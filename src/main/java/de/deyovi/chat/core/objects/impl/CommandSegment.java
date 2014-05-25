package de.deyovi.chat.core.objects.impl;

import de.deyovi.chat.core.constants.ChatConstants;
import de.deyovi.chat.core.constants.ChatConstants.ChatCommand ;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Segment;

import java.io.InputStream;

/**
 * Created by michi on 24.05.14.
 */
public class CommandSegment implements Segment {

    private final ChatUser user;
    private final ChatCommand chatCommand;
    private final String payload;
    private final InputStream uploadStream;
    private final String uploadName;

    public CommandSegment(ChatUser user, ChatCommand chatCommand, String payload,InputStream uploadStream, String uploadName) {
        this.user = user;
        this.chatCommand = chatCommand;
        this.payload = payload;
        this.uploadStream = uploadStream;
        this.uploadName = uploadName;
    }

    public ChatUser getChatUser() {
        return user;
    }

    @Override
    public String getUser() {
        return user.getUserName();
    }

    @Override
    public ContentType getType() {
        return null;
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public void setContent(String content) {

    }

    @Override
    public String getPreview() {
        return null;
    }

    @Override
    public String getPinky() {
        return null;
    }

    @Override
    public String getAlternateName() {
        return null;
    }

    @Override
    public void append(String content) {

    }

    public String getUploadName() {
        return uploadName;
    }

    public InputStream getUploadStream() {
        return uploadStream;
    }

    public String getPayload() {
        return payload;
    }

    public ChatCommand getChatCommand() {
        return chatCommand;
    }
}
