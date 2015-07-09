name := "BeastServer"

version       := "0.1"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-target:jvm-1.8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "com.typesafe.slick"  %%  "slick"         % "3.0.0",
    "com.typesafe.slick"  %% "slick-codegen"  % "3.0.0",
    "org.postgresql"      %   "postgresql"    % "9.4-1201-jdbc41"
  )
}

//Using for slick tables generating
//Look at https://github.com/slick/slick-codegen-example/
//Includes db settings for code gen
//Note: should use the same driver in the rest of app
lazy val genTables = inputKey[Unit]("Generates slick tables. The format is \"genTables <driver:url:port ><db> <user> <pswd>\"")

genTables := {
  import sbt.complete.DefaultParsers._

  //parsing inputs
  val args: Seq[String] = spaceDelimited("<arg>").parsed
  require(args.size == 4)

  //some settings
  val outputDir = (sourceManaged.value / "slick").getPath
  val url = args.head + args(1)
  val jdbcDriver = "org.postgresql.driver"
  val slickDriver = "slick.driver.PostgresDriver"
  val pkg = "com.beastserver.dao"

  //generating
  toError{
    (runner in Compile).value.run(
      "slick.codegen.SourceCodeGenerator",
      (dependencyClasspath in Compile).value.files,
      Array(slickDriver, jdbcDriver, url, outputDir, pkg, args(2), args(3)),
      streams.value.log
    )
  }
}