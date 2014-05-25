package de.deyovi.aide;

import java.util.Collection;

/**
 * General Wrapper for Results that may also return Information (Errors, Warnings, Info)
 * 
 * @author Michi
 *
 * @param <T>
 */
public interface Outcome<T> {

	/**
	 * The actual Result of the
	 * @return 
	 */
	public T getResult();
	
	/**
	 * Notices that come along with the Result (or the lack thereof)
	 * @return {@link Collection} of {@link Notice}
	 */
	public Collection<Notice> getNotices();
	
}
