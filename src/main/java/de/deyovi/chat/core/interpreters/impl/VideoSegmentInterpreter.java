package de.deyovi.chat.core.interpreters.impl;

import de.deyovi.chat.core.interpreters.InputSegmentInterpreter;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.Segment.ContentType;
import de.deyovi.chat.core.objects.impl.ThumbnailedSegment;
import de.deyovi.chat.core.services.impl.VideoThumbGeneratorService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.net.URLConnection;

@Component
public class VideoSegmentInterpreter implements InputSegmentInterpreter {

	private final static Logger logger = Logger.getLogger(VideoSegmentInterpreter.class);

    @Resource
    private VideoThumbGeneratorService thumbGeneratorService;

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
				return new Segment[] { new ThumbnailedSegment(segment.getUser(), name, segment.getURL().toExternalForm(), ContentType.VIDEO, connection, thumbGeneratorService) };
			} else {
				return null;
			}
		}
		return null;
	}
	
 }
