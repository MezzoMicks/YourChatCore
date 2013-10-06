package de.deyovi.chat.core.interpreters.impl;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;

import de.deyovi.chat.core.interpreters.InputSegmentInterpreter;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.Segment.ContentType;
import de.deyovi.chat.core.objects.impl.DefaultSegment;
import de.deyovi.chat.core.objects.impl.ThumbnailedSegment;
import de.deyovi.chat.core.services.impl.ImageThumbGeneratorService;
import de.deyovi.chat.core.services.impl.WebsiteThumbGeneratorService;

public class WebsiteProcessorPlugin implements InputSegmentInterpreter {

	private final static Logger logger = Logger.getLogger(WebsiteProcessorPlugin.class);
	private final static int MIN_SIZE = 450 * 450;
	private final static int MAX_IMAGE_GETTER_ERRORS = 3;
	private final Pattern titlePattern = Pattern.compile("<title>(.*)</title>", Pattern.CASE_INSENSITIVE);
	private final Pattern imgPattern = Pattern.compile("<img[^>]*src=\"([^\"]+)\"[^>]*>|background(-image)?:url\\('?(.*?)'?\\).*?;", Pattern.CASE_INSENSITIVE);
	
	@Override
	public Segment[] interprete(InterpretableSegment segment) {
		if (!segment.isURL()) {
			return null;
		} 
		URLConnection connection = segment.getConnection();
		String contentType = connection.getContentType();
		// we only go there, if it's html what's coming
		if (contentType != null && contentType.startsWith("text/html")) {
			URL url = connection.getURL();
			String title = null;
			StringBuilder fullText = null;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				int length = connection.getContentLength();
				fullText = length > 0  ? new StringBuilder(length) : new StringBuilder(100 * 1024);
				while ((line = br.readLine()) != null) {
					Matcher matcher = titlePattern.matcher(line);
					if (matcher.find()) {
						String group = matcher.group(1);
						if (group.length() > 40) {
							title = group.substring(0, 39) + "...";
						} else {
							title = group;
						}
					}
					fullText.append(line);
				}
			} catch (IOException e) {
				logger.error("Error, while reading from URL " + url.toString(), e);
			}
			Segment[] result = new Segment[1];
			if (fullText == null) {
				result[0] = new DefaultSegment(segment.getUser(), segment.getContent(), ContentType.UNKNOWN, null, null, null);
			} else {
				Matcher matcher = imgPattern.matcher(fullText.toString());
				Set<String> imageSrcs = new HashSet<String>();
				while (matcher.find()) {
					String group = matcher.group(1);
					if (group != null) {
						imageSrcs.add(group);
					} else {
						imageSrcs.add(matcher.group(3));
					}
				}
				Set<ImageInputStream> streams = new HashSet<ImageInputStream>(imageSrcs.size());
				ImageReader bigImageReader = null;
				int imageGetterErrors = 0;
				for (String src : imageSrcs) {
					try {
						URL srcURL;
						// Fully qualified?
						if (src.startsWith("http://")) {
							srcURL = new URL(src);
						// also, but without protocol?
						} else if (src.startsWith("//")) {
							srcURL = new URL("http:" + src);
						//  absolute serverpath?
						} else if (src.startsWith("/")) {
							srcURL = new URL(url.getProtocol() + "://" + url.getHost() + src);
						// relative serverpath?
						} else {
							String parent = url.getPath();
							int lastIxOfSlash = parent.lastIndexOf('/');
							if (lastIxOfSlash > 0) {
								parent = parent.substring(0, lastIxOfSlash);
							}
							srcURL = new URL(url.getProtocol() + "://" + url.getHost() + "/" + parent + "/" + src);
						}
						// Fetch the Image and evaluate it's size!
						ImageInputStream in = ImageIO.createImageInputStream(srcURL.openStream());
						streams.add(in);
						int size = -1;
						final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
						if (readers.hasNext()) {
							ImageReader reader = readers.next();
							reader.setInput(in);
							size = reader.getWidth(0) * reader.getHeight(0);
							if (size > MIN_SIZE) {
								if (bigImageReader != null) {
									logger.debug("More than one big image found!");
									bigImageReader.dispose();
									bigImageReader = null;
									break;
								} else {
									logger.debug("Big image found... taking it");
									bigImageReader = reader;
								}
							} else {
								reader.dispose();
							}
						}
						if (size >= MIN_SIZE) {
							System.out.println(src + " has " + size);
						}
					} catch (IOException e) {
						logger.error(e);
						imageGetterErrors++;
					}
					// To many problems?
					if (imageGetterErrors >= MAX_IMAGE_GETTER_ERRORS) {
						// assume we won't get any pictures this way!
						break;
					}
				}
				// Did we find a big image to read?
				if (bigImageReader != null) {
					try {
						BufferedImage image;
						// then let's try to read that one 
						image = bigImageReader.read(0);
						// and create thumbnails for it
						result[0] = new ThumbnailedSegment(segment.getUser(), title, url.toExternalForm(), de.deyovi.chat.core.objects.Segment.ContentType.WEBSITE, image, new ImageThumbGeneratorService());
					} catch (IOException e) {
						logger.error("Error while reading assumed 'big image'", e);
					} finally {
						bigImageReader.dispose();
					}
				}
				// close all the used streams
				try {
					for (ImageInputStream stream : streams) {
						stream.close();
					}
				} catch (IOException e) {
					logger.error("Error while closing used streams", e);
				}
				// no thumbnail yet
				if (result[0] == null) {
					// we will do it the phantom-js-way, screening the webpage
					result[0] = new ThumbnailedSegment(segment.getUser(), title, url.toExternalForm(), de.deyovi.chat.core.objects.Segment.ContentType.WEBSITE, url, new WebsiteThumbGeneratorService());
				}
			}
			return result;
		} else {
			return null;
		}
	}
	
 }
