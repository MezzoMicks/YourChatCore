package de.deyovi.chat.core.objects.impl;

import java.util.Map;

import de.deyovi.chat.core.constants.ChatConstants.ImageSize;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.services.ThumbGeneratorService;

public class ThumbnailedSegment implements Segment {
	
	private final String username;
	private String content;
	private final ContentType type;
	private final String alternateName;
	private String preview = null;
	private String pinky = null;
	private transient Object source;
	
	public ThumbnailedSegment(String username, String title, String url, ContentType type, Object source, ThumbGeneratorService thumbGeneratorService) {
		this.username = username;
		this.content = url;
		this.alternateName = title;
		this.type = type;
		this.source = source;
		generateThumbs(thumbGeneratorService);
	}


	@Override
	public ContentType getType() {
		return type;
	}

	@Override
	public String getUser() {
		return username;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public String getPreview() {
		return preview;
	}

	@Override
	public String getPinky() {
		return pinky;
	}

	@Override
	public String getAlternateName() {
		return alternateName;
	}
	
	@Override
	public void append(String content) {
		this.content += ' ' + content;
	}
	
	private void generateThumbs(ThumbGeneratorService thumbGeneratorService) {
		if (thumbGeneratorService != null) {
			Map<ImageSize, String> generate = thumbGeneratorService.generate(source, alternateName, ImageSize.PINKY, ImageSize.PREVIEW);
			preview = generate.get(ImageSize.PREVIEW);
			pinky = generate.get(ImageSize.PINKY);
		}
	}
	
}
