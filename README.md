About
=====

This is Gradle plugin for compiling(transpiling) Java bytecode to JavaScript using [TeaVM](http://teavm.org/). Plugin uses TeaVMTool for compilation. Plugin is written in [Kotlin](http://kotlinlang.org/) and depends on `kotlin-stdlib`. Project that uses plugin will depend on `teavm-classlib`, `teavm-jso` and `teavm-jso-apis`. All dependencies can be acquired from Maven Central repo.

Plugin is tested with Java 6 and Kotlin but it should be able to transpile other JVM outputs too.

Building
========

Use this command to build and publish plugin to local maven repo:

```
./gradlew teavm-gradle-plugin:publishToMavenLocal
```

Usage
=====

Sample is located in teavm-plugin-sample directory. Check comments in `build.gradle` file.

To compile javascript application, use `teavmc` task. By default output will be located in `build/teavm` directory.

It's possible to add source code for source code copying tasks by using dependency with `teavmsources` configuration that points to source artifact. Sample:

```
dependencies {
    teavmsources "com.test:test:1.0.0:sources"
}
```
