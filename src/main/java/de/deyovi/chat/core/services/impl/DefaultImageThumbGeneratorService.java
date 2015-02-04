package de.deyovi.chat.core.services.impl;

import de.deyovi.chat.core.constants.ChatConstants;
import de.deyovi.chat.core.utils.ChatUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by michi on 25.05.14.
 */
@Service
public class DefaultImageThumbGeneratorService implements ImageThumbGeneratorService {

    private final static Logger logger = Logger.getLogger(ImageThumbGeneratorService.class);

    @Resource
    private ChatUtils chatUtils;

    @Override
    public Map<ChatConstants.ImageSize, String> generate(Object source, String suffix, ChatConstants.ImageSize... imageSizes) {
        Map<ChatConstants.ImageSize, String> result = new HashMap<ChatConstants.ImageSize, String>(imageSizes.length);
        if (source != null) {
            try {
                String name = suffix != null ? suffix : "imagepreview.png";
                if (source instanceof BufferedImage) {
                    for (ChatConstants.ImageSize size : imageSizes) {
                        String filename = chatUtils.createAndStoreResized(size.getPrefix(), (BufferedImage)source, name, size.getSize(), size.getSize(), null);
                        result.put(size,  "data/" + filename);
                    }
                } else {
                    InputStream inputStream = null;
                    if (source instanceof InputStream) {
                        inputStream = (InputStream) source;
                    }
                    if (inputStream == null) {
                        if (source instanceof URLConnection) {
                            inputStream = ((URLConnection) source).getInputStream();
                        } else if (source instanceof URL) {
                            inputStream = ((URL) source).openStream();
                        } else if (source instanceof File) {
                            inputStream = new FileInputStream((File) source);
                        }
                    }
                    if (inputStream == null) {
                        logger.error("couldn't open Stream for Image: " + suffix);
                    } else {
                        ByteArrayInputStream bis;
                        if (inputStream instanceof ByteArrayInputStream) {
                            bis = (ByteArrayInputStream) inputStream;
                        } else {
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            IOUtils.copy(inputStream, bos);
                            inputStream.close();
                            inputStream = null;
                            bis = new ByteArrayInputStream(bos.toByteArray());
                        }
                        for (ChatConstants.ImageSize size : imageSizes) {
                            bis.reset();
                            String filename = chatUtils.createAndStoreResized(size.getPrefix(), bis, name, size.getSize(), size.getSize(), null);
                            result.put(size,  "data/" + filename);
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("couldn't open Source: " + source, e);
            }
        }
        return result;
    }
}
