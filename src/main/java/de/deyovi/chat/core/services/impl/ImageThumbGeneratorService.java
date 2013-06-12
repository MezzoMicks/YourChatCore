package de.deyovi.chat.core.services.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.constants.ChatConstants.ImageSize;
import de.deyovi.chat.core.services.ThumbGeneratorService;
import de.deyovi.chat.core.utils.ChatUtils;

public class ImageThumbGeneratorService implements ThumbGeneratorService {

	private final static Logger logger = Logger.getLogger(ImageThumbGeneratorService.class);
	
	@Override
	public Map<ImageSize, String> generate(Object source, String suffix, ImageSize... imageSizes) {
		Map<ImageSize, String> result = new HashMap<ImageSize, String>(imageSizes.length);
		if (source != null) {
			try {
				String name = suffix != null ? suffix : "imagepreview.png";
				if (source instanceof BufferedImage) {
					for (ImageSize  size : imageSizes) {
						String filename = ChatUtils.createAndStoreResized(size.getPrefix(), (BufferedImage)source, name, size.getSize(), size.getSize(), null);
						result.put(size,  "data/" + filename);
					}	
				} else {
					InputStream inputStream = null;
					if (source instanceof InputStream) {
						inputStream = (InputStream) source;
					}
					if (inputStream == null) {
						if (source instanceof URLConnection) {
							inputStream = ((URLConnection) source).getInputStream();
						} else if (source instanceof URL) {
							inputStream = ((URL) source).openStream();
						} else if (source instanceof File) {
							inputStream = new FileInputStream((File) source);
						}
					}
					if (inputStream == null) {
						logger.error("couldn't open Stream for Image: " + suffix);
					} else {
						ByteArrayInputStream bis;
						if (inputStream instanceof ByteArrayInputStream) {
							bis = (ByteArrayInputStream) inputStream;
						} else {
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							IOUtils.copy(inputStream, bos);
							inputStream.close();
							inputStream = null;
							bis = new ByteArrayInputStream(bos.toByteArray());
						}
						for (ImageSize  size : imageSizes) {
							bis.reset();
							String filename = ChatUtils.createAndStoreResized(size.getPrefix(), bis, name, size.getSize(), size.getSize(), null);
							result.put(size,  "data/" + filename);
						}	
					}
				}
			} catch (IOException e) {
				logger.error("couldn't open Source: " + source, e);
			}
		}
		return result;
	}
	
}
