package io.github.volyx;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class MembotController {
    private static Logger logger = LoggerFactory.getLogger(MembotController.class);
    private static final String BOT_NAME = "MemBot";
    private Map<String, BufferedImage> images = new HashMap<>();

    private static final String JPG = ".jpg";

    public MembotController() {

        URL url = this.getClass().getClassLoader().getResource("images");
        try {
            logger.info("Image directory = {}", url);
            Files.list(Paths.get(url.getPath())).forEach(new Consumer<Path>() {
                @Override
                public void accept(Path path) {
                    logger.info("image {}", path.toString());
                    if (!path.toString().endsWith(JPG)) {
                        return;
                    }
                    final File file = path.toFile();
                    String filename = file.getName().replace(JPG, "");
                    try {
                        images.put(filename, Thumbnails.of(file)
                                .size(300, 300).asBufferedImage());
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException();
        }

    }

    @RequestMapping(value = "/event", method=POST)
    public Bot event(
            final @RequestBody Req req,
            final HttpServletResponse rsp
    ) throws Exception {

        final String[] split = req.getText().split("!");

        if (split.length != 2) {
            rsp.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            return new Bot("I don't know, ask Bobuk!", BOT_NAME);
        }

        String slogan = split[1];

        final String text = split[0];
        final String filename = text.replace(":", "");


        BufferedImage img = images.get(filename);

        if (img == null) {
            rsp.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            return new Bot("I don't know, ask Bobuk!", BOT_NAME);
        }

        String imgstr = encodeToString(img);

        String result = "<img alt='" + text + "' src='data:image/jpg;base64," + imgstr + "' />";

        if (!result.isEmpty()){
            rsp.setStatus(HttpStatus.CREATED.value());
            return new Bot(result, BOT_NAME);
        } else {
            rsp.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            return new Bot("I don't know, ask Bobuk!", BOT_NAME);
        }
    }

    private static String encodeToString(BufferedImage image) {
        String imageString = "";
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes).replace("\n", "");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

}
