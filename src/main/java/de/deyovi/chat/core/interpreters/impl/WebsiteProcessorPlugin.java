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
import de.deyovi.chat.core.objects.impl.ThumbnailedSegment;
import de.deyovi.chat.core.services.impl.ImageThumbGeneratorService;
import de.deyovi.chat.core.services.impl.WebsiteThumbGeneratorService;

public class WebsiteProcessorPlugin implements InputSegmentInterpreter {

	private final static Logger logger = Logger.getLogger(WebsiteProcessorPlugin.class);
	private final static int MIN_SIZE = 450 * 450;
	private final Pattern titlePattern = Pattern.compile("<title>(.*)</title>", Pattern.CASE_INSENSITIVE);
	private final Pattern imgPattern = Pattern.compile("<img[^>]*src=\"([^\"]+)\"[^>]*>", Pattern.CASE_INSENSITIVE);
	
	@Override
	public Segment[] interprete(InterpretableSegment segment) {
		if (!segment.isURL()) {
			return null;
		} 
		URLConnection connection = segment.getConnection();
		String contentType = connection.getContentType();
		if (contentType != null && contentType.startsWith("text/html")) {
			URL url = connection.getURL();
			try {
				String title = null;
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				int length = connection.getContentLength();
				StringBuilder fullText = length > 0  ? new StringBuilder(length) : new StringBuilder(100 * 1024);
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
				Matcher matcher = imgPattern.matcher(fullText.toString());
				Set<String> imageSrcs = new HashSet<String>();
				while (matcher.find()) {
					imageSrcs.add(matcher.group(1));
				}
				Set<ImageInputStream> streams = new HashSet<ImageInputStream>(imageSrcs.size());
				ImageReader bigImageReader = null;
				for (String src : imageSrcs) {
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
//					BufferedImage image = ImageIO.read(srcURL);
					if (size >= MIN_SIZE) {
						System.out.println(src + " has " + size);
					}
				}
				Segment[] result = new Segment[1];
				if (bigImageReader != null) {
					BufferedImage image = bigImageReader.read(0);
					result[0] = new ThumbnailedSegment(segment.getUser(), title, url.toExternalForm(), de.deyovi.chat.core.objects.Segment.ContentType.WEBSITE, image, new ImageThumbGeneratorService());
				} else {
					result[0] = new ThumbnailedSegment(segment.getUser(), title, url.toExternalForm(), de.deyovi.chat.core.objects.Segment.ContentType.WEBSITE, url, new WebsiteThumbGeneratorService());
				}
				for (ImageInputStream stream : streams) {
					stream.close();
				}
				return result;
			} catch (IOException e) {
				logger.error(e);
				return null;
			}
		} else {
			return null;
		}
	}
 }
