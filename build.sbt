
ThisBuild / scalaVersion := "2.12.9"

// from gradle/dependencies.gradle
val version_activation = "1.1.1"
val version_apacheda = "1.0.2"
val version_apacheds = "2.0.0-M24"
val version_argparse4j = "0.7.0"
val version_bcpkix = "1.62"
val version_checkstyle = "8.20"
val version_commonsCli = "1.4"
val version_gradle = "5.4.1"
val version_gradleVersionsPlugin = "0.21.0"
val version_grgit = "3.1.1"
val version_httpclient = "4.5.9"
val version_easymock = "4.0.2"
val version_jackson = "2.9.9"
val version_jacksonDatabind = "2.9.9.3"
val version_jacoco = "0.8.3"
val version_jetty = "9.4.19.v20190610"
val version_jersey = "2.28"
val version_jmh = "1.21"
val version_hamcrest = "2.1"
val version_log4j = "1.2.17"
val version_scalaLogging = "3.9.2"
val version_jaxb = "2.3.0"
val version_jaxrs = "2.1.1"
val version_jfreechart = "1.0.0"
val version_jopt = "5.0.4"
val version_junit = "4.13-beta-2"
val version_kafka_0100 = "0.10.0.1"
val version_kafka_0101 = "0.10.1.1"
val version_kafka_0102 = "0.10.2.2"
val version_kafka_0110 = "0.11.0.3"
val version_kafka_10 = "1.0.2"
val version_kafka_11 = "1.1.1"
val version_kafka_20 = "2.0.1"
val version_kafka_21 = "2.1.1"
val version_kafka_22 = "2.2.1"
val version_lz4 = "1.6.0"
val version_mavenArtifact = "3.6.1"
val version_metrics = "2.2.0"
val version_mockito = "3.0.0"
val version_owaspDepCheckPlugin = "5.2.1"
val version_powermock = "2.0.2"
val version_reflections = "0.9.11"
val version_rocksDB = "5.18.3"
val version_scalaCollectionCompat = "2.1.2"
val version_scalafmt = "1.5.1"
val version_scalaJava8Compat = "0.9.0"
val version_scalatest = "3.0.8"
val version_scoverage = "1.4.0"
val version_scoveragePlugin = "2.5.0"
val version_shadowPlugin = "4.0.4"
val version_slf4j = "1.7.27"
val version_snappy = "1.1.7.3"
val version_spotbugs = "3.1.12"
val version_spotbugsPlugin = "1.6.9"
val version_spotlessPlugin = "3.23.1"
val version_zookeeper = "3.5.5"
val version_zstd = "1.4.2-1"

lazy val kafka = project
  .in(file("."))
  .aggregate(
    core
  )

val additionalScalacOptions = Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "utf8",
  "-Xlog-reflective-calls",
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:existentials",
  "-Xlint:delayedinit-select",
  "-Xlint:doc-detached",
  "-Xlint:missing-interpolator",
  "-Xlint:nullary-override",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit",
  "-Xlint:package-object-classes",
  "-Xlint:poly-implicit-overload",
  "-Xlint:private-shadow",
  "-Xlint:stars-align",
  "-Xlint:type-parameter-shadow",
  "-Xlint:constant",
  "-Xlint:unused"
)

lazy val generator = project
  .settings(
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % version_jackson,
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % version_jackson,
      "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % version_jackson,
      "junit" % "junit" % version_junit % Test,
    )
  )

lazy val clients = project
  .dependsOn(
    generator
  )
  .settings(
    scalacOptions ++= additionalScalacOptions ++ {
      if (scalaBinaryVersion.value != "2.13") Seq(
        "-Xlint:by-name-right-associative",
        "-Xlint:unsound-match"
      ) else Seq.empty
    },
    sourceGenerators in Compile += Def.task {
      (generator / Compile / run).toTask(" clients/src/generated/java/org/apache/kafka/common/message clients/src/main/resources/common/message/").value
      file(baseDirectory.value.getAbsolutePath + "/src/generated/java/").globRecursive("*.java").get()
    }.taskValue,
    libraryDependencies ++= Seq(
      "com.github.luben" % "zstd-jni" % version_zstd,
      "org.lz4" % "lz4-java" % version_lz4,
      "org.xerial.snappy" % "snappy-java" % version_snappy,
      "org.slf4j" % "slf4j-api" % version_slf4j,
      "com.fasterxml.jackson.core" % "jackson-databind" % version_jackson,
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % version_jackson,
      "org.bouncycastle" % "bcpkix-jdk15on" % version_bcpkix % Test,
      "junit" % "junit" % version_junit % Test,
      "org.mockito" % "mockito-core" % version_mockito % Test
    )
  )

lazy val core = project
  .dependsOn(
    clients % "compile->compile;test->test"
  )
  .settings(
    scalacOptions ++= additionalScalacOptions ++ {
      if (scalaBinaryVersion.value != "2.13") Seq(
        "-Xlint:by-name-right-associative",
        "-Xlint:unsound-match"
      ) else Seq.empty
    },
    // -a Show stack traces and exception class name for AssertionErrors.
    // -v Log "test run started" / "test started" / "test run finished" events on log level "info" instead of "debug".
    // -q Suppress stdout for successful tests.
    testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-v"),
    Test / fork := true,
    Test / parallelExecution := false,
    Test / unmanagedSourceDirectories := Seq(
      baseDirectory.value / "src" / "test" / "scala",
      baseDirectory.value / "src" / "test" / "scala" / "integration",
      baseDirectory.value / "src" / "test" / "scala" / "unit",
      baseDirectory.value / "src" / "test" / "scala" / "other"),
    Test / unmanagedSources / excludeFilter := HiddenFileFilter
      || "AddPartitionsTest.scala"
      || "AddPartitionsToTxnRequestTest.scala"
      || "ConsumerBounceTest.scala"
      || "ConsumerPerformanceTest.scala"
      || "DeleteTopicsRequestWithDeletionDisabledTest.scala"
      || "DescribeLogDirsRequestTest.scala"
      || "DynamicConnectionQuotaTest.scala"
      || "EdgeCaseRequestTest.scala"
      || "FetchRequestDownConversionConfigTest.scala"
      || "GroupAuthorizerIntegrationTest.scala"
      || "GroupEndToEndAuthorizationTest.scala"
      || "GssapiAuthenticationTest.scala"
      || "KafkaMetricReporterExceptionHandlingTest.scala"
      || "ListOffsetsRequestTest.scala"
      || "LogOffsetTest.scala"
      || "MultipleListenersWithDefaultJaasContextTest.scala"
      || "MultipleListenersWithAdditionalJaasContextTest.scala"
      || "ReassignPartitionsClusterTest.scala"
      || "RequestQuotaTest.scala"
      || "SaslApiVersionsRequestTest.scala"
      || "SaslGssapiSslEndToEndAuthorizationTest.scala"
      || "SaslMultiMechanismConsumerTest.scala"
      || "SaslPlaintextConsumerTest.scala"
      || "SaslPlainSslEndToEndAuthorizationTest.scala"
      || "SaslSslAdminClientIntegrationTest.scala"
      || "SaslSslConsumerTest.scala"
      || "SimpleAclAuthorizerTest.scala"
      || "SocketServerTest.scala"
      || "StreamTableJoinIntegrationTest.java"
      || "TransactionsBounceTest.scala"
      || "UserQuotaTest.scala"
    /*
      Ignored via JUnit annotations:
      AuthorizerIntegrationTest. ...
      ConsumerBounceTest. ...
      EpochDrivenReplicationProtocolAcceptanceTest.offsetsShouldNotGoBackwards (which is excluded above?!)
     */
      ,
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % version_jacksonDatabind,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % version_jackson,
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % version_jackson,
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % version_jackson,
      "net.sf.jopt-simple" % "jopt-simple" % version_jopt,
      "com.yammer.metrics" % "metrics-core" % version_metrics,
      "org.scala-lang.modules" %% "scala-java8-compat" % version_scalaJava8Compat,
      "org.scala-lang.modules" %% "scala-collection-compat" % version_scalaCollectionCompat,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "com.typesafe.scala-logging" %% "scala-logging" % version_scalaLogging,
      "org.slf4j" % "slf4j-api" % version_slf4j,
      "org.apache.zookeeper" % "zookeeper" % version_zookeeper excludeAll (
        ExclusionRule("org.slf4j", "slf4j-log4j12"),
        ExclusionRule("log4j", "log4j"),
        ExclusionRule("io.netty", "netty")
      ),
      "commons-cli" % "commons-cli" % version_commonsCli,
      "log4j" % "log4j" % version_log4j,

      "org.bouncycastle" % "bcpkix-jdk15on" % version_bcpkix % Test,
      "org.mockito" % "mockito-core" % version_mockito % Test,
      "org.easymock" % "easymock" % version_easymock % Test,
      "org.apache.directory.api" % "api-all" % version_apacheda % Test excludeAll (
        ExclusionRule("xml-apis", "xml-apis"),
        ExclusionRule("org.apache.mina", "mina-core"),
        // From build.gradle
        // To prevent a UniqueResourceException due the same resource existing in both
        // org.apache.directory.api/api-all and org.apache.directory.api/api-ldap-schema-data
        ExclusionRule("org.apache.directory.api", "api-ldap-schema-data")
      ),
      "org.apache.directory.server" % "apacheds-core-api" % version_apacheds % Test,
      "org.apache.directory.server" % "apacheds-interceptor-kerberos" % version_apacheds % Test,
      "org.apache.directory.server" % "apacheds-protocol-shared" % version_apacheds % Test,
      "org.apache.directory.server" % "apacheds-protocol-kerberos" % version_apacheds % Test,
      "org.apache.directory.server" % "apacheds-protocol-ldap" % version_apacheds % Test,
      "org.apache.directory.server" % "apacheds-ldif-partition" % version_apacheds % Test,
      "org.apache.directory.server" % "apacheds-mavibot-partition" % version_apacheds % Test,
      "org.apache.directory.server" % "apacheds-jdbm-partition" % version_apacheds % Test,
      "junit" % "junit" % version_junit % Test,
      "org.scalatest" %% "scalatest" % version_scalatest % Test, // ApacheV2
      "org.slf4j" % "slf4j-log4j12" % version_slf4j % Test,
      "jfree" % "jfreechart" % version_jfreechart % Test,
      //
      "com.novocode" % "junit-interface" % "0.11" % Test, // BSD-style
//      "org.junit.jupiter" % "junit-jupiter-api" % JupiterKeys.junitJupiterVersion.value % Provided
    )
  )

lazy val streams = project
  .dependsOn(
    clients % "compile->test;test->test",
    core % "compile->test;test->test"
  )
  .settings(
    Test / unmanagedSourceDirectories += baseDirectory.value / "test-utils" / "src" /  "main" / "java",
    Test / unmanagedSources / excludeFilter := HiddenFileFilter
      || "EosIntegrationTest.java"
      || "InMemoryKeyValueStoreTest.java"
      || "KStreamAggregationIntegrationTest.java"
      || "ResetIntegrationTest.java"
      || "ResetIntegrationWithSslTest.java"
      || "StreamStreamJoinIntegrationTest.java"
      || "StreamTableJoinIntegrationTest.java"
      || "TableTableJoinIntegrationTest.java",
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-api" % version_slf4j,
      "org.rocksdb" % "rocksdbjni" % version_rocksDB,
      /*

    // testCompileOnly prevents streams from exporting a dependency on test-utils, which would cause a dependency cycle
    testCompileOnly project(':streams:test-utils')
    testCompile project(':clients').sourceSets.test.output
    testCompile project(':core')
    testCompile project(':core').sourceSets.test.output
    */
      "log4j" % "log4j" % version_log4j % Test,
      "junit" % "junit" % version_junit % Test,
      "org.easymock" % "easymock" % version_easymock % Test,
      "org.powermock" % "powermock-module-junit4" % version_powermock % Test,
      "org.powermock" % "powermock-api-easymock" % version_powermock % Test,
      "org.bouncycastle" % "bcpkix-jdk15on" % version_bcpkix % Test,
      "org.hamcrest" % "hamcrest" % version_hamcrest % Test,
/*
    testRuntimeOnly project(':streams:test-utils')
    testRuntime libs.slf4jlog4j
       */
      "org.slf4j" % "slf4j-log4j12" % version_slf4j,
    )
  )

lazy val `streams-scala` = project.in(file("streams/streams-scala"))
  .dependsOn(
    streams % "compile->compile;test->test"
  )
