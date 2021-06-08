@file:JvmName("TabLayoutUtils")
@file:JvmMultifileClass

package com.superyao.dev.toolkit.ui

import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout

fun TabLayout.enableTabs(enable: Boolean) {
    getTabViewGroup()?.let {
        for (i in 0 until it.childCount) {
            val tabView = it.getChildAt(i)
            if (tabView != null) {
                tabView.isEnabled = enable
                tabView.alpha = if (enable) 1f else 0.5f
            }
        }
    }
}

fun TabLayout.getTabView(position: Int): View? {
    getTabViewGroup()?.let {
        if (it.childCount > position) {
            return it.getChildAt(position)
        }
    }
    return null
}

private fun TabLayout.getTabViewGroup(): ViewGroup? {
    if (childCount > 0) {
        val view = getChildAt(0)
        if (view is ViewGroup) {
            return view
        }
    }
    return null
}