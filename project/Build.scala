import com.typesafe.sbt.SbtPgp
import sbt.Keys._
import sbt.{Credentials, Path, _}
import sbt.plugins.JvmPlugin
import xerial.sbt.Sonatype

object Build extends AutoPlugin {

  override def trigger  = AllRequirements
  override def requires = JvmPlugin

  object autoImport {
    val org                    = "com.sksamuel.elastic4s"
    val AkkaVersion            = "2.5.23"
    val AkkaHttpVersion        = "10.1.8"
    val CatsVersion            = "1.6.0"
    val CatsEffectVersion      = "1.3.1"
    val CirceVersion           = "0.12.0-M2"
    val CommonsIoVersion       = "2.6"
    val ElasticsearchVersion   = "7.0.1"
    val ExtsVersion            = "1.61.0"
    val JacksonVersion         = "2.9.9"
    val Json4sVersion          = "3.6.6"
    val AWSJavaSdkVersion      = "2.5.52"
    val Log4jVersion           = "2.11.1"
    val MockitoVersion         = "1.10.19"
    val MonixVersion           = "2.3.3"
    val PlayJsonVersion        = "2.7.3"
    val ReactiveStreamsVersion = "1.0.2"
    val ScalatestVersion       = "3.0.8"
    val ScalamockVersion       = "4.1.0"
    val ScalazVersion          = "7.2.27"
    val SprayJsonVersion       = "1.3.5"
    val SttpVersion            = "1.5.18"
    val Slf4jVersion           = "1.7.26"
  }

  import autoImport._

  def isTravis = System.getenv("TRAVIS") == "true"
  def travisBuildNumber = System.getenv("TRAVIS_BUILD_NUMBER")

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    organization := org,
    scalaVersion := "2.12.8",
    crossScalaVersions := Seq("2.11.12", "2.12.8"),
    publishMavenStyle := true,
    resolvers += Resolver.mavenLocal,
    resolvers += Resolver.url("https://artifacts.elastic.co/maven"),
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),
    publishArtifact in Test := false,
    fork in Test := false,
    parallelExecution in ThisBuild := false,
    SbtPgp.autoImport.useGpg := true,
    SbtPgp.autoImport.useGpgAgent := true,
    if (isTravis) {
      credentials += Credentials(
        "Sonatype Nexus Repository Manager",
        "oss.sonatype.org",
        sys.env("OSSRH_USERNAME"),
        sys.env("OSSRH_PASSWORD")
      )
    } else {
      credentials += Credentials(Path.userHome / ".sbt" / "credentials.sbt")
    },
    publishTo := Sonatype.autoImport.sonatypePublishTo.value,
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
    javacOptions := Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies ++= Seq(
      "com.sksamuel.exts" %% "exts" % ExtsVersion,
      "org.slf4j" % "slf4j-api" % Slf4jVersion,
      "org.mockito" % "mockito-all" % MockitoVersion % "test",
      "org.scalatest" %% "scalatest" % ScalatestVersion % "test"
    ),
    if (isTravis) {
      version := s"7.0.2.$travisBuildNumber-SNAPSHOT"
    } else {
      version := "7.0.2"
    },
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra :=
      <url>https://github.com/sksamuel/elastic4s</url>
        <licenses>
          <license>
            <name>Apache 2</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:sksamuel/elastic4s.git</url>
          <connection>scm:git@github.com:sksamuel/elastic4s.git</connection>
        </scm>
        <developers>
          <developer>
            <id>sksamuel</id>
            <name>sksamuel</name>
            <url>http://github.com/sksamuel</url>
          </developer>
        </developers>
  )
}
