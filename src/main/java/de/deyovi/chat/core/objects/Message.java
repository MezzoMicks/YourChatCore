package de.deyovi.chat.core.objects;

import java.io.Serializable;



/**
 * A message is definied as an Object containing a unique id (within it's scope) 
 * the Segments the message contains of and the origin (User)
 * @author Michi
 *
 */
public interface Message extends Serializable {

	
	/**
	 * unique id within a scope
	 * @return long
	 */
	public long getID();
	
	/**
	 * Segments (in order) of which this message is made
	 * @return Array of {@link Segment}
	 */
	public Segment[] getSegments();

	/**
	 * The Origin of this Message (usually a User)
	 * @return User or null if the originator is the System itself
	 */
	public ChatUser getOrigin();
	
	public int getCode();
	
}
