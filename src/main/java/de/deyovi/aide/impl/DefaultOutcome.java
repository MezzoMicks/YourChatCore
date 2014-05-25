package de.deyovi.aide.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import de.deyovi.aide.Notice;
import de.deyovi.aide.Outcome;

/**
 * Default generic Implementation of {@link Outcome}
 * @author Michi
 *
 * @param <T>
 */
public class DefaultOutcome<T> implements Outcome<T> {

	private final T result;
	private final Collection<Notice> notices;

	public DefaultOutcome(T result) {
		this(result, new LinkedList<Notice>());
	}
	
	public DefaultOutcome(T result, Notice notice) {
		this(result, Collections.singletonList(notice));
	}

	public DefaultOutcome(T result, Collection<Notice> notices) {
		this.result = result;
		Collection<Notice> newList;
		if (notices == null) {
			newList = Collections.emptyList();
		} else if (notices.size() == 1 && notices.contains(null)) {
			newList = notices;
		} else {
			newList = new ArrayList<Notice>(notices.size());
			for (Notice notice : notices) {
				if (notice != null) {
					newList.add(notice);
				}
			}
		}
		this.notices = notices;
	}
	
	@Override
	public T getResult() {
		return result;
	}

	@Override
	public Collection<Notice> getNotices() {
		return notices;
	}

}
