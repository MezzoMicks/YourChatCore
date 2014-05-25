package de.deyovi.chat.core.services.impl;

import de.deyovi.chat.core.constants.ChatConstants.ImageSize;
import de.deyovi.chat.core.services.ThumbGeneratorService;
import de.deyovi.chat.core.utils.ChatConfiguration;
import de.deyovi.chat.core.utils.ChatUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class DefaultWebsiteThumbGeneratorService implements WebsiteThumbGeneratorService {
	
	private final static Logger logger = Logger.getLogger(DefaultWebsiteThumbGeneratorService.class);

	private static final String BIGIMAGE_PREFIX = "bigimage: ";

    @Inject
    private ChatUtils chatUtils;

	@Override
	public Map<ImageSize, String> generate(Object source, String suffix, ImageSize... imageSizes) {
		Map<ImageSize, String> result = new HashMap<ImageSize, String>(imageSizes.length);
		String phantomJSPath = ChatConfiguration.getPhantomjs();
		if (phantomJSPath != null) {
			String urlAsString = null;
			if (source instanceof String) {
				urlAsString = (String) source;
			} else if (source instanceof URL) {
				urlAsString = ((URL) source).toExternalForm();
			} else if (source instanceof URLConnection) {
				urlAsString = ((URLConnection) source).getURL().toExternalForm();
			} else {
				return null;
			}
			try {
				File tmpFile = File.createTempFile("web", ".jpg", new File(ChatConfiguration.getDataDir()));
				logger.info("Creating Preview for website \"" + urlAsString + "\"");
				String renderJSPath = ChatConfiguration.getRenderJS();
				String[] cmds = new String[] { phantomJSPath, renderJSPath, "\"" + urlAsString + "\"", tmpFile.getAbsolutePath() };
				String cmdString = "";
				if (logger.isDebugEnabled()) {
					for (String cmd : cmds) {
						cmdString += cmd + " ";
					}
					logger.debug("Creating thumbnail for website, spawning \"" + cmdString + "\"");
				}
				// Process exec = Runtime.getRuntime().exec(cmdString);
				ProcessBuilder pb = new ProcessBuilder(phantomJSPath, renderJSPath, urlAsString, tmpFile.getAbsolutePath());
				Process process = pb.start();
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				InputStream inputStream = null;
				String line;
				while ((line = reader.readLine()) != null) {
					logger.debug("exec>" + line);
					if (line.startsWith(BIGIMAGE_PREFIX)) {
						logger.info("got big image, using url " + line);
						inputStream = new URL(line.substring(BIGIMAGE_PREFIX.length())).openStream();
					}
				}
				int exe = process.waitFor();
				logger.debug("exec:" + exe);
				Thread.sleep(100);
				logger.debug("resizing to thumbsize");
				if (inputStream == null) {
					logger.debug("reading inputstream from " + tmpFile.getAbsolutePath());
					inputStream = new FileInputStream(tmpFile);
				}
				if (inputStream != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					IOUtils.copy(inputStream, bos);
					ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
					for (ImageSize size : imageSizes) {
						bis.reset();
						String thumbname = chatUtils.createAndStoreResized(size.getPrefix(), bis, suffix, size.getSize(), size.getSize(), null);
						result.put(size, "data/" + thumbname);
					}
				}
				if (tmpFile != null) {
					tmpFile.delete();
				}
			} catch (InterruptedException e) {
				logger.error("error while executing phantomjs", e);
			} catch (IOException e) {
				logger.error("error while executing phantomjs", e);
			}
		}
		return result;
	}
}