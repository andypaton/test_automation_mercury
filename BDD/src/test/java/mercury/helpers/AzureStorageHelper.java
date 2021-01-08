package mercury.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

public class AzureStorageHelper {

    /**
     *
     * @param storageConnectionString
     * @param localPathname - eg. "C:\\myimages\\myimage.jpg"
     * @param remoteFileName - eg. "blobby.pdf"
     */
    public void uploadBlob(String storageConnectionString, String localPathname, String containerName, String remoteFileName) {
        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Retrieve reference to a previously created container.
            CloudBlobContainer container = blobClient.getContainerReference(containerName);

            // Create or overwrite the remoteFileName blob with contents from localPathname.
            CloudBlockBlob blob = container.getBlockBlobReference(remoteFileName);
            File source = new File(localPathname);
            blob.upload(new FileInputStream(source), source.length());
        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

    public List<String> getBlobs(String storageConnectionString, String containerName, String directory) {
        List<String> blobs = new ArrayList<>();
        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Retrieve reference to a previously created container.
            CloudBlobContainer container = blobClient.getContainerReference(containerName);

            // Loop over blobs within the container and output the URI to each of them.
            for (ListBlobItem blobItem : container.listBlobs(directory, true)) {
                blobs.add(blobItem.getUri().getPath());
            }
            return blobs;
        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
            return null;
        }
    }

}
