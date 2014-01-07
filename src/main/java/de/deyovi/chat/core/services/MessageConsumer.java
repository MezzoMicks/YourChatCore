package de.deyovi.chat.core.services;

import java.util.Locale;

import de.deyovi.chat.core.objects.Segment;
import de.deyovi.chat.core.services.OutputService.OutputMeta;

public interface MessageConsumer {
	
	public void consume(Segment[] segments, Locale locale, OutputMeta meta);

	public void finish(OutputMeta meta);
	
	public String getResult();
	
	
}
