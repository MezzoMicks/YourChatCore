package de.deyovi.chat.core.services.impl;

import de.deyovi.chat.core.constants.ChatConstants;
import de.deyovi.chat.core.interpreters.InputSegmentInterpreter;
import de.deyovi.chat.core.interpreters.InputSegmentInterpreter.InterpretableSegment;
import de.deyovi.chat.core.interpreters.impl.ImageSegmentInterpreter;
import de.deyovi.chat.core.interpreters.impl.WebsiteProcessorPlugin;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.Segment.ContentType;
import de.deyovi.chat.core.objects.impl.CommandSegment;
import de.deyovi.chat.core.objects.impl.SystemMessage;
import de.deyovi.chat.core.objects.impl.TextSegment;
import de.deyovi.chat.core.objects.impl.ThumbnailedSegment;
import de.deyovi.chat.core.services.FileStoreService;
import de.deyovi.chat.core.services.InputProcessorService;
import de.deyovi.chat.core.services.ThumbGeneratorService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultInputProcessorService implements InputProcessorService {

	private final static Logger logger = LogManager.getLogger(DefaultInputProcessorService.class);
    /*
        * RegExp for URL-Identification taken from Justin Saunders@regexlib.com
        * Thanks :)
        */
    private final Pattern pattern = Pattern.compile("([a-z0-9-.]*)\\.([a-z]{2,5})", Pattern.CASE_INSENSITIVE);

    private final MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

    private TreeMap<Integer, InputSegmentInterpreter> processorMap;
    private VideoThumbGeneratorService videoThumbGeneratorService;
    private ImageThumbGeneratorService imageThumbGeneratorService;

    @Resource
	private FileStoreService fileStoreService;
	
	@Override
	public Segment[] process(ChatUser user, String message, InputStream uploadStream, String uploadName) {
        logger.debug("processing segments for user[" + user + "] message[" + message + "] upload[" + uploadName + "]");
        Segment[] segmentsArray = null;
        // no system-message-injections!
        if (message.startsWith("$")) {
            message = "\\" + message;
            // commands start with "/"
        } else if (message.startsWith("/")) {
            int whitespace = message.indexOf(' ');
            String cmdprefix;
            if (whitespace > 0) {
                cmdprefix = message.substring(1, whitespace);
            } else {
                cmdprefix = message.substring(1);
            }
            // Now is it a command?
            ChatConstants.ChatCommand cmd = ChatConstants.ChatCommand.getByCmd(cmdprefix);
            // if not
            if (cmd == null) {
                // ... tell someone about it
                logger.info("Unknown command[" + cmdprefix + "] from user[" + user + "]");
                user.push(new SystemMessage(null, 0l, ChatConstants.MessagePreset.UNKNOWN_COMMAND, cmdprefix));
                return null;
            } else {
                // ... else if yes...
                // get the commands payload
                String payload = null;
                if (whitespace > 0) {
                    payload = message.substring(whitespace);
                }
                logger.info("Command[" + cmdprefix + "] from user[" + user + "]");
                // and process it
                segmentsArray = new Segment[]{ new CommandSegment(user, cmd, payload, uploadStream, uploadName) };
            }
        }
        // now is there stuff left to do?
        if (segmentsArray == null) {
            // then it's about to parse the message for it's actual content
            List<Segment> segments = parseSegments(user != null ? user.getUserName() : "$system", message, uploadStream, uploadName);
            segmentsArray = segments.toArray(new Segment[segments.size()]);
        }
        return segmentsArray;
    }

    /**
     * Parses and splits an input into it's segments
     * @param input
     * @return
     */
    private List<Segment> parseSegments(String username, String input, InputStream uploadStream, String uploadName) {
        LinkedList<Segment> result = new LinkedList<Segment>();
        // Split over every whitespacey character
        String [] parts = input.split("\\s");
        for(String item : parts ) {
            logger.debug("checking segment " + item);
            Segment[] newSegments = null;
            // check if content might be a public domain
            if (!item.contains("//")) {
                // see if it looks like a public hostname
                Matcher matcher = pattern.matcher(item);
                if (matcher.find()) {
                    // if so...
                    Socket socket = new Socket();
                    boolean reachable = false;
                    try {
                        // we attempt to connect
                        socket.connect(new InetSocketAddress(item, 80), 250);
                        logger.debug("Testing assumed Host successful '" + item + "', prepending 'http://'");
                        reachable = true;
                    } catch (Exception e) {
                        logger.debug("Testing assumed Host failed '" + item + "' " + e.getMessage());
                    } finally {
                        if (socket != null) try { socket.close(); } catch(IOException e) {}
                    }
                    if (reachable) {
                        item = "http://" + item;
                    }
                }
            }
            // Let's interprete the input
            for (InputSegmentInterpreter interpreter : processorMap.values()) {
                newSegments = interpreter.interprete(new MyInterpretableSegment(username, item));
                if (newSegments != null) {
                    break;
                }
            }
            // nobody processed the segment, assume that it is text
            if (newSegments == null) {
                logger.debug("appending segment " + item + " as text");
                // if the previous segment was already a String
                Segment previous = result.isEmpty() ? null : result.getLast();
                if (previous != null && previous.getType() == Segment.ContentType.TEXT) {
                    //.. append to previous segment
                    previous.append(item);
                } else {
                    //.. create a new Segment
                    newSegments = new Segment[] { new TextSegment(username, item) };
                }
            }
            if (newSegments != null) {
                for (Segment segment : newSegments) {
                    result.add(segment);
                }
            }
        }
        // now take care of the upload!
        Segment uploadSegment = processUpload(username, uploadStream, uploadName);
        if (uploadSegment != null) {
            result.add(uploadSegment);
        }
        return result;
    }

	
	private Segment processUpload(String username, InputStream uploadStream, String uploadname) {
		Segment result = null;
		if (uploadStream != null) {
			// We only do images!
			try {
				// First copy the Stream
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				IOUtils.copy(uploadStream, bos);
				// Load retrieved data into new ByteInputStream
				ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
				String filename = fileStoreService.store(bis, uploadname);
				bis.reset();
				String contentType = mimeTypesMap.getContentType(uploadname);
				logger.info("Upload from " + username + " stored to " + filename + "[" + contentType +"]" );
				ContentType type;
				ThumbGeneratorService thumbGeneratorService;
				if (contentType.startsWith("video")) {
					type = ContentType.VIDEO;
					// Create Thumbnail
					thumbGeneratorService = videoThumbGeneratorService;
				} else {
					type = ContentType.IMAGE;
					// Create Thumbnail
					thumbGeneratorService = imageThumbGeneratorService;
				}
				result = new ThumbnailedSegment(username, uploadname, "u/" + filename, type, bis, thumbGeneratorService);
				bis.close();
				uploadStream.close();
			} catch (IOException e) {
				logger.error("Couldn't buffer upload " + uploadname, e);
			}
		}
		return result;
	}
	
	
	private class MyInterpretableSegment extends TextSegment implements InterpretableSegment {

		/**
		 * 
		 */
		private static final long serialVersionUID = 275628196564573602L;
		private Boolean urlFlag;
		private URL url = null;
		private URLConnection urlConnection = null;
		
		public MyInterpretableSegment(String user, String content) {
			super(user, content);
		}

		@Override
		public boolean isURL() {
			if (urlFlag == null) {
				getURL();
			}
			return urlFlag;
		}

		@Override
		public URL getURL() {
			if (urlFlag == null) {
				String content = getContent();
				if (content != null && !(content = content.trim()).isEmpty()) {
					if (content.startsWith("//")) {
						content = "http:" + content;
					}
					try {
						url = new URL(content);
					} catch (MalformedURLException mux) {
						url = null;
					}
					urlFlag = url != null;
				} else {
                    urlFlag = false;
                }
			}
			return url;
		}

		@Override
		public URLConnection getConnection() {
			if (urlFlag == null) {
				getURL();
			}
			if (url != null && urlConnection == null) {
				try {
					urlConnection = url.openConnection();
				} catch (IOException e) {
					logger.error(e);
				}
			}
			return urlConnection;
		}
	}

    public void setImageThumbGeneratorService(ImageThumbGeneratorService imageThumbGeneratorService) {
        this.imageThumbGeneratorService = imageThumbGeneratorService;
    }

    public void setVideoThumbGeneratorService(VideoThumbGeneratorService videoThumbGeneratorService) {
        this.videoThumbGeneratorService = videoThumbGeneratorService;
    }

    public void setProcessorMap(TreeMap<Integer, InputSegmentInterpreter> processorMap) {
        this.processorMap = processorMap;
    }
}
