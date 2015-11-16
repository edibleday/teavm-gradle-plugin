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
import org.teavm.tooling.sources.DirectorySourceFileProvider
import org.teavm.tooling.sources.JarSourceFileProvider
import org.teavm.tooling.RuntimeCopyOperation
import org.teavm.tooling.TeaVMTool
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader
import java.util.ArrayList

public open class TeaVMTask : DefaultTask() {

    public var installDirectory: String = File(getProject().getBuildDir(), "teavm").getAbsolutePath()
    public var targetFileName: String = "app.js"
    public var mainPageIncluded: Boolean = true
    public var copySources: Boolean = false
    public var generateSourceMap: Boolean = false
    public var minified: Boolean = true
    public var runtime: RuntimeCopyOperation = RuntimeCopyOperation.SEPARATE

    val log by lazy { TeaVMLoggerGlue(getProject().getLogger()) }

    @TaskAction public fun compTeaVM() {
        val tool = TeaVMTool()
        val project = getProject()

        tool.setTargetDirectory(File(installDirectory))
        tool.setTargetFileName(targetFileName)
        tool.setMainPageIncluded(mainPageIncluded)

        if (project.hasProperty("mainClassName") && project.property("mainClassName") != null) {
            tool.setMainClass("${project.property("mainClassName")}")
        } else throw TeaVMException("mainClassName not found!")


        val addSrc = { f: File, tool: TeaVMTool ->
            if (f.isFile()) {
                if (f.getAbsolutePath().endsWith(".jar")) {
                    tool.addSourceFileProvider(JarSourceFileProvider(f))
                } else {
                    tool.addSourceFileProvider(DirectorySourceFileProvider(f))
                }
            } else {
                tool.addSourceFileProvider(DirectorySourceFileProvider(f))
            }

        }


        val convention = project.getConvention().getPlugin(JavaPluginConvention::class.java)

        convention
                .getSourceSets()
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                .getAllSource()
                .getSrcDirs().forEach { addSrc(it, tool) }

        project
                .getConfigurations()
                .getByName("teavmsources")
                .getFiles()
                .forEach { addSrc(it, tool) }

        val cacheDirectory = File(project.getBuildDir(), "teavm-cache")
        cacheDirectory.mkdirs()
        tool.setCacheDirectory(cacheDirectory)
        tool.setRuntime(runtime)
        tool.setMinifying(minified)
        tool.setLog(log)
        tool.setSourceFilesCopied(copySources)
        tool.setSourceMapsFileGenerated(generateSourceMap)

        val classLoader = prepareClassLoader()
        try {
            tool.setClassLoader(classLoader)
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
            for (file in getProject().getConfigurations().getByName("runtime").getFiles()) {
                if (classpath.length > 0) {
                    classpath.append(':')
                }
                classpath.append(file.getPath())
                urls.add(file.toURI().toURL())
            }

            if (classpath.length > 0) {
                classpath.append(':')
            }
            classpath.append(File(getProject().getBuildDir(), "classes/main").getPath())
            urls.add(File(getProject().getBuildDir(), "classes/main").toURI().toURL())
            classpath.append(':')
            classpath.append(File(getProject().getBuildDir(), "resources/main").getPath())
            urls.add(File(getProject().getBuildDir(), "resources/main").toURI().toURL())

            return URLClassLoader(urls.toArray<URL>(arrayOfNulls<URL>(urls.size)), javaClass.getClassLoader())
        } catch (e: MalformedURLException) {
            throw MojoExecutionException("Error gathering classpath information", e)
        }

    }


}
