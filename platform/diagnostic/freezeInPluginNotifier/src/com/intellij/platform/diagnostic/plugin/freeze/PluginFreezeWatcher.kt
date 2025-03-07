// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.platform.diagnostic.plugin.freeze

import com.intellij.diagnostic.LogMessage
import com.intellij.diagnostic.ThreadDump
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.ide.plugins.PluginUtilImpl
import com.intellij.openapi.application.impl.ApplicationInfoImpl
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.extensions.PluginId
import com.intellij.platform.diagnostic.freezeAnalyzer.FreezeAnalyzer
import com.intellij.threadDumpParser.ThreadState
import com.intellij.util.application

@Service(Service.Level.APP)
internal class PluginFreezeWatcher {
  @Volatile
  private var reason: FreezeReason? = null

  private val stackTracePattern: Regex = """at (\S+)\.(\S+)\(([^:]+):(\d+)\)""".toRegex()

  companion object {
    @JvmStatic
    fun getInstance(): PluginFreezeWatcher = service()
  }

  fun getFreezeReason(): FreezeReason? = reason

  fun reset() {
    reason = null
  }

  fun dumpedThreads(event: LogMessage, dump: ThreadDump, durationMs: Long) : FreezeReason? {
    val freezeCausingThreads = FreezeAnalyzer.analyzeFreeze(dump.rawDump, null)?.threads.orEmpty()
    val pluginIds = freezeCausingThreads.mapNotNull { analyzeFreezeCausingPlugin(it) }
    val frozenPlugin = pluginIds.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: return null

    val pluginDescriptor = PluginManagerCore.getPlugin(frozenPlugin) ?: return null

    if (pluginDescriptor.isImplementationDetail || ApplicationInfoImpl.getShadowInstance().isEssentialPlugin(frozenPlugin)) {
      return FreezeReason(frozenPlugin, event, durationMs, reportToUser = false)
    }

    if (pluginDescriptor.isBundled && !application.isInternal && !application.isEAP) {
      return FreezeReason(frozenPlugin, event, durationMs, reportToUser = false)
    }

    val freezeStorageService = PluginsFreezesService.getInstance()
    if (freezeStorageService.shouldBeIgnored(frozenPlugin)) return null
    freezeStorageService.setLatestFreezeDate(frozenPlugin)

    reason = FreezeReason(frozenPlugin, event, durationMs, reportToUser = true)

    return reason
  }

  private fun analyzeFreezeCausingPlugin(threadInfo: ThreadState): PluginId? {
    val stackTraceElements = threadInfo.stackTrace.lineSequence()
      .mapNotNull { parseStackTraceElement(it) }
      .toList()
      .toTypedArray()

    return PluginUtilImpl.doFindPluginId(Throwable().apply { stackTrace = stackTraceElements })
  }

  private fun parseStackTraceElement(stackTrace: String): StackTraceElement? {
    return stackTracePattern.find(stackTrace.trim())?.let { matchResult ->
      val (className, methodName, fileName, lineNumber) = matchResult.destructured
      StackTraceElement(className, methodName, fileName, lineNumber.toInt())
    }
  }
}

internal data class FreezeReason(
  val pluginId: PluginId,
  val event: LogMessage,
  val durationMs: Long,
  val reportToUser: Boolean,
)
