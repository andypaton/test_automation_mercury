package mercury.helpers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FileHelper {

    private static final Logger logger = LogManager.getLogger();

    public static String getResourceFile(String filePath, String filename) throws Exception {
        String pathname = filePath + filename;
        return getResourceFile(pathname);
    }

    public static String getResourceFile(String pathname) throws Exception {

        ClassLoader classLoader = FileHelper.class.getClassLoader();
        File file = new File(classLoader.getResource(pathname).getFile());

        if (!file.exists()) {
            throw new Exception("file not found: " + pathname);
        }

        String content = new String(Files.readAllBytes(file.toPath()));

        return content;
    }

    public static List<String> getLines(String path, String filename) throws URISyntaxException {
        String slash = "/".equals(path.substring(path.length() - 1)) ? "" : "/";
        String pathname = path + slash + filename ;
        return getLines(pathname);
    }

    public static List<String> getLines(String pathname) throws URISyntaxException {
        ClassLoader classLoader = FileHelper.class.getClassLoader();
        List<String> list = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(classLoader.getResource(pathname).toURI()))) {
            list = stream.collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void mkFolder(String dir) {
        try {
            Files.createDirectories(Paths.get(dir));
        } catch (IOException e) {
            logger.error("Creation of folder + " + dir + " failed.", e);
        }
    }
}
