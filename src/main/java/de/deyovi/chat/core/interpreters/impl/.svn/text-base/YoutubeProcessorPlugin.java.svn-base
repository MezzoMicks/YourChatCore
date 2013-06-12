package de.deyovi.chat.core.interpreters.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.deyovi.chat.core.constants.ChatConstants.ImageSize;
import de.deyovi.chat.core.interpreters.InputSegmentInterpreter;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.Segment.ContentType;
import de.deyovi.chat.core.objects.impl.ThumbnailedSegment;
import de.deyovi.chat.core.services.ThumbGeneratorService;

public class YoutubeProcessorPlugin implements InputSegmentInterpreter {

	private final static Logger logger = Logger.getLogger(YoutubeProcessorPlugin.class);
	
	@Override
	public Segment[] interprete(InterpretableSegment segment) {
		Segment[] result = null;
		if (segment.isURL()) {
			URL url = segment.getURL();
			if (url.getHost().contains("youtube.com")) {
				String query = url.getQuery();
				int ixOfVideo = query != null ? query.indexOf("v=") : -1;
				if (ixOfVideo >= 0) {
					String id = query.substring(ixOfVideo + 2);
					int nextParam = id.indexOf('&');
					if (nextParam >= 0) {
						id = query.substring(0, nextParam);
					}
					result = new Segment[1];
					result[0] = new ThumbnailedSegment(segment.getUser(), null, url.toExternalForm(), ContentType.VIDEO, id, new YoutubeThumbGeneratorService());
				}
			}
		}
		return result;
	}
	
	private class YoutubeThumbGeneratorService implements ThumbGeneratorService {


		@Override
		public Map<de.deyovi.chat.core.constants.ChatConstants.ImageSize, String> generate(Object source, String suffix, de.deyovi.chat.core.constants.ChatConstants.ImageSize... imageSizes) {
			String vid = null;
			if (source instanceof String) {
				vid = (String) source;
			}
			Map<ImageSize, String> result = new HashMap<ImageSize, String>();
			for (ImageSize size : imageSizes) {
				String name;
				switch (size) {
				case PREVIEW:
					name = "http://img.youtube.com/vi/" + vid + "/0.jpg";
					break;
				case PINKY:
					name = "http://img.youtube.com/vi/" + vid + "/default.jpg";
					break;
				default:
					name = null;
					break;
				}
				result.put(size, name);
			}
			return result;
		}
		
	}
	
}
