package de.deyovi.chat.core.services;

import de.deyovi.chat.core.constants.ChatConstants.ImageSize;

import java.io.IOException;
import java.util.Map;

/**
 * Generator-Object for Thumbnails, each Plugin may implement it's on thumbnail-generation or retrieval algorithm
 */
public interface ThumbGeneratorService {
	
	/**
	 * Generates a thumbnail for the supplied size (if possible, may also return something that's about 'similiar')
	 * @param source
	 * @param suffix
     * @param imageSizes
	 * @return
	 * @throws IOException
	 */
	Map<ImageSize, String> generate(Object source, String suffix, ImageSize... imageSizes);

}