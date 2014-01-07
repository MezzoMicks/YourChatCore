package de.deyovi.chat.core.objects;

public interface Alert extends Comparable<Alert> {

	/**
	 * Constants for AlertLevel-Types
	 * @author Michi
	 *
	 */
	public enum Level {
		/**
		 * INFO this alert is not crucial and just informative
		 * (e.g. a file was delivered succesfully)
		 */
		INFO(2), 
		/**
		 * WARN this alert is relevant but is of now harm to the UserExperience<br>
		 * (e.g. supplied password is wrong, or timeout commencing)
		 */
		WARN(1), 
		/**
		 * ERROR this alert is very important, the Information is vital for proper UserExperience<br>
		 * (e.g. alert about "Server Shutdown" or "erroneous Data")
		 */
		ERROR(0);
		
		private final int priority;
		
		private Level(int priority) {
			this.priority = priority;
		}
		
		/**
		 * Returns the Priority of this Level (numeric for comparison, purpose
		 * The lower the number, the more urgent the {@link Level}, hence it being befor the others in a list
		 * @return int
		 */
		public int getPriority() {
			return priority;
		}
	}
	
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
		AUTO_HIDE
	}
	
	/**
	 * The Level of this alert, one of {@link Level}
	 */
	public Level getLevel();
	
	/**
	 * The Lifespan (display-time) of this alert
	 * @return
	 */
	public Lifespan getLifespan();
	
	/**
	 * The actual message of this alert (message-code, usually prefixed "alert")
	 */
	public String getMessageCode();
	
	/**
	 * ID for proper re-identification of the same Alert
	 * @return long
	 */
	public long getID();
	
}
