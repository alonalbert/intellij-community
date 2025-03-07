// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.debugger.engine.evaluation.expression;

import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluateExceptionUtil;
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl;
import com.sun.jdi.BooleanValue;
import org.jetbrains.annotations.NotNull;

public class WhileStatementEvaluator extends LoopEvaluator {
  private final Evaluator myConditionEvaluator;

  public WhileStatementEvaluator(@NotNull Evaluator conditionEvaluator, Evaluator bodyEvaluator, String labelName) {
    super(labelName, bodyEvaluator);
    myConditionEvaluator = DisableGC.create(conditionEvaluator);
  }

  @Override
  public Object evaluate(EvaluationContextImpl context) throws EvaluateException {
    Object value;
    while (true) {
      value = myConditionEvaluator.evaluate(context);
      if (!(value instanceof BooleanValue)) {
        throw EvaluateExceptionUtil.BOOLEAN_EXPECTED;
      }
      else {
        if (!((BooleanValue)value).booleanValue()) {
          break;
        }
      }

      if (body(context)) break;
    }

    return value;
  }
}
