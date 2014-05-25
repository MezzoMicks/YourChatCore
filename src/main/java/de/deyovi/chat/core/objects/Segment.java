package de.deyovi.chat.core.objects;

import java.io.Serializable;

public interface Segment extends Serializable {
	
	/**
	 * The user who posted this segment (should be equal to the messages origin)
	 * @return {@link String}
	 */
	public String getUser();
	
	/**
	 * The type of this Segment
	 * @return {@link ContentType}
	 */
	public ContentType getType();
	
	/**
	 * The actual content of this Segment
	 * @return String
	 */
	public String getContent();


    public void setContent(String content);
	
	/**
	 * Thumbnail for content, if present
	 * @return URL to thumbnail or null
	 */
	public String getPreview();
	
	/**
	 * Thumbnail for content, if present
	 * @return URL to thumbnail or null
	 */
	public String getPinky();
	
	
	public String getAlternateName();
	
	public void append(String content);
	
	/**
	 * The type of content, which a Segment may have
	 * @author Michi
	 */
	public enum ContentType {

		/**
		 * The type of content is unknown
		 */
		UNKNOWN,
        /**
         * The Segment represents a Command (to be further processed)
         */
        COMMAND,
		/**
		 * normal Text should be marked as this
		 */
		TEXT, 
		/**
		 * JPG/PNG/GIF..
		 */
		IMAGE, 
		/**
		 * Embedded Videos, FLVs, MPG...
		 */
		VIDEO, 
		/**
		 * URLs which direct to plain old Websites
		 */
		WEBSITE,
		/**
		 * PDF/TXT/DOC-Links should be this
		 */
		DOCUMENT,
		/**
		 * Roomprotocols are marked this way
		 */
		PROTOCOL

	}
	
}