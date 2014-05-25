package de.deyovi.aide;


public interface Notice extends Comparable<Notice>  {

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

		public String getName() {
			return this.name();
		}
	}

	/**
	 * The Level of this alert, one of {@link Level}
	 */
	public Level getLevel();
	
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
