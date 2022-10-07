lazy val scala212 = "2.12.13"

lazy val scala213 = "2.13.9"

lazy val scala3 = "3.1.1"

lazy val supportedScalaVersions = List(scala213, scala212, scala3)

crossScalaVersions := supportedScalaVersions

ThisBuild / scalaVersion := scala3

lazy val commonSettings = Seq(
  organization := "net.ssanj",
  licenses ++= Seq(("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))),
  scalacOptions ++= Seq(
                      "-encoding", "utf-8",
                      "-unchecked",
                      "-deprecation",
                      "-explaintypes",
                      "-feature",
                      "-Xfatal-warnings",
                      "-language:implicitConversions",
                      "-language:higherKinds"
                    ),

   scalacOptions ++=  (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => Seq(
        "-Xlint:_",
        "-Ywarn-dead-code",                
      )
      case Some((2, 13)) => Seq(
        "-Xsource:3",
        "-Xlint:_",
        "-Ywarn-dead-code",                
      )
      case _ => Seq.empty
    }),


  Compile/  console / scalacOptions := Seq(
                      "-encoding", "utf-8",
                      "-unchecked",
                      "-deprecation",
                      "-explaintypes",
                      "-feature",
                      "-language:implicitConversions",
                      "-language:higherKinds"
  ),

  sonatypeCredentialHost := "s01.oss.sonatype.org",

  Test / console / scalacOptions := (Compile / console / scalacOptions).value
)

lazy val scalaReflect = Def.setting { "org.scala-lang" % "scala-reflect" % scalaVersion.value }

lazy val scalaCheckVersion = "1.17.0"

lazy val boon = (project in file("core"))
  .dependsOn(boonMacro)
  .settings(
    commonSettings,
    name := "boon",
    testFrameworks := Seq(new TestFramework("boon.sbt.BoonFramework"), sbt.TestFrameworks.ScalaCheck),
    libraryDependencies ++= Seq(
        "org.scala-sbt"  % "test-interface" % "1.0",
        "org.scalacheck" %% "scalacheck"    % scalaCheckVersion % Test
    ),
    crossScalaVersions := supportedScalaVersions
  )


// lazy val boonLaws = (project in file("laws"))
//   .dependsOn(boon % "test->test;compile->compile")
//   .settings(
//     commonSettings,
//     name := "boon-laws",
//     testFrameworks := Seq(sbt.TestFrameworks.ScalaCheck),
//     libraryDependencies ++= Seq(
//         "org.scalacheck" %% "scalacheck" % scalaCheckVersion
//     ),
//     crossScalaVersions := supportedScalaVersions
//   )

lazy val boonMacro = (project in file("macro"))
  .settings(
    commonSettings,
    name := "boon-macro",
    libraryDependencies ++= 
      (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) => Seq(scalaReflect.value)
        case _ => Seq.empty
      }),
    crossScalaVersions := supportedScalaVersions
)

lazy val boonProj = (project in file(".")).
  settings(
    commonSettings,
    name := "boon-project",
    crossScalaVersions := supportedScalaVersions
  ).aggregate(boonMacro, boon/*, boonLaws*/)
