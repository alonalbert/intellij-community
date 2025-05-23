/*
 * Copyright 2000-2015 JetBrains s.r.o.
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
package com.intellij.psi.codeStyle.extractor.differ;

import com.intellij.lang.LanguageExtension;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.extractor.values.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

public interface LangCodeStyleExtractor {
  LanguageExtension<LangCodeStyleExtractor> EXTENSION =
    new LanguageExtension<>("com.intellij.lang.formatting.extractor");

  @NotNull
  Differ getDiffer(Project project, PsiFile psiFile, CodeStyleSettings settings);

  @NotNull
  @Unmodifiable
  Collection<Value.VAR_KIND> getCustomVarKinds();
}
