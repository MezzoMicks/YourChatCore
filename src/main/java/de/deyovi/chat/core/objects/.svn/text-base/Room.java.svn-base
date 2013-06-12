package de.deyovi.chat.core.objects;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.xml.registry.infomodel.User;

import de.deyovi.chat.core.objects.impl.DefaultChatUser;
import de.deyovi.chat.core.objects.impl.SystemMessage;

public interface Room {

	public Collection<ChatUser> getUsers();

	public boolean isVisible();

	public boolean isMember(ChatUser user);

	public String getName();

	public String getColor();

	public void leave(ChatUser user);

	public void join(ChatUser user);

	public boolean isAnonymous();

	public ChatUser getOwner();

	public boolean isInvited(ChatUser user);

	boolean invite(ChatUser inviteeUser);

	boolean revokeInvitation(ChatUser user);

	boolean ban(ChatUser user);

	boolean revokeBan(ChatUser user);

	List<Message> getProtocol();
	
	/**
	 * Sets this rooms color (Background)
	 * @param color
	 */
	void setColor(String color);

	boolean setOpen(boolean b);

	void setAnonymous(boolean b);

	void setMotd(ChatUser username, String motd, InputStream uploadStream, String uploadName) ;

	RoomInfo getInfoForUser(ChatUser user);
	
	void talk(ChatUser user, Segment[] segments);

	void shout(Message systemMessage);
	
	/**
	 * Sets this rooms textcolor (Background)
	 * @param fontColor
	 */
	void setFontColor(String string);
	
	void setBgImage(String bgImage);

	void setOwner(ChatUser user);

	void clearLog(String userName);
	
	void clearMedia(String userName);

	/**
	 * Information about a channel
	 * @author Michi
	 *
	 */
	public interface RoomInfo extends Serializable {

		/**
		 * Returns this channels name
		 * @return {@link String}
		 */
		public String getName();
		
		/**
		 * Returns this channels color in webformat
		 * @return Hex-String like #FFCCCC
		 */
		public String getBgColor();
		
		/**
		 * Returns this channels text color in webformat
		 * @return Hex-String like #FFCCCC
		 */
		public String getFgColor();
		
		public String getBgImage();
		
		/**
		 * Returns the members of this room
		 * @return Array of {@link String} representing the users
		 */
		public ChatUser[] getUsers();
		
		/**
		 * Returns the List of MediaSegments, that occured in this room
		 * @return Array of {@link MediaSegment}
		 */
		public Segment[] getMedia();
		
		public ChatUser[] getOtherUsers();
		
	}

}
