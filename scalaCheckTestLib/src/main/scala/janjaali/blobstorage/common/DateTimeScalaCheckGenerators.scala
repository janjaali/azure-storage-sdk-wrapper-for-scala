package janjaali.blobstorage.common

import java.time._

import org.scalacheck._

private[blobstorage] object DateTimeScalaCheckGenerators {

  object Implicits {

    implicit val arbitraryOffsetDateTime: Arbitrary[OffsetDateTime] = Arbitrary(offsetDateTimeGen)
  }

  private def offsetDateTimeGen: Gen[OffsetDateTime] = {

    val maxNanonSeconds = 999_999_999L

    for {
      epochSecond <- Gen.choose(min = OffsetDateTime.MIN.toEpochSecond, max = OffsetDateTime.MAX.toEpochSecond)
      nanoAdjustment <- Gen.choose(min = 0, max = maxNanonSeconds)
    } yield Instant.ofEpochSecond(epochSecond, nanoAdjustment).atOffset(ZoneOffset.UTC)
  }
}
