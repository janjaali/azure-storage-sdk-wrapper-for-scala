package janjaali.blobstorage.clients

import com.azure.storage.blob.{BlobServiceClientBuilder, BlobServiceClient => jBlobServiceClient}
import com.azure.storage.common.StorageSharedKeyCredential
import janjaali.blobstorage.model._

import scala.concurrent.ExecutionContext

private[blobstorage] class BlobServiceClient(blobServiceClient: jBlobServiceClient)
                                            (implicit executionContext: ExecutionContext) {

  def createBlobContainerClient(containerName: ContainerName): BlobContainerClient = {

    val client = blobServiceClient.getBlobContainerClient(containerName.value)

    new BlobContainerClient(client)
  }
}

object BlobServiceClient {

  case class Endpoint(value: String) extends AnyVal

  case class Credentials(accountName: Credentials.AccountName, accountKey: Credentials.AccountKey)

  object Credentials {

    case class AccountName(value: String) extends AnyVal

    case class AccountKey(value: String) extends AnyVal
  }

  def apply(endpoint: Endpoint, credentials: Credentials)
           (implicit executionContext: ExecutionContext): BlobServiceClient = {

    val accountName = credentials.accountName.value
    val accountKey = credentials.accountKey.value
    val storageSharedKeyCredential = new StorageSharedKeyCredential(accountName, accountKey)

    val jClient = new BlobServiceClientBuilder()
      .endpoint(endpoint.value)
      .credential(storageSharedKeyCredential)
      .buildClient()

    new BlobServiceClient(jClient)
  }
}
