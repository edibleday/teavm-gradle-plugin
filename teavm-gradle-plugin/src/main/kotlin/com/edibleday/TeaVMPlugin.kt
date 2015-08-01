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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

public class TeaVMPlugin : Plugin<Project> {

    val version = "0.3.1"

    override fun apply(project: Project) {

        project.apply(mapOf(
                "plugin" to "java"
        ))

        project.apply(mapOf(
                "plugin" to "application"
        ))

        project.getConfigurations().create("teavmsources")

        project.getDependencies().let {
            it.add("compile", "org.teavm:teavm-classlib:$version")
            it.add("compile", "org.teavm:teavm-jso:$version")
            it.add("compile", "org.teavm:teavm-dom:$version")
            it.add("teavmsources", "org.teavm:teavm-platform:$version:sources")
            it.add("teavmsources", "org.teavm:teavm-classlib:$version:sources")
            it.add("teavmsources", "org.teavm:teavm-jso:$version:sources")
            it.add("teavmsources", "org.teavm:teavm-dom:$version:sources")
        }


        project.task(mapOf(
                Task.TASK_TYPE to javaClass<TeaVMTask>(),
                Task.TASK_DEPENDS_ON to "build",
                Task.TASK_DESCRIPTION to "TeaVM Compile",
                Task.TASK_GROUP to "build"
        ), "teavmc");


    }

}
