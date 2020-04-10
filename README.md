# azure-storage-sdk-wrapper-for-scala

Simple scala wrapper library wrapping the [azure storage SDK](https://github.com/Azure/azure-storage-java) providing functionality to:

* List blob items

* Upload blob items

* Download blob items

## Getting started

Add the core library to your dependencies:

```scala
"com.github.janjaali" %% "azure-storage-sdk-wrapper-for-scala" % "0.1.0-SNAPSHOT"
```

The main entry point is the `BlobStorageService` which can be instantiated with a `BlobServiceClient`:

```scala
import janjaali.blobstorage.BlobStorageService
import janjaali.blobstorage.clients.BlobServiceClient

val blobServiceClient = BlobServiceClient(
  endpoint = BlobStorageService.Endpoint("http://127.0.0.1:10000/devstoreaccount1"),
  credentials = BlobServiceClient.Credentials(
    accountName = BlobStorageService.Credentials.AccountName("devstoreaccount1"),
    accountKey = BlobStorageService.Credentials.AccountKey("...")
  ))

val blobStorageService = new BlobStorageService(blobServiceClient)
```

## Development

[SBT](https://www.scala-sbt.org/) is used as build tool. Tests can be executed via

```shell
sbt test
```

To publish the artifacts locally just `sbt publishLocal` and `sbt publishSigned` to publish a signed artifact to [Sonatype central repository](https://central.sonatype.org/).
