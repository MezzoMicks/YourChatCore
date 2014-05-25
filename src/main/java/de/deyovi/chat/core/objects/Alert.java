package de.deyovi.chat.core.objects;

import de.deyovi.aide.Notice;

public interface Alert extends Notice {
	
	/**
	 * Constants for an Alerts lifespan, deciding how long they are displayed on screen
	 * @author Michi
	 *
	 */
	public enum Lifespan {
		/**
		 * Normal : The alert may be hidden by the user, but won't disappear automatically
		 */
		NORMAL,
		/**
		 * Permanent: The alert must not be hidden, these alerts also shouldn't be removed by user-action from the alert-stack
		 */
		PERMANENT,
		/**
		 * Auto-Hide : These Alerts will hide themselves after a short period of time
		 */
		AUTO_HIDE;
		
		public String getName() {
			return this.name();
		}
	}
	
	/**
	 * The Lifespan (display-time) of this alert
	 * @return
	 */
	public Lifespan getLifespan();
	
}
