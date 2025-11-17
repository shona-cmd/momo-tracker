package com.momotracker.ussd

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.momotracker.data.TransactionRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UssdAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var repo: TransactionRepository

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Initialize repository via dependency injection
        // repo = Injected via AppModule.provideRepository()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            if (it.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                val rootNode = rootInActiveWindow
                rootNode?.let { node ->
                    val text = extractTextFromNode(node)
                    if (text.contains("USSD") || text.contains("MoMo")) {
                        val transaction = TransactionParser.parse(text)
                        transaction?.let { tx ->
                            // Use CoroutineScope to insert transaction
                            CoroutineScope(Dispatchers.IO).launch { repo.insert(tx) }
                        }
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
        // Handle service interruption
    }

    private fun extractTextFromNode(node: AccessibilityNodeInfo): String {
        val stringBuilder = StringBuilder()
        if (node.text != null) {
            stringBuilder.append(node.text)
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            child?.let {
                stringBuilder.append(extractTextFromNode(it))
            }
        }
        return stringBuilder.toString()
    }
}
