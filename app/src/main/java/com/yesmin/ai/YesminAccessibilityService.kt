package com.yesmin.ai

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/**
 * Opt-in service foundation. Android shows the system consent screen;
 * the user must enable this service manually in Accessibility settings.
 * No covert interaction or automatic tapping is performed in this MVP.
 */
class YesminAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) { /* Observe only; no data is uploaded. */ }
    override fun onInterrupt() {}
}
