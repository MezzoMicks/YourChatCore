package de.deyovi.chat.core.objects;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface Room {

	Collection<ChatUser> getUsers();

	boolean isVisible();

	boolean isMember(ChatUser user);

	String getName();

	String getColor();

	boolean isAnonymous();

	ChatUser getOwner();

	boolean isInvited(ChatUser user);

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

	void setMotd(ChatUser username, Segment[] segments) ;

    Message getMotd();

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
		String getName();
		
		/**
		 * Returns this channels color in webformat
		 * @return Hex-String like #FFCCCC
		 */
		String getBgColor();
		
		/**
		 * Returns this channels text color in webformat
		 * @return Hex-String like #FFCCCC
		 */
		String getFgColor();
		
		String getBgImage();
		
		/**
		 * Returns the members of this room
		 * @return Array of {@link String} representing the users
		 */
		ChatUser[] getUsers();
		
		/**
		 * Returns the List of MediaSegments, that occured in this room
		 * @return Array of {@link MediaSegment}
		 */
		Segment[] getMedia();
		
	}

}
