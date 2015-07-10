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
sourceGenerators in Compile += Def.task{
  //getting inputs
  val args = IO.readLines(file(".gen"))
  require(args.size == 2)

  //some settings
  val outputDir = (sourceManaged.value / "main").getPath
  val url = "jdbc:postgresql://localhost:5432/beast"
  val jdbcDriver = "org.postgresql.driver"
  val slickDriver = "slick.driver.PostgresDriver"
  val pkg = "com.beastserver.gen"

  //generating
  toError{
    (runner in Compile).value.run(
      "slick.codegen.SourceCodeGenerator",
      (dependencyClasspath in Compile).value.files,
      Array(slickDriver, jdbcDriver, url, outputDir, pkg, args.head, args(1)),
      streams.value.log
    )
  }

  val fname = outputDir + "/com/beastserver/gen/Tables.scala"
  Seq(file(fname))
}.taskValue