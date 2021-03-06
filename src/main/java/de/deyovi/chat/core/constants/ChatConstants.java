package de.deyovi.chat.core.constants;



public class ChatConstants {
	

	public final static int MESSAGE_EVENT_NONE = 0;
	public final static int MESSAGE_EVENT_STOP = 1;
	public final static int MESSAGE_EVENT_REFRESH = 2;


	/**
	 * Enum of commands which can be triggered from within normal messages
	 * @author Michi
	 *
	 */
	public enum ChatCommand {

		WHISPER("whisper", "w", "fluester", "f"),
		SEARCH("search", "suche"),
		JOIN("join", "j"),
		IGNORE("ignore", "ig"),
		INVITE("invite", "si"),
		NEWROOM("new", "sepnew", "sn"),
		OPENROOM("open", "sepopen"),
		CLOSEROOM("lock", "seplock"),
		AWAY("away"),
		BACKGROUND("background", "bg"),
		FOREGROUND("foreground", "fg"), 
		WHO("wer", "who", "werc", "whoc"),
		PROFILE("profile", "id", "about"),
		MOTD("motd"),
		CLEAR("clear"),
		ALIAS("alias", "aka"),
		LOGOUT("logout", "bye"),
		;
		
		private final String[] cmds;
		
		private ChatCommand(String... cmds) {
			this.cmds = cmds;
		}
		
		/**
		 * Retrieves a {@link ChatCommand} for the supplied String
		 * @param cmd
		 * @return {@link ChatCommand}
		 */
		public static ChatCommand getByCmd(String cmd) {
			for (ChatCommand value : values()) {
				for (String tmp : value.cmds) {
					if (tmp.equals(cmd)) {
						return value;
					}
				}
			}
			return null;
		}
		
	}
	
	public enum MessagePreset {
		SYSTEM(-1, ""),
		DEFAULT(0, ""),
		SETTINGS(10, "$SETTINGS", MESSAGE_EVENT_REFRESH),
		DUPLICATESESSION(50, "$DUPLICATESESSION", MESSAGE_EVENT_STOP),
		REFRESH(51, null, MESSAGE_EVENT_REFRESH),
		TIMEOUT(75, "$TIMEOUT", MESSAGE_EVENT_STOP),
		UNKNOWN_COMMAND(99, "$UNKNOWN_COMMAND{cmd=%s}"),
		WELCOME(100, "$WELCOME{user=%s}"), 
		MOTD(101, "$MOTD"),
		CLEAR_LOG(102, "$CLEAR_LOG", MESSAGE_EVENT_REFRESH),
		CLEAR_MEDIA(103, "$CLEAR_MEDIA", MESSAGE_EVENT_REFRESH),
		SWITCH_CHANNEL(201, "$SWITCH_CHANNEL{channel=%s}{background=%s}", MESSAGE_EVENT_REFRESH),
		UNKNOWN_CHANNEL(202, "$UNKNOWN_CHANNEL{channel=%s}"),
		JOIN_CHANNEL(203,"$JOIN_CHANNEL{user=%s}", MESSAGE_EVENT_REFRESH),
		LEFT_CHANNEL(204, "$LEFT_CHANNEL{user=%s}", MESSAGE_EVENT_REFRESH),
		CHANNEL_NOTALLOWED(205, "$CHANNEL_NOTALLOWED{channel=%s}"),
		USER_AWAY(206, "$USER_AWAY{user=%s}", MESSAGE_EVENT_REFRESH),
		USER_BACK(207, "$USER_BACK{user=%s}", MESSAGE_EVENT_REFRESH),
		USER_ALIAS_SET(208, "$USER_ALIAS_SET{user=%s}{alias=%s}", MESSAGE_EVENT_REFRESH),
		USER_ALIAS_CLEARED(209, "$USER_ALIAS_CLEARED{user=%s}", MESSAGE_EVENT_REFRESH),
		CREATE_NOGUEST(210, "$CREATE_NOGUEST"),
		CREATE_NAMEGIVEN(211, "$CREATE_NAMEGIVEN{channel=%s}"),
		CREATE_DONE(212, "$CREATE_DONE{channel=%s}", MESSAGE_EVENT_REFRESH),
		OPEN_CHANNEL(213, "$OPEN_CHANNEL{user=%s}", MESSAGE_EVENT_REFRESH),
		OPEN_CHANNEL_ALREADY(214, "$OPEN_CHANNEL_ALREADY"),
		CLOSE_CHANNEL(215, "$CLOSE_CHANNEL{user=%s}", MESSAGE_EVENT_REFRESH),
		CLOSE_CHANNEL_ALREADY(216, "$CLOSE_CHANNEL_ALREADY"),
		CHANNEL_BG_CHANGED(217, "$CHANNEL_BG_CHANGED{user=%s}", MESSAGE_EVENT_REFRESH),
		CHANNEL_FG_CHANGED(218, "$CHANNEL_FG_CHANGED{user=%s}", MESSAGE_EVENT_REFRESH),
		CHANNEL_PRIVATE(219, "$CHANNEL_PRIVATE{room=%s}"),
		INVITE_NOGUEST(220, "$INVITE_NOGUEST"),
		INVITE_USER(221, "$INVITE_USER{user=%s}{channel=%s}"),
		INVITETO_USER(222, "$INVITETO_USER{user=%s}"),
		INVITETO_USER_ALREADY(223, "$INVITETO_USER_ALREADY{user=%s}"),
		MOTD_NOTALLOWED(224, "$MOTD_NOTALLOWED"),
		MOTD_SET(225, "$MOTD_SET{user=%s}"),
		UNKNOWN_USER(300, "$UNKNOWN_USER{user=%s}"),
		WHISPER(301, "$WHISPER{user=%s}"),
		WHISPERTO(302, "$WHISPERTO{user=%s}"), 
		MESSAGE(303, "$MESSAGE{user=%s}{subject=%s}"),
		USER_NOT_LOGGED_IN(310, "$USER_NOT_LOGGED_IN{user=%s}"),
		SEARCH_NOWHERE(311, "$SEARCH_NOWHERE{user=%s}"),
		SEARCH_PRIVATE(312, "$SEARCH_PRIVATE{user=%s}"),
		SEARCH_CHANNEL(313, "$SEARCH_CHANNEL{user=%s}{channel=%s}"),
		SEARCH_LAST(314, "$SEARCH_LAST{user=%s}{date=%s}{time=%s}"),
		PROFILE_OPEN(315, "$PROFILE_OPEN{user=%s}"),
		
		;
		
		private final int code;
		private final String content;
		private final int event;
		
		private MessagePreset(int code, String content) {
			this(code, content, MESSAGE_EVENT_NONE);
		}
		

		private MessagePreset(int code, String content, int event) {
			this.code = code;
			this.content = content;
			this.event = event;
		}
		
		public int getCode() {
			return code;
		}
		
		public String getContent() {
			return content;
		}
		
		public int getEvent() {
			return event;
		}
		
		public static MessagePreset getByCode(int code) {
			for (MessagePreset value : values()) {
				if (value.code == code) {
					return value;
				}
			}
			return DEFAULT;
		}
		
	}
	
	public final static String PROPERTY_BUNDLE = "yourchat";
	public final static String PROPERTY_CHANNELS = "de.deyovi.chat.channels";
	public final static String PROPERTY_DATAPATH = "de.deyovi.chat.datapath";
	public final static String PROPERTY_RENDERJS = "de.deyovi.chat.renderjs";
	public final static String PROPERTY_PHANTOMJS = "de.deyovi.chat.phantomjs";
	public final static String PROPERTY_FFMPEGTHUMBNAILER = "de.deyovi.chat.ffmpegthumbnailer";
	public final static String PROPERTY_UPLOAD_THRESHOLD = "de.deyovi.chat.upload.threshold";
	public final static String PROPERTY_UPLOAD_MAXIMUM = "de.deyovi.chat.upload.maximum";
	public final static String PROPERTY_UPLOAD_REQUEST = "de.deyovi.chat.upload.request";
	public final static String PROPERTY_INVITATION_REQUIRED = "de.deyovi.chat.invitation.required";
	public final static String PROPERTY_URL_PREFIX = "de.deyovi.chat.url.prefix";
	public final static String PROPERTY_PLUGINS = "de.deyovi.chat.plugins";
	public final static String PROPERTY_INITMODE = "de.deyovi.chat.initmode";
	
	public enum ImageSize {
		ORIGINAL("", -1), PREVIEW("preview", 280), THUMBNAIL("thumb", 160), PINKY("pinky", 64),;
		
		private final String prefix;
		private final int size;
		
		private ImageSize(String prefix, int size) {
			this.size = size;
			this.prefix = prefix;
		}
		
		public String getPrefix() {
			return prefix;
		}
		
		public static ImageSize getByPrefix(String prefix) {
			if (prefix != null) {
				for (ImageSize value : values()) {
					if (value != ORIGINAL) {
						if (value.prefix.equals(prefix)) {
							return value;
						}
					}
				}
			}
			return ORIGINAL;
		}
		
		public int getSize() {
			return size;
		}
	}
	
}
