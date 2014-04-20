/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.plugins.announce

import org.gradle.integtests.fixtures.WellBehavedPluginTest

class AnnouncePluginIntegrationTest extends WellBehavedPluginTest {
    private static String isHeadlessProperty

    @Override
    String getPluginId() {
        return "announce"
    }

    @Override
    String getMainTask() {
        return "tasks"
    }

    void setupSpec() {
        isHeadlessProperty = System.getProperty("java.awt.headless", "false")
        System.setProperty("java.awt.headless", "true")
    }

    void cleanupSpec() {
        System.setProperty("java.awt.headless", isHeadlessProperty)
    }

    def "does not blow up in headless mode when a local notification mechanism is not available"() {
        buildFile << """
apply plugin: 'java'
apply plugin: 'build-announcements'
"""

        expect:
        succeeds 'assemble'
    }
}
