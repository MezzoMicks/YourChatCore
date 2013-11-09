package de.deyovi.chat.core.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.Segment.ContentType;
import de.deyovi.chat.core.services.MessageConsumer;
import de.deyovi.chat.core.services.OutputService.OutputMeta;
import de.deyovi.chat.core.services.TranslatorService;
import de.deyovi.chat.core.utils.ChatUtils;

public class HTMLMessageConsumer implements MessageConsumer {

	private final static Logger logger = LogManager.getLogger(HTMLMessageConsumer.class);
	
	private static final String ANCHOR_PLAIN = "<a id=\"%4$s\" target=\"_blank\" href=\"%1$s\"><i class=\"%3$s\" />&nbsp;%2$s</a>";

	private static final String ANCHOR_THUMBED = "<a id=\"%6$s\" target=\"_blank\" href=\"%1$s\" data-preview=\"%3$s\" data-pinky=\"%4$s\"><i class=\"%5$s\" />&nbsp;%2$s</a><script type=\"text/javascript\">tooltipify($('#%6$s'));</script>";

	private static final String STOP_SCRIPT = "<script type=\"text/javascript\">"
			+ //
			"	stop();" + //
			"</script>";

	private static final String REFRESH_SCRIPT = "<script type=\"text/javascript\">"
			+ //
			"	refresh();" + //
			"</script>";

	private static final String PROFILE_SCRIPT = "<script type=\"text/javascript\">"
			+ //
			"	openProfile('%s');" + //
			"</script>";

	public static final String USER_TAG = "<span class=\"username\" style=\"color:#%s\">"
			+ //
			"%s" + //
			"</span>";

	public static final String USER_ALIAS_TAG = "<span style=\"color:#%s\" data-animation=\"false\" data-title=\"%s\" data-placement=\"right\" onmouseover=\"toolify(this);\" class=\"useralias\">"
			+ //
			"%s" + //
			"</span>";
	

	private final static AtomicLong ANCHOR_COUNT = new AtomicLong();

	private final TranslatorService translatorService = ResourceTranslatorService.getInstance();
	
	private final Appendable target;
	
	private boolean refresh = false;
	private boolean stop = false;

	public HTMLMessageConsumer() {
		this(null);
	}
	
	public HTMLMessageConsumer(Appendable target) {
		if (target != null) {
			this.target = target;
		} else {
			this.target = new StringBuilder();
		}
	}
	
	private String decorateText(String text) {
		text = ChatUtils.escape(text);
		text = makeCursive(text);
		return text;
	}
	
	private String decorateLink(Segment seg) {
		String content;
		String text = seg.getAlternateName() != null ? seg.getAlternateName() : seg.getContent();
		text =  ChatUtils.escape(text);
		String typeClass = "icon-";
		switch (seg.getType()) {
		case IMAGE:
			typeClass += "picture";
			break;
		case DOCUMENT:
			typeClass += "text";
			break;
		case VIDEO:
			typeClass += "film";
			break;
		case WEBSITE:
			typeClass += "globe";
			break;
		default:
		case UNKNOWN:
			typeClass += "question";
			break;
		}
		String anchorID = "linkNo" + ANCHOR_COUNT.getAndIncrement();
		content = seg.getContent();
		if (seg.getPreview() != null) {
			text = StringEscapeUtils.escapeHtml4(text);
			content = String.format(ANCHOR_THUMBED, 
							content, text, seg.getPreview(),
							seg.getPinky(),typeClass,anchorID);
		} else {
			content = String.format(ANCHOR_PLAIN,
							content, text, typeClass, anchorID);
		
		}
		return content;
	}
	
	public void consume(Segment[] segments, Locale locale, OutputMeta meta) {
		String profile = null;
		try {
			target.append("<p>");

			ChatUser origin = meta.getOrigin();
			if (origin != null) {
				String name = StringEscapeUtils.escapeHtml4(origin.getUserName());
				String color = origin.getSettings().getColor();
				if (origin.getAlias() != null) {
					name = String.format(USER_ALIAS_TAG, color, name,
							StringEscapeUtils.escapeHtml4(origin.getAlias()));
				} else {
					name = String.format(USER_TAG, color, name);
				}
				target.append(name);
				target.append(':');
			}
			
			for (Segment seg : segments) {
				String content = seg.getContent();
				if (seg.getType() == ContentType.TEXT) {
					if (content.charAt(0) == '$') {
						if (content.startsWith("$PROFILE_OPEN")) {
							List<String> parsedMessage = translatorService.parse(content);
							profile = parsedMessage.get(1);
							content = translatorService.translate(parsedMessage, locale);
						} else {
							content = translatorService.translate(content, locale);
						}		
					}
					content = decorateText(content);
				} else {
					content = decorateLink(seg);
				}
                if (content != null) {
                    target.append(content);
                    target.append(' ');
                }
			}
		
			target.append("</p>\n");
			if (profile != null) {
				target.append(String.format(PROFILE_SCRIPT, profile));
			}
			refresh |= meta.isRefreshRequired();
			stop |= meta.isInterrupted();
		} catch (IOException e) {
			logger.error("Error while writing to target", e);
		}
	}
	
	@Override
	public void finish() {
		try {
			if (refresh) {
				target.append(REFRESH_SCRIPT);
			} else if (stop) {
				target.append(STOP_SCRIPT);
			}
		} catch (IOException e) {
			logger.error("Error while writing to target", e);
		}
	}
	
	@Override
	public String getResult() {
		return target.toString();
	}

	private static String makeCursive(String content) {
		int ixOfAst;
		boolean iOpen = false;
		int offset = 0;
		while ((ixOfAst = content.indexOf('*', offset)) >= 0) {
			offset = ixOfAst + 1;
			// if there's no open i-tag
			if (!iOpen) {
				// and the next char is 'the end'
				char next;
				if (content.length() <= (ixOfAst + 1)) {
					// skip this one
					continue;
					// or is a whitespace or another asterisk
				} else if ((next = content.charAt(ixOfAst + 1)) == '*'
						|| Character.isWhitespace(next)) {
					// skip this one
					continue;
				}
			}
			String before = content.substring(0, ixOfAst);
			String after = content.substring(ixOfAst + 1);
			content = before;
			if (iOpen) {
				content += "</i>";
				iOpen = false;
				offset += 3;
			} else {
				content += "<i>";
				iOpen = true;
				offset += 2;
			}
			content += after;
		}
		if (iOpen) {
			content += "</i>";
		}
		return content;
	}

	
}
