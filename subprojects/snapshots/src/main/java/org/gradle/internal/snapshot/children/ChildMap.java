/*
 * Copyright 2020 the original author or authors.
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

package org.gradle.internal.snapshot.children;

import org.gradle.internal.snapshot.VfsRelativePath;

import java.util.Optional;

public interface ChildMap<T> {
    Optional<T> get(VfsRelativePath relativePath);

    ChildMap<T> put(VfsRelativePath relativePath, T value);

    class Entry<T> {
        private final String path;
        private final T value;

        private Entry(String path, T value) {
            this.path = path;
            this.value = value;
        }
    }
}
