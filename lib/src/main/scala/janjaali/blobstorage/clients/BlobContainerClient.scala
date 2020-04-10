package janjaali.blobstorage.clients

import com.azure.storage.blob.{BlobContainerClient => jBlobContainerClient}
import janjaali.blobstorage.model._

import scala.concurrent.{ExecutionContext, Future}

private[blobstorage] class BlobContainerClient(blobContainerClient: jBlobContainerClient)
                                              (implicit executionContext: ExecutionContext) {

  def createBlobClient(blobName: BlobName): BlobClient = {

    val client = blobContainerClient.getBlobClient(blobName.value)

    new BlobClient(client)
  }

  def listBlobs(): Future[Iterable[BlobItem]] = {

    Future {

      import scala.jdk.CollectionConverters._

      blobContainerClient.listBlobs().iterator().asScala.map { blobItem =>

        val blobName = BlobName(blobItem.getName)

        val blobItemProperties = {

          val properties = blobItem.getProperties

          BlobItem.Properties(
            creationTime = properties.getCreationTime,
            lastModified = properties.getLastModified,
            contentLength = properties.getContentLength,
            contentType = BlobItem.Properties.ContentType(properties.getContentType)
          )
        }

        BlobItem(blobName, blobItemProperties)
      }.iterator.to(Iterable)
    }
  }
}
