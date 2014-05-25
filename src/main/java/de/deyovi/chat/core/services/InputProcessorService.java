package de.deyovi.chat.core.services;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Segment;

import java.io.InputStream;

public interface InputProcessorService {

	Segment[] process(ChatUser user, String message, InputStream uploadStream, String uploadName);
	
}
