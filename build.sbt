ThisBuild / name := "azure-storage-sdk-wrapper-for-scala"

ThisBuild / organization := "com.github.janjaali"

ThisBuild / version := "0.1.0"

ThisBuild / licenses := Seq("MIT License" -> url("https://opensource.org/licenses/MIT"))

ThisBuild / homepage := Some(url("https://github.com/janjaali/azure-storage-cdn-mock"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/janjaali/azure-storage-cdn-mock"),
    "scm:git@github.com:janjaali/azure-storage-cdn-mock.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "janjaali",
    name  = "janjaali",
    email = "",
    url   = url("http://github.com/janjaali")
  )
)

ThisBuild / scalaVersion := "2.13.1"

ThisBuild / scalacOptions := Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred.
  "-language:higherKinds", // Allow higher-kinded types.
  "-language:implicitConversions", // Allow definition of implicit functions called views.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:nullary-override", // Warn when non-nullary def f() overrides nullary def f.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be Any.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:unused", // Enable -Wunused:imports,privates,locals,implicits.
  "-Xlint:nonlocal-return", // A return statement used an exception for flow control.
  "-Xlint:deprecation", // Enable linted deprecations.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-octal-literal", // Warn on obsolete octal syntax.
  "-Wunused:imports", // Warn if an import selector is not referenced.
  "-Wunused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Wunused:privates", // Warn if a private member is unused.
  "-Wunused:locals", // Warn if a local definition is unused.
  "-Wunused:params" // Enable -Wunused:explicits,implicits.
)

lazy val supportedScalaVersions = List("2.12.4", "2.13.0")
ThisBuild / crossScalaVersions := supportedScalaVersions

publishMavenStyle := true

ThisBuild / credentials += publishSettings.credentials

ThisBuild / publishTo := publishSettings.publishTo(isSnapshot.value)

skip in publish := true

lazy val lib = (project in file("lib"))
  .settings(
    name := "azure-storage-sdk-wrapper-for-scala",

    libraryDependencies ++= Seq(
      // tests
      "org.scalatest" %% "scalatest" % "3.1.1" % Test,

      // mocking
      "org.scalamock" %% "scalamock" % "4.4.0" % Test,

      // logging
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",

      // stream processing
      "com.typesafe.akka" %% "akka-stream" % "2.6.4",

      // azure storage
      "com.azure" % "azure-storage-blob" % "12.6.0"
    )
  )
  .dependsOn(model, scalaCheckTestLib % "test->test")

lazy val model = (project in file("model"))
  .settings(
    name := "azure-storage-sdk-wrapper-for-scala-model"
  )

lazy val scalaCheckTestLib = (project in file("scalaCheckTestLib"))
  .settings(
    name := "azure-storage-sdk-wrapper-for-scala-scalacheck",

    libraryDependencies ++= Seq(
      // property based tests
      "org.scalatestplus" %% "scalacheck-1-14" % "3.1.0.0"
    )
  )
  .dependsOn(model)
