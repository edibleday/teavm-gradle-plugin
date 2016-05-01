[![](https://jitpack.io/v/com.edibleday/teavm-gradle-plugin.svg)](https://jitpack.io/#com.edibleday/teavm-gradle-plugin)

About
=====

This is Gradle plugin for compiling(transpiling) Java bytecode to JavaScript using [TeaVM](http://teavm.org/). Plugin uses TeaVMTool for compilation. Plugin is written in [Kotlin](http://kotlinlang.org/) and depends on `kotlin-stdlib`. Project that uses plugin will depend on `teavm-classlib`, `teavm-jso` and `teavm-jso-apis`. All dependencies can be acquired from Maven Central repo.

Plugin is tested with Java 6 and Kotlin but it should be able to transpile other JVM outputs too.

Quick Start
===========

Add repository and classpath in build script:
```
buildscript {
    repositories {
        maven { url "https://jitpack.io" }
    }

    dependencies {
        classpath 'com.edibleday:teavm-gradle-plugin:VERSION'
    }
}
```
Replace VERSION with actual version of plugin. See [releases](https://github.com/edibleday/teavm-gradle-plugin/releases) for version list.

You can also use git commit hash as version. Check [JitPack](https://jitpack.io/#com.edibleday/teavm-gradle-plugin) for list of hashes and build status.

Apply plugin to project:
```
apply plugin: "com.edibleday.teavm"
```

Add repositories for TeaVM dependencies
```
repositories {
    mavenCentral()
}
```

Set main class (one with `public static void main(String [] args)` method):
```
mainClassName = "my.package.Main"
```

Optionally set compilation options:
```
teavmc { //Optional configuration block
    /* Where to put final web app*/
    installDirectory "${project.buildDir}/teavm"
    /* Main javascript file name */
    targetFileName "app.js"
    /* Include simple html file that can launch app */
    mainPageIncluded true
    /* Copy sources to install directory for simpler debugging */
    copySources false
    /* Generate javascript to java mapping for debugging */
    generateSourceMap false
    /* Minify javascript */
    minified true
    /* Runtime javascript inclusion
        NONE: don't include runtime
        MERGED: merge runtime into main javascript file
        SEPARATE: include runtime as separated file
     */
    runtime org.teavm.tooling.RuntimeCopyOperation.SEPARATE
}
```

To compile project, run `teavmc` task:
```
./gradlew teavmc
```

Resulting application will be put into `installDirectory` (by default `build/teavm').

To run app, open `main.html` from `installDirectory`.

Usage
=====

Sample is located [here](https://github.com/edibleday/teavm-gradle-plugin-sample). Check comments in `build.gradle` file.

To compile javascript application, use `teavmc` task. By default output will be located in `build/teavm` directory.

It's possible to add source code for source code copying tasks by using dependency with `teavmsources` configuration that points to source artifact. Sample:

```
dependencies {
    teavmsources "com.test:test:1.0.0:sources"
}
```

Building
========

Use this command to build and publish plugin to local maven repo:

```
./gradlew publishToMavenLocal
```
