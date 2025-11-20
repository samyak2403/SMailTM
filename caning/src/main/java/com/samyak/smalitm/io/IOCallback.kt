package com.samyak.smalitm.io

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.MessageEvent
import com.samyak.smalitm.SMailTM
import com.samyak.smalitm.callbacks.EventListener
import com.samyak.smalitm.util.Account

class IOCallback(
    private val listener: EventListener,
    private val mailTM: SMailTM
) : EventHandler {

    override fun onOpen() {
        listener.onReady()
    }

    override fun onClosed() {
        listener.onClose()
    }

    override fun onMessage(event: String, messageEvent: MessageEvent) {
        val data = messageEvent.data.trim()
        try {
            if (data.isNotEmpty()) {
                val json = JsonParser.parseString(messageEvent.data).asJsonObject
                
                when (json.get("@type").asString) {
                    "Message" -> {
                        val seen = json.get("seen").asBoolean
                        val isDeleted = json.get("isDeleted").asBoolean
                        val id = json.get("id").asString
                        
                        when {
                            isDeleted -> listener.onMessageDelete(id)
                            seen -> listener.onMessageSeen(mailTM.getMessageById(id))
                            else -> listener.onMessageReceived(mailTM.getMessageById(id))
                        }
                    }
                    else -> {
                        val field = mailTM.javaClass.getDeclaredField("gson")
                        field.isAccessible = true
                        val gson = field.get(mailTM) as Gson
                        val account = gson.fromJson(json, Account::class.java)
                        
                        if (account.isDeleted) {
                            listener.onAccountDelete(account)
                        } else {
                            listener.onAccountUpdate(account)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            listener.onError(e.message ?: "Unknown error")
        }
    }

    override fun onComment(comment: String) {
        listener.onSSEComment(comment)
    }

    override fun onError(t: Throwable) {
        listener.onError(t.message ?: "Unknown error")
    }
}
