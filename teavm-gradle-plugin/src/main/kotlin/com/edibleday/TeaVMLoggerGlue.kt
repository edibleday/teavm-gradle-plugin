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

import org.gradle.api.logging.Logger
import org.teavm.tooling.TeaVMToolLog

public class TeaVMLoggerGlue(val log: Logger) : TeaVMToolLog {

    override fun info(text: String) {
        log.info(text)
    }

    override fun debug(text: String) {
        log.debug(text)
    }

    override fun warning(text: String) {
        log.warn(text)
    }

    override fun error(text: String) {
        log.error(text)
    }

    override fun info(text: String, e: Throwable) {
        log.info(text, e)
    }

    override fun debug(text: String, e: Throwable) {
        log.debug(text, e)
    }

    override fun warning(text: String, e: Throwable) {
        log.warn(text, e)
    }

    override fun error(text: String, e: Throwable) {
        log.error(text, e)
    }
}
