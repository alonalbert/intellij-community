// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.psi.search;

import com.intellij.openapi.application.ReadActionProcessor;
import com.intellij.psi.PsiReference;
import com.intellij.util.PairProcessor;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

public final class QuerySearchRequest {
  public final Query<PsiReference> query;
  public final SearchRequestCollector collector;
  public final Processor<? super PsiReference> processor;

  public QuerySearchRequest(@NotNull Query<PsiReference> query,
                            final @NotNull SearchRequestCollector collector,
                            boolean inReadAction,
                            final @NotNull PairProcessor<? super PsiReference, ? super SearchRequestCollector> processor) {
    this.query = query;
    this.collector = collector;
    if (inReadAction) {
      this.processor = new ReadActionProcessor<>() {
        @Override
        public boolean processInReadAction(PsiReference psiReference) {
          return processor.process(psiReference, collector);
        }
      };
    }
    else {
      this.processor = psiReference -> processor.process(psiReference, collector);
    }
  }

  public boolean runQuery() {
    return query.forEach(processor);
  }

  @Override
  public String toString() {
    return query + " -> " + collector;
  }
}
