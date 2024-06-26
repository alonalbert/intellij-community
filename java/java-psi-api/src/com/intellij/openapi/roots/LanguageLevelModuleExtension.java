/*
 * Copyright 2000-2014 JetBrains s.r.o.
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
package com.intellij.openapi.roots;

import com.intellij.pom.java.LanguageLevel;
import org.jetbrains.annotations.Nullable;

public interface LanguageLevelModuleExtension {
  void setLanguageLevel(LanguageLevel languageLevel);

  /**
   * Returns explicitly specified custom language level for {@code module}, or {@code null} if the module uses 'Project default' language level.
   * May return {@linkplain LanguageLevel#isUnsupported() unsupported} language level.
   */
  @Nullable
  LanguageLevel getLanguageLevel();
}
