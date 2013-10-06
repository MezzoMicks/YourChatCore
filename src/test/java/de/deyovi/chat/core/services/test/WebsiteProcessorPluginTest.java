package de.deyovi.chat.core.services.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Before;
import org.junit.Test;

import de.deyovi.chat.core.interpreters.InputSegmentInterpreter;
import de.deyovi.chat.core.interpreters.InputSegmentInterpreter.InterpretableSegment;
import de.deyovi.chat.core.interpreters.impl.WebsiteProcessorPlugin;
import de.deyovi.chat.core.utils.ChatConfiguration;


public class WebsiteProcessorPluginTest {

	private InputSegmentInterpreter websiteProcessor;
	
	@Before
	public void setup() {
		websiteProcessor = new WebsiteProcessorPlugin();
	}
	
	@Test
	public void testGoogle() {
		ChatConfiguration.initialize();
		InterpretableSegment seg = new MyInterpretableMockSegment("http://www.google.de");
		assertNotNull(websiteProcessor.interprete(seg)[0].getPreview());
	}
	

	
	@Test
	public void testTest() {
		ChatConfiguration.initialize();
		InterpretableSegment seg = new MyInterpretableMockSegment("http://www.test.de");
		assertNotNull(websiteProcessor.interprete(seg)[0].getPreview());
	}
	
	
	private class MyInterpretableMockSegment implements InterpretableSegment {
		
		private final String url;
		
		private MyInterpretableMockSegment(String url) {
			this.url = url;
		}

		@Override
		public String getUser() {
			return "Anonymous";
		}
		
		@Override
		public ContentType getType() {
			return null;
		}
		
		@Override
		public String getPreview() {
			return null;
		}
		
		@Override
		public String getPinky() {
			return null;
		}
		
		@Override
		public String getContent() {
			return url;
		}
		
		@Override
		public String getAlternateName() {
			return null;
		}
		
		@Override
		public void append(String content) {
			
		}
		
		@Override
		public boolean isURL() {
			return true;
		}
		
		@Override
		public URL getURL() {
			try {
				return new URL(url);
			} catch (MalformedURLException e) {
				return null;
			}
		}
		
		@Override
		public URLConnection getConnection() {
			try {
				return getURL().openConnection();
			} catch (IOException e) {
				return null;
			}
		}
	}
}
