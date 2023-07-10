/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.5.1/userguide/building_java_projects.html
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    java
    `maven-publish`
    `java-library`
    idea
//    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

//java.sourceCompatibility = JavaVersion.VERSION_17
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

version = "1.2.1"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    flatDir {
        dirs("libs")
    }
    flatDir {
        dirs("plugins")
    }
    maven {
        url = uri("https://raw.github.com/Deses/RiverLayout/mvn-repo/")
    }

    dependencies {
        // This dependency is used by the application.
        implementation("com.google.guava:guava:31.0.1-jre")
        implementation("nz.ac.waikato.cms.weka:weka-stable:3.8.6")
        implementation("nz.ac.waikato.cms.weka:LibSVM:1.0.10")
        implementation("tw.edu.ntu.csie:libsvm:3.31")
        implementation(":libsvm")

        api("com.thoughtworks.xstream:xstream:1.4.19")
        api("com.thoughtworks.xstream:xstream-hibernate:1.4.19")
        api("edu.stanford.nlp:stanford-corenlp:3.9.2")
        api("edu.stanford.nlp:stanford-parser:3.9.2")
        api(":yeritools-min-1.0")
        api(":libsvm")
        api(":genesis")
//        api("junit:junit:4.13.2")
        testCompileOnly("junit:junit:4.13.2")
        api("se.datadosen.riverlayout:riverlayout:1.1")
//        api("nz.ac.waikato.cms.weka:weka-dev:3.9.6")
        api("nz.ac.waikato.cms.weka:bayesianLogisticRegression:1.0.5")
//        api("nz.ac.waikato.cms.weka:LibSVM:1.0.10")
        api("nz.ac.waikato.cms.weka:LibLINEAR:1.9.7")
        api("nz.ac.waikato.cms.weka:chiSquaredAttributeEval:1.0.4")
        api("com.oracle.database.xml:xmlparserv2:21.5.0.0")
        api("org.apache.commons:commons-math3:3.6.1")
        api("org.simpleframework:simple-http:6.0.1")
        api("org.simpleframework:simple:5.1.6")
        api("org.simpleframework:simple-transport:6.0.1")
    }

    testing {
        suites {
            // Configure the built-in test suite
            val test by getting(JvmTestSuite::class) {
                // Use JUnit4 test framework
                useJUnit("4.13.2")
                }
            }
    }

    tasks.withType<Test>().all {
        jvmArgs("--add-opens=java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED",
                "--add-opens=java.base/java.util=ALL-UNNAMED")
    }


    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
//        options.compilerArgs.addAll(arrayOf(
//            "--add-exports", "java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED",
//            "--add-exports", "java.base/java.util=ALL-UNNAMED"
//        ))
    }



//    application {
//        // Define the main class for the application.
//        mainClass.set("edu.cmu.side.WorkBench")
//    }
}