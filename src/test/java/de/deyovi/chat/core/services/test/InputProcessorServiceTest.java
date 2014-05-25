package de.deyovi.chat.core.services.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.objects.Segment.ContentType;
import de.deyovi.chat.core.services.InputProcessorService;
import de.deyovi.chat.core.services.impl.DefaultInputProcessorService;
import de.deyovi.chat.core.utils.ChatConfiguration;

public class InputProcessorServiceTest {

	private InputProcessorService service;
	
	@Before
	public void setup() {
		ChatConfiguration.initialize();
		service = new DefaultInputProcessorService();
	}
	
	@Test
	public void testSimpleStringOutput() {
		Segment[] process = service.process(null, "Hallo Welt", null, null);
		assertEquals("Hallo Welt", process[0].getContent());
	}

	@Test
	public void testComplexStringOutput() {
		Segment[] process = service.process(null, "Hallo Welt. Du bist obertoll, aber weißt du was?! ich bin noch Obertoller.Als du!", null, null);
		assertEquals("Hallo Welt. Du bist obertoll, aber weißt du was?! ich bin noch Obertoller.Als du!", process[0].getContent());
	}
	

	@Test
	public void testIncompleteURL() {
		Segment[] process = service.process(null, "www.google.de", null, null);
		assertEquals("http://www.google.de", process[0].getContent());
		assertEquals(ContentType.WEBSITE, process[0].getType());
	}
	
	
	@Test
	public void testSimpleURL() {
		Segment[] process = service.process(null, "http://www.google.de", null, null);
		assertEquals("http://www.google.de", process[0].getContent());
		assertEquals(ContentType.WEBSITE, process[0].getType());
	}
	
	@Test
	public void testSimpleImage() {
		Segment[] process = service.process(null, "https://www.google.de/images/srpr/logo4w.png", null, null);
		assertEquals("https://www.google.de/images/srpr/logo4w.png", process[0].getContent());
		assertEquals(ContentType.IMAGE, process[0].getType());
	}
	

	@Test
	public void testTextWebsiteAndImage() {
		Segment[] process = service.process(null, "Hallo auf http://www.google.de gibt es ein Bild http://www.google.de/images/srpr/logo4w.png das ist toll", null, null);
		assertEquals("Hallo auf", process[0].getContent());
		assertEquals(ContentType.TEXT, process[0].getType());
		assertEquals("http://www.google.de", process[1].getContent());
		assertEquals(ContentType.WEBSITE, process[1].getType());
		assertEquals("gibt es ein Bild", process[2].getContent());
		assertEquals(ContentType.TEXT, process[2].getType());
		assertEquals("http://www.google.de/images/srpr/logo4w.png", process[3].getContent());
		assertEquals(ContentType.IMAGE, process[3].getType());
		assertEquals("logo4w.png", process[3].getAlternateName());
		assertEquals("das ist toll", process[4].getContent());
		assertEquals(ContentType.TEXT, process[4].getType());
	}
	
}
