package janjaali.blobstorage

import java.io.{ByteArrayOutputStream, OutputStream}

import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import akka.{Done, NotUsed}
import janjaali.Spec
import janjaali.akka.TestActorSystem
import janjaali.blobstorage.clients._
import janjaali.blobstorage.model._
import org.scalacheck.Arbitrary
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class BlobStorageServiceSpec extends Spec with MockFactory with TestActorSystem with ScalaFutures {

  import BlobStorageServiceSpec._
  import actorSystem.dispatcher
  import model.ScalaCheckGenerators.Implicits._

  "BlobStorageService" - {

    "should list blobs." in {

      forAll { containerName: ContainerName =>

        val fixture = createFixture()

        val blobContainerClientMock = mock[BlobContainerClient]

        (fixture.blobServiceClientMock.createBlobContainerClient _)
          .expects(containerName)
          .returns(blobContainerClientMock)

        val expectedBlobItems = Arbitrary.arbitrary[Iterable[BlobItem]].sample.get

        (blobContainerClientMock.listBlobs _)
          .expects()
          .returns(Future.successful(expectedBlobItems))

        fixture.sut.listBlobItems(containerName).futureValue should contain theSameElementsAs expectedBlobItems
      }
    }

    "should upload streams of bytes." in {

      forAll { (containerName: ContainerName, blobName: BlobName, byteSource: Source[ByteString, NotUsed]) =>

        val fixture = createFixture()

        val blobContainerClientMock = mock[BlobContainerClient]

        (fixture.blobServiceClientMock.createBlobContainerClient _)
          .expects(containerName)
          .returns(blobContainerClientMock)

        val blobClientMock = mock[BlobClient]

        (blobContainerClientMock.createBlobClient _)
          .expects(blobName)
          .returns(blobClientMock)

        val outputStream = new ByteArrayOutputStream()

        (blobClientMock.uploadStream _)
          .expects()
          .returns(outputStream)

        val uploadResult = fixture.sut.upload(containerName, blobName, byteSource).futureValue
        uploadResult shouldBe Done

        val expectedUploadedContent = byteSource.runWith(Sink.head).map(_.utf8String).futureValue
        outputStream.toString shouldBe expectedUploadedContent
      }
    }

    "should download stream of bytes." in {

      forAll { (containerName: ContainerName, blobName: BlobName, downloadTimeout: FiniteDuration) =>

        val fixture = createFixture()

        val blobContainerClientMock = expectContainerClientMock(fixture.blobServiceClientMock, containerName)

        val expectedContent = Arbitrary.arbitrary[String].sample.get
        val expectedContentType = BlobItem.Properties.ContentType(Arbitrary.arbitrary[String].sample.get)

        val blobClientMock = new BlobClient(blobClient = null) {

          override def download(outputStream: OutputStream): Unit = {

            outputStream.write(expectedContent.getBytes)
          }

          override def fetchProperties(): Future[Option[BlobItem.Properties]] = {

            Future.successful(
              Arbitrary.arbitrary[BlobItem.Properties].map { properties =>
                properties.copy(
                  contentType = expectedContentType
                )
              }.sample
            )
          }
        }

        (blobContainerClientMock.createBlobClient _)
          .expects(blobName)
          .returns(blobClientMock)

        val (downloadSource, contentType) = fixture.sut.download(containerName, blobName, downloadTimeout).futureValue.get

        val downloadContent = downloadSource.runWith(Sink.head).map(_.utf8String).futureValue

        downloadContent shouldBe expectedContent
        contentType shouldBe expectedContentType
      }
    }
  }

  private def createFixture(): Fixture = {

    val blobServiceClientMock = mock[BlobServiceClient]

    val blobStorageService = new BlobStorageService(blobServiceClientMock)

    Fixture(
      sut = blobStorageService,
      blobServiceClientMock = blobServiceClientMock
    )
  }

  private def expectContainerClientMock(blobServiceClientMock: BlobServiceClient, containerName: ContainerName)
  : BlobContainerClient = {

    val blobContainerClientMock = mock[BlobContainerClient]

    (blobServiceClientMock.createBlobContainerClient _)
      .expects(containerName)
      .returns(blobContainerClientMock)

    blobContainerClientMock
  }
}

object BlobStorageServiceSpec {

  private case class Fixture(sut: BlobStorageService, blobServiceClientMock: BlobServiceClient)

  private implicit val arbitraryByteStringSource: Arbitrary[Source[ByteString, NotUsed]] = Arbitrary {

    val arbitraryByteString = Arbitrary.arbitrary[String].map(ByteString.apply)

    arbitraryByteString.map(Source.single)
  }
}
