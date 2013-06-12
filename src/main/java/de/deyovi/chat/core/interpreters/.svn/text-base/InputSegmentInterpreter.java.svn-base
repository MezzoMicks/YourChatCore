package de.deyovi.chat.core.interpreters;

import java.net.URL;
import java.net.URLConnection;

import de.deyovi.chat.core.objects.Segment;

public interface InputSegmentInterpreter {

	/**
	 * Interprete a Segment
	 * @param segment
	 * @return one or more or null segments (null indicates, that this segment couldn't be interpreted)
	 */
	public Segment[] interprete(InterpretableSegment segment);
	
	/**
	 * Wrapper for {@link Segment}-Objects that will be interpreted.
	 * So eventually required URL/Connection-Instantiations won't be done redundantly by the different Interpreters
	 * @author Michi
	 *
	 */
	public interface InterpretableSegment extends Segment {
		
		/**
		 * Is this segment an URL
		 * @return true if it is an URL
		 */
		public boolean isURL();
		
		/**
		 * The URL represented by this Segment
		 * @return
		 */
		public URL getURL();

		/**
		 * The connection to the URL, represented by this Segment
		 * @return
		 */
		public URLConnection getConnection();
		
	}
	
}
