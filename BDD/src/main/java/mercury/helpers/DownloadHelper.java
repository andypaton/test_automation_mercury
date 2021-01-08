package mercury.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class DownloadHelper {

    private static final Logger logger = LogManager.getLogger();

    private static final int BUFFER_SIZE = 4096;
    private static final String DOWNLOADS = System.getProperty("user.dir") + "//target//Downloads";


    public void downloadFile(String url) throws ClientProtocolException, IOException {
        mkFolder(DOWNLOADS);
        downloadFile(url, DOWNLOADS);
    }

    private void mkFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            boolean bool = file.mkdir();
            if(bool){
                logger.debug("Directory created successfully");
            }else{
                logger.debug("Sorry couldn’t create specified directory");
            }
        }
    }

    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    private static void downloadFile(String fileURL, String saveDir) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
            }

            logger.debug("Content-Type = " + contentType);
            logger.debug("Content-Disposition = " + disposition);
            logger.debug("Content-Length = " + contentLength);
            logger.debug("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            logger.debug("File downloaded");
        } else {
            logger.debug("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
}
