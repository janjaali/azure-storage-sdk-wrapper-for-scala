import sbt._

object publishSettings {

  val credentials = Credentials(Path.userHome / ".sbt" / ".sonatype_credentials")

  def publishTo(isSnapshot: Boolean): Option[MavenRepository] = {

    val nexus = "https://oss.sonatype.org/"

    if (isSnapshot) {
      Some("snapshots" at nexus + "content/repositories/snapshots")
    } else {
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  }
}
