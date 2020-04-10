package janjaali.blobstorage.clients

import java.io.OutputStream

import com.azure.storage.blob.models.BlobStorageException
import com.azure.storage.blob.{BlobClient => jBlobClient}
import janjaali.blobstorage.model._

import scala.concurrent.{ExecutionContext, Future}

private[blobstorage] class BlobClient(blobClient: jBlobClient)
                                     (implicit executionContext: ExecutionContext) {

  def uploadStream(): OutputStream = {

    val blockBlob = blobClient.getBlockBlobClient

    blockBlob.getBlobOutputStream()
  }

  def download(outputStream: OutputStream): Unit = {

    blobClient.download(outputStream)
  }

  def fetchProperties(): Future[Option[BlobItem.Properties]] = {

    Future {

      val properties = blobClient.getProperties

      Some {
        BlobItem.Properties(
          creationTime = properties.getCreationTime,
          lastModified = properties.getLastModified,
          contentLength = properties.getBlobSize,
          contentType = BlobItem.Properties.ContentType(properties.getContentType)
        )
      }
    }.recoverWith {

      case ex: BlobStorageException =>

        val notFoundStatusCode = 404

        if (ex.getStatusCode == notFoundStatusCode) {
          Future.successful(None)
        } else {
          Future.failed(ex)
        }
    }
  }
}
