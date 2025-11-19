package com.samyak.smalitm.callbacks

import com.samyak.smalitm.util.Message

/**
 * The Message Listener Callback. Runs when some new message or email has arrived
 */
@Deprecated("Use EventListener instead")
interface MessageListener {
    fun onReady() {
        // Do What you want
    }
    
    fun onClose() {
        // Do What you want
    }
    
    fun onMessageReceived(message: Message)
    fun onError(error: String)
}
