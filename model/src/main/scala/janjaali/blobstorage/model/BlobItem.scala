package janjaali.blobstorage.model

import java.time.OffsetDateTime

case class BlobItem(name: BlobName, properties: BlobItem.Properties)

object BlobItem {

  case class Properties(creationTime: OffsetDateTime,
                        lastModified: OffsetDateTime,
                        contentLength: Long,
                        contentType: Properties.ContentType)

  object Properties {

    case class ContentType(value: String) extends AnyVal
  }
}
