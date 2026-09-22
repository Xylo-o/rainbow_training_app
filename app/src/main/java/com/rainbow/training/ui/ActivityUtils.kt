package com.rainbow.training.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/** Znajduje [Activity] kryjaca sie pod kontekstem Compose (moze byc opakowana kilka razy). */
tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
