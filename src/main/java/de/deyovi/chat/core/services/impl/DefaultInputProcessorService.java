package de.deyovi.chat.core.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.deyovi.chat.core.constants.ChatConstants.ChatCommand;
import de.deyovi.chat.core.constants.ChatConstants.MessagePreset;
import de.deyovi.chat.core.interpreters.InputSegmentInterpreter;
import de.deyovi.chat.core.interpreters.InputSegmentInterpreter.InterpretableSegment;
import de.deyovi.chat.core.interpreters.impl.ImageSegmentInterpreter;
import de.deyovi.chat.core.interpreters.impl.VideoSegmentInterpreter;
import de.deyovi.chat.core.interpreters.impl.YoutubeProcessorPlugin;
import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.Segment.ContentType;
import de.deyovi.chat.core.objects.impl.SystemMessage;
import de.deyovi.chat.core.objects.impl.TextSegment;
import de.deyovi.chat.core.objects.impl.ThumbnailedSegment;
import de.deyovi.chat.core.services.CommandProcessorService;
import de.deyovi.chat.core.services.FileStoreService;
import de.deyovi.chat.core.services.InputProcessorService;
import de.deyovi.chat.core.services.ThumbGeneratorService;
import de.deyovi.chat.core.utils.ChatConfiguration;

public class DefaultInputProcessorService implements InputProcessorService {

	private final static Logger logger = LogManager.getLogger(DefaultInputProcessorService.class);
	
	/*
	 * RegExp for URL-Identification taken from Justin Saunders@regexlib.com
	 * Thanks :)
	 */
	private final static String URL_REGEXP_START = "^(((ht|f)tp(s?))\\://)?(www.|[a-zA-Z].)[a-zA-Z0-9\\-\\.]+\\.(";
	private final static String[] TLD_LIST = new String[] {
		"com" , "de", "xxx", "edu", "gov", "mil", "net", "org", "biz", "info", "name", "museum", "us", "ca", "uk", "tv"
	};
	private final static String URL_REGEXP_END = ")(\\:[0-9]+)*(/($|[a-zA-Z0-9\\.\\,\\;\\?\\'\\\\\\+&amp;%\\$#\\=~_\\-]+))*$";
	private static Pattern URL_REGEXP_PATTERN;
	
	private final static MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
	private final static TreeMap<Integer, InputSegmentInterpreter> processorMap = new TreeMap<Integer, InputSegmentInterpreter>();
	static {
		mimeTypesMap.addMimeTypes("video/3gp 3gp 3GP");
		InputSegmentInterpreter imageProcessorPlugin = new ImageSegmentInterpreter();
		processorMap.put(1, imageProcessorPlugin);
		int i = 101;
		for (String pluginClass : ChatConfiguration.getExtraPlugins()) {
			InputSegmentInterpreter plugin = null;
			try {
				
				Class<?> loadedClass = imageProcessorPlugin.getClass().getClassLoader().loadClass(pluginClass);
				Constructor<?>[] constructors = loadedClass.getConstructors();
				for (Constructor<?> constructor : constructors) {
					plugin = (InputSegmentInterpreter) constructor.newInstance();
					break;
				}
				logger.info("Adding Plugin: " + plugin.getClass().getName());
				processorMap.put(i++, plugin);
			} catch (Exception e) {
				logger.error("Error, while loading Plugins", e);
			}
		}
		processorMap.put(Integer.MAX_VALUE - 1, new VideoSegmentInterpreter());
		
		String tlds = "";
		
		for (i = 0; i < TLD_LIST.length; i++) {
			if (i != 0) {
				tlds += "|";
			}
			tlds += TLD_LIST[i];
		}
		URL_REGEXP_PATTERN = Pattern.compile(tlds, Pattern.CASE_INSENSITIVE);
//		processorMap.put(Integer.MAX_VALUE, new WebisteSe());
	}
	
	private static InputProcessorService _instance = null;
	
	public static InputProcessorService getInstance() {
		if (_instance == null) {
			createInstance();
		}
		return _instance;
	}
	
	private synchronized static void createInstance() {
		if (_instance == null) {
			_instance = new DefaultInputProcessorService();
		}
	}
	
	private DefaultInputProcessorService() {
		// Hidden
	}

	private CommandProcessorService commandProcessorService = DefaultCommandProcessorService.getInstance();
	private FileStoreService fileStoreService = DefaultFileStoreService.getInstance();
	
	@Override
	public Segment[] process(ChatUser user, String message, InputStream uploadStream, String uploadName) {
		logger.debug("processing segments for user[" + user + "] message[" + message + "] upload[" + uploadName + "]");
		boolean processed = false;
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
			ChatCommand cmd = ChatCommand.getByCmd(cmdprefix);
			// if not
			if (cmd == null) {
				// ... tell someone about it
				logger.info("Unknown command[" + cmdprefix + "] from user[" + user + "]");
				user.push(new SystemMessage(null, 0l, MessagePreset.UNKNOWN_COMMAND, cmdprefix));
			} else {
				// ... else if yes... 
				// get the commands payload
				String payload = null;
				if (whitespace > 0) {
					payload = message.substring(whitespace);
				}
				logger.info("Command[" + cmdprefix + "] from user[" + user + "]");
				// and process it
				commandProcessorService.process(user, cmd, payload, uploadStream, uploadName);
			}
			processed = true;
		}
		Segment[] segmentsArray = null;
		// now is there stuff left to do?
		if (!processed) {
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
        // Attempt to convert each item into an URL.   
        for(String item : parts ) {
        	logger.debug("checking segment " + item);
        	Segment[] newSegments = null;
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
				if (previous != null && previous.getType() == ContentType.TEXT) {
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
					thumbGeneratorService= new VideoThumbGeneratorService();
				} else {
					type = ContentType.IMAGE;
					// Create Thumbnail
					thumbGeneratorService = new ImageThumbGeneratorService();
				}
				result = new ThumbnailedSegment(username, uploadname, "data/" + filename, type, bis, thumbGeneratorService);
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
	
}
