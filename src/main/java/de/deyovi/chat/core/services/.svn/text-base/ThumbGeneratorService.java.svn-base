package de.deyovi.chat.core.services;

import java.io.IOException;
import java.util.Map;

import de.deyovi.chat.core.constants.ChatConstants.ImageSize;

/**
 * Generator-Object for Thumbnails, each Plugin may implement it's on thumbnail-generation or retrieval algorithm
 */
public interface ThumbGeneratorService {
	
	/**
	 * Generates a thumbnail for the supplied size (if possible, may also return something that's about 'similiar')
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	public Map<ImageSize, String>generate(Object source, String suffix, ImageSize... imageSizes);

}