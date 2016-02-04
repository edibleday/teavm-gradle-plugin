/**
 * Copyright 2015 SIA "Edible Day"
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.edibleday

import org.apache.maven.plugin.MojoExecutionException
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.teavm.tooling.RuntimeCopyOperation
import org.teavm.tooling.TeaVMTool
import org.teavm.tooling.sources.DirectorySourceFileProvider
import org.teavm.tooling.sources.JarSourceFileProvider
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader
import java.util.*

open class TeaVMTask : DefaultTask() {

    var installDirectory: String = File(project.buildDir, "teavm").absolutePath
    var targetFileName: String = "app.js"
    var mainPageIncluded: Boolean = true
    var copySources: Boolean = false
    var generateSourceMap: Boolean = false
    var minified: Boolean = true
    var runtime: RuntimeCopyOperation = RuntimeCopyOperation.SEPARATE

    val log by lazy { TeaVMLoggerGlue(project.logger) }

    @TaskAction fun compTeaVM() {
        val tool = TeaVMTool()
        val project = project

        tool.targetDirectory = File(installDirectory)
        tool.targetFileName = targetFileName
        tool.isMainPageIncluded = mainPageIncluded

        if (project.hasProperty("mainClassName") && project.property("mainClassName") != null) {
            tool.mainClass = "${project.property("mainClassName")}"
        } else throw TeaVMException("mainClassName not found!")


        val addSrc = { f: File, tool: TeaVMTool ->
            if (f.isFile) {
                if (f.absolutePath.endsWith(".jar")) {
                    tool.addSourceFileProvider(JarSourceFileProvider(f))
                } else {
                    tool.addSourceFileProvider(DirectorySourceFileProvider(f))
                }
            } else {
                tool.addSourceFileProvider(DirectorySourceFileProvider(f))
            }

        }


        val convention = project.convention.getPlugin(JavaPluginConvention::class.java)

        convention
                .sourceSets
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                .allSource
                .srcDirs.forEach { addSrc(it, tool) }

        project
                .configurations
                .getByName("teavmsources")
                .files
                .forEach { addSrc(it, tool) }

        val cacheDirectory = File(project.buildDir, "teavm-cache")
        cacheDirectory.mkdirs()
        tool.cacheDirectory = cacheDirectory
        tool.runtime = runtime
        tool.isMinifying = minified
        tool.log = log
        tool.isSourceFilesCopied = copySources
        tool.isSourceMapsFileGenerated = generateSourceMap

        val classLoader = prepareClassLoader()
        try {
            tool.classLoader = classLoader
            tool.generate()
        } finally {
            try {
                classLoader.close()
            } catch (ignored: IOException) {
            }
        }

    }


    private fun prepareClassLoader(): URLClassLoader {
        try {
            val urls = ArrayList<URL>()
            val classpath = StringBuilder()
            for (file in project.configurations.getByName("runtime").files) {
                if (classpath.length > 0) {
                    classpath.append(':')
                }
                classpath.append(file.path)
                urls.add(file.toURI().toURL())
            }

            if (classpath.length > 0) {
                classpath.append(':')
            }
            classpath.append(File(project.buildDir, "classes/main").path)
            urls.add(File(project.buildDir, "classes/main").toURI().toURL())
            classpath.append(':')
            classpath.append(File(project.buildDir, "resources/main").path)
            urls.add(File(project.buildDir, "resources/main").toURI().toURL())

            return URLClassLoader(urls.toArray<URL>(arrayOfNulls<URL>(urls.size)), javaClass.classLoader)
        } catch (e: MalformedURLException) {
            throw MojoExecutionException("Error gathering classpath information", e)
        }

    }


}
