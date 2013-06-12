package de.deyovi.chat.core.interpreters.impl;

import java.io.File;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import de.deyovi.chat.core.interpreters.InputSegmentInterpreter;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.Segment.ContentType;
import de.deyovi.chat.core.objects.impl.ThumbnailedSegment;
import de.deyovi.chat.core.services.ThumbGeneratorService;
import de.deyovi.chat.core.services.impl.VideoThumbGeneratorService;

public class VideoSegmentInterpreter implements InputSegmentInterpreter {

	private final static Logger logger = Logger.getLogger(VideoSegmentInterpreter.class);
	
	@Override
	public Segment[] interprete(InterpretableSegment segment) {
		if (segment.isURL()) {
			URLConnection connection = segment.getConnection();
			String contentType = connection.getContentType();
			if (contentType != null && contentType.startsWith("video")) {
				String name = connection.getURL().getFile();
				if (name == null || name.isEmpty()) {
					name = "video_" + segment.getURL().getHost();
				} else {
					int ixOfSlash = name.lastIndexOf(File.separatorChar);
					name = name.substring(ixOfSlash + 1);
				}
				ThumbGeneratorService thumbGenerator = new VideoThumbGeneratorService();
				return new Segment[] { new ThumbnailedSegment(segment.getUser(), name, segment.getURL().toExternalForm(), ContentType.VIDEO, connection, thumbGenerator) };
			} else {
				return null;
			}
		}
		return null;
	}
	
 }
