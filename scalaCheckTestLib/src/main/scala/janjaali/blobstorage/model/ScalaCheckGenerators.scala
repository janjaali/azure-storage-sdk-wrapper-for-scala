package janjaali.blobstorage.model

import java.time.OffsetDateTime

import janjaali.blobstorage.common
import org.scalacheck._

object ScalaCheckGenerators {

  object Implicits {

    implicit val arbitraryContainerName: Arbitrary[ContainerName] = Arbitrary(containerNameGen)

    implicit val arbitraryBlobItemProperties: Arbitrary[BlobItem.Properties] = {

      Arbitrary(blobItemProperties)
    }

    implicit val arbitraryBlobItem: Arbitrary[BlobItem] = Arbitrary(blobItemGen)

    implicit val arbitraryBlobName: Arbitrary[BlobName] = Arbitrary(blobNameGen)
  }

  import Implicits._
  import common.DateTimeScalaCheckGenerators.Implicits._

  private def containerNameGen: Gen[ContainerName] = {

    Arbitrary.arbitrary[String].map(ContainerName.apply)
  }

  private def blobItemProperties: Gen[BlobItem.Properties] = {

    val contentTypeGen: Gen[BlobItem.Properties.ContentType] = {

      Arbitrary.arbitrary[String].map(BlobItem.Properties.ContentType.apply)
    }

    for {
      creationTime <- Arbitrary.arbitrary[OffsetDateTime]
      lastModified <- Arbitrary.arbitrary[OffsetDateTime]
      contentLength <- Arbitrary.arbitrary[Long]
      contentType <- contentTypeGen
    } yield BlobItem.Properties(creationTime, lastModified, contentLength, contentType)
  }

  private def blobItemGen: Gen[BlobItem] = {

    for {
      blobName <- Arbitrary.arbitrary[BlobName]
      properties <- Arbitrary.arbitrary[BlobItem.Properties]
    } yield BlobItem(blobName, properties)
  }

  private def blobNameGen: Gen[BlobName] = {

    Arbitrary.arbitrary[String].map(BlobName.apply)
  }
}
