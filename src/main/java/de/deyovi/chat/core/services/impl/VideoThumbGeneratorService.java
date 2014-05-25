package de.deyovi.chat.core.services.impl;

import de.deyovi.chat.core.constants.ChatConstants.ImageSize;
import de.deyovi.chat.core.services.ThumbGeneratorService;
import de.deyovi.chat.core.utils.ChatConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class VideoThumbGeneratorService implements ThumbGeneratorService {

	private final static Logger logger = Logger.getLogger(VideoThumbGeneratorService.class);


    private final ImageThumbGeneratorService imageThumbGeneratorService;

    @Inject
    public VideoThumbGeneratorService(@Any Instance<ThumbGeneratorService> thumbGenerator) {
        imageThumbGeneratorService = thumbGenerator.select(ImageThumbGeneratorService.class).get();
    }

	@Override
	public Map<de.deyovi.chat.core.constants.ChatConstants.ImageSize, String> generate(Object source, String suffix, de.deyovi.chat.core.constants.ChatConstants.ImageSize... imageSizes) {
		Map<ImageSize, String> result = new HashMap<ImageSize, String>(imageSizes.length);
		String ffMPEGthumber = ChatConfiguration.getFFMPEGThumbnailer();
		if (ffMPEGthumber != null) {
			try {
				File dataDir = new File(ChatConfiguration.getDataDir());
				File sourceFile = null;
				boolean deleteSource = false;
				InputStream inputStream = null;
				if (source instanceof InputStream) {
					inputStream = (InputStream) source;
				} else {
					if (source instanceof URL) {
						URL videoURL = (URL) source;
						if ("file".equals(videoURL.getProtocol())) {
							try {
								sourceFile = new File(videoURL.toURI());
							} catch (URISyntaxException e) {
								logger.error("Invalid videoSource!", e);
							}
						} else {
							inputStream = videoURL.openStream();
						}
					} else if (source instanceof URLConnection) {
						inputStream = ((URLConnection) source).getInputStream();
					}
					
					// In the case of URL's we need to create a local copy
					if (inputStream != null) {
						sourceFile = File.createTempFile("source", suffix, dataDir);
						FileOutputStream fos = new FileOutputStream(sourceFile);
						IOUtils.copy(inputStream, fos);
						fos.close();
						deleteSource = true;
						
					}
				}
				if (inputStream != null) {
					sourceFile = File.createTempFile("source", suffix, dataDir);
					FileOutputStream fos = new FileOutputStream(sourceFile);
					IOUtils.copy(inputStream, fos);
					fos.close();
					deleteSource = true;
				} 
				
				if (sourceFile == null) {
					logger.warn("Couldn't get sourcefile for " + suffix);
					sourceFile = null;
				} else {
					logger.info("Creating Preview for video \"" + sourceFile.getAbsolutePath() + "\"");
					File tmpFile = File.createTempFile("video", ".jpg", dataDir);
					String videoFileArg = "-i\"" + sourceFile.getAbsolutePath() + "\"";
					String tmpFileArg = "-o\"" + tmpFile.getAbsolutePath() + "\"";
					String stripeArg = "-f";
					String timeArg = "-t25%";
					String sizeArg = "-s640";
					String[] cmds = new String[] {
							ffMPEGthumber,
							videoFileArg,
							tmpFileArg,
							stripeArg,
							timeArg,
							sizeArg
					};
					String cmdString = "";
					if (logger.isDebugEnabled()) {
						for (String cmd : cmds) {
							cmdString += cmd + " ";
						}
						logger.debug("Creating thumbnail for video, spawning \"" + cmdString + "\"");
					}
					try {
						ProcessBuilder pb = new ProcessBuilder(ffMPEGthumber, "-i", sourceFile.getAbsolutePath(), "-o", tmpFile.getAbsolutePath(), stripeArg, timeArg, sizeArg);
						Process process = pb.start();
						int exe = process.waitFor();
						logger.debug("exec:" + exe);
						Thread.sleep(100);
						logger.debug("resizing to thumbsize");
						logger.debug("reading inputstream from " + tmpFile.getAbsolutePath());
						result = imageThumbGeneratorService.generate(sourceFile, suffix, imageSizes);
						if (tmpFile != null) {
							tmpFile.delete();
						}
						if (deleteSource && sourceFile != null) {
							sourceFile.delete();
						}
					} catch (InterruptedException e) {
						logger.error("error while executing ffmpegthumbnailer", e);
					} catch (IOException e) {
						logger.error("error while executing ffmpegthumbnailer", e);
					}
				}
			} catch (IOException ioex) {
				logger.error("error while processing VideoInput", ioex);
			}
		}
		return result;
	}
	
}
