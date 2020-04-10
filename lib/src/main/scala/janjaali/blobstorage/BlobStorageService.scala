package janjaali.blobstorage

import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.stream.scaladsl._
import akka.util.ByteString
import akka.{Done, NotUsed}
import janjaali.blobstorage.clients.BlobServiceClient
import janjaali.blobstorage.model._
import org.slf4j.LoggerFactory

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class BlobStorageService(blobServiceClient: BlobServiceClient)
                        (implicit actorSystem: ActorSystem, executionContext: ExecutionContext) {

  import BlobStorageService._

  def listBlobItems(containerName: ContainerName): Future[Iterable[BlobItem]] = {

    val containerClient = blobServiceClient.createBlobContainerClient(containerName)

    containerClient.listBlobs()
  }

  def upload(containerName: ContainerName, blobName: BlobName, byteSource: Source[ByteString, NotUsed]): Future[Done] = {

    val containerClient = blobServiceClient.createBlobContainerClient(containerName)

    val blobClient = containerClient.createBlobClient(blobName)

    val uploadSink = StreamConverters.fromOutputStream(() => blobClient.uploadStream())

    logger.info(s"Start uploading [container: '$containerName', blob: '$blobName'].")

    byteSource.runWith(uploadSink)
      .flatMap {
        case IOResult(_, Failure(exception)) => Future.failed(exception)
        case IOResult(_, Success(Done)) => Future.successful(Done)
      }
      .andThen {
        case Failure(exception) =>
          logger.error(s"Failed to upload [container: '$containerName', blob: '$blobName']: ", exception)
        case Success(_) =>
          logger.info(s"Successfully uploaded [container: '$containerName', blob: '$blobName'].")
      }
  }

  def download(containerName: ContainerName, blobName: BlobName, downloadTimeout: FiniteDuration)
  : Future[Option[(Source[ByteString, Future[Unit]], BlobItem.Properties.ContentType)]] = {

    val blobContainerClient = blobServiceClient.createBlobContainerClient(containerName)

    val blobClient = blobContainerClient.createBlobClient(blobName)

    for {
      maybeBlobItemProperties <- blobClient.fetchProperties()

      maybeDownloadStreamAndContentType = {

        maybeBlobItemProperties.map { properties =>

          val downloadStream = StreamConverters
            .asOutputStream(writeTimeout = downloadTimeout)
            .mapMaterializedValue { outputStream =>

              logger.info(s"Start downloading [container: '$containerName', blob: '$blobName'].")

              Future {
                blobClient.download(outputStream)

                outputStream.close()

                logger.info(s"Successfully downloaded [container: '$containerName', blob: '$blobName'].")
              }
            }

          (downloadStream, properties.contentType)
        }
      }
    } yield maybeDownloadStreamAndContentType
  }
}

object BlobStorageService {

  private val logger = LoggerFactory.getLogger(classOf[BlobStorageService])
}
