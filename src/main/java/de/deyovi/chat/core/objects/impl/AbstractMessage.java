package de.deyovi.chat.core.objects.impl;

import java.util.Arrays;

import de.deyovi.chat.core.objects.Message;
import de.deyovi.chat.core.objects.Segment;

public abstract class AbstractMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -290010414219802813L;
	private Segment[] segments;
	private int code = 0;

	protected AbstractMessage(Segment[] segments) {
		if (segments != null && segments.length > 0 && segments[0] != null) {
			this.segments = segments;
		}
	}
	
	@Override
	public int getCode() {
		return code;
	}
	
	@Override
	public Segment[] getSegments() {
		return segments;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public void append(Segment... segments) {
		if (segments != null) {
			int offset = this.segments.length;
			this.segments = Arrays.copyOf(this.segments, offset + segments.length);
			for (Segment segment : segments) {
				this.segments[offset++] = segment;
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getOrigin() != null ? getOrigin().getUserName() : "SYSTEM");
		result.append("::");
        final Segment[] segments = getSegments();
        if (segments != null) {
            for (Segment seg : segments) {
                result.append(seg.getContent());
            }
        }
		return result.toString();
	}

}
