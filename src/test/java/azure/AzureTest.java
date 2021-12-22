package azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class AzureTest {

    @Test
    public void testBlobStorageUpload() throws IOException {
        var connectionString = "DefaultEndpointsProtocol=https;AccountName=monorvo;AccountKey=Ap/5utSWZ+V7Bk03gpFWnqmv3Y9jcxHGjS1KAjAr7I3Ff4PTVahNOf+5FK5eOUNvaB31pG4ZHtomkMcbJPOFcQ==;EndpointSuffix=core.windows.net";

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("proforma-invoice");
        var bolbClient = containerClient.getBlobClient("inv2005399761.pdf");
      //  File downloadedFile = new File("/Users/kolinsky.hexad/Downloads/2005399761.pdf");

        bolbClient.downloadToFile("/Users/kolinsky.hexad/Downloads/2005399761.pdf");

    }
}
