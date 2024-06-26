// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
package com.intellij.platform.impl.toolkit

import java.awt.Component
import java.awt.GraphicsEnvironment
import java.awt.Point
import java.awt.Window
import java.awt.event.MouseEvent
import java.awt.peer.MouseInfoPeer
import javax.swing.SwingUtilities

object IdeMouseInfoPeer: MouseInfoPeer {
  private val lastMouseCoords = Point()
  private var lastWindowUnderMouse: Component? = null
  private var lastComponentUnderCursor: Component? = null

  override fun fillPointWithCoords(point: Point): Int {
    point.location = lastMouseCoords
    return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().indexOfFirst {
      it.defaultConfiguration.bounds.contains(point)
    }
  }

  override fun isWindowUnderMouse(w: Window): Boolean {
    return w == lastWindowUnderMouse
  }

  fun getComponentUnderCursor(): Component? = lastComponentUnderCursor

  fun processMouseEvent(event: MouseEvent, window: Window) {
    lastMouseCoords.setLocation(event.xOnScreen, event.yOnScreen)
    lastWindowUnderMouse = window
    val component = SwingUtilities.getDeepestComponentAt(window, event.x, event.y)
    lastComponentUnderCursor = component
    if (event.id == MouseEvent.MOUSE_EXITED && component == window) {
      lastComponentUnderCursor = null
    }
  }
}