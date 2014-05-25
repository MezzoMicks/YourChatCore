package de.deyovi.chat.core.interpreters.impl;

import java.net.URL;
import java.net.URLConnection;

import de.deyovi.chat.core.services.ThumbGeneratorService;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.interpreters.InputSegmentInterpreter;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.Segment.ContentType;
import de.deyovi.chat.core.objects.impl.ThumbnailedSegment;
import de.deyovi.chat.core.services.impl.ImageThumbGeneratorService;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Singleton
public class ImageSegmentInterpreter implements InputSegmentInterpreter {

	private final static Logger logger = Logger.getLogger(ImageSegmentInterpreter.class);
	
	private final static String[] EXTENSIONS = new String[] {
			"jpg", "jpeg", "png", "gif", "tif", "tiff", "bmp"
	};

    @Inject
    private ImageThumbGeneratorService thumbGeneratorService;
	
	@Override
	public Segment[] interprete(InterpretableSegment segment) {
		if (segment.isURL()) {
			String user = segment.getUser();
			String content = segment.getContent();
			logger.debug("file " + content);
			URL url = segment.getURL();
			String name = url.getFile();
			if (name == null || name.isEmpty()) {
				name = "image_" + url.getHost();
			} else {
				int ixOfSlash = name.lastIndexOf('/');
				name = name.substring(ixOfSlash + 1);
			}
			URLConnection urlConnection = segment.getConnection();
			String contentType = urlConnection.getContentType();
			if (contentType != null && contentType.startsWith("image/")) {
				return new Segment[] { new ThumbnailedSegment(user, name, content, ContentType.IMAGE, urlConnection, thumbGeneratorService)};
			} else {
				String urlAsString = content;
				String lcUrlAsString = urlAsString.toLowerCase().trim();
				for (String extension : EXTENSIONS) {
					if (lcUrlAsString.endsWith(extension)) {
						return new Segment[] { new ThumbnailedSegment(user, name, content, ContentType.IMAGE, urlConnection, thumbGeneratorService)};
					}
				}
			}
		}
		return null;
	}
	
	// TODO write Test!
//	public static void main(String[] args) throws IOException {
//		java.net.URL url = new java.net.URL("http://upload.wikimedia.org/wikipedia/commons/9/92/Colorful_spring_garden.jpg");
//		new ImageProcessorPlugin().process(url.openConnection());
//	}
	
}
