package de.deyovi.chat.core.services;

import java.io.InputStream;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Segment;

public interface InputProcessorService {

	public Segment[] process(ChatUser user, String message, InputStream uploadStream, String uploadName);
	
}
