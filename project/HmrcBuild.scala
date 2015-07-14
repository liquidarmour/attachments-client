import sbt.Keys._
import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning


object HmrcBuild extends Build {

  import BuildDependencies._
  import uk.gov.hmrc.DefaultBuildSettings._

  val appName = "attachments-client"

  lazy val AttachmentsClient = (project in file("."))
    .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning)
    .settings(
      name := appName,
      targetJvm := "jvm-1.7",
      libraryDependencies ++= Seq(
        Compile.verbs,
        Compile.domain,
        Compile.config,
        Test.scalaTest,
        Test.pegdown
      ),
      Developers()
    )
}

private object BuildDependencies {

  object Compile {
    val verbs = "uk.gov.hmrc" %% "http-verbs" % "1.8.0-30-g34fe7a2" % "provided"
    val domain = "uk.gov.hmrc" %% "domain" % "2.9.0" % "provided"
    val config = "uk.gov.hmrc" %% "play-config" % "1.0.0" % "provided"
  }

  sealed abstract class Test(scope: String) {
    val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % scope
    val pegdown = "org.pegdown" % "pegdown" % "1.5.0" % scope
  }

  object Test extends Test("test")

}

object Developers {

  def apply() = developers := List[Developer]()
}
