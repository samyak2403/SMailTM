//package com.samyak.smalitm
//
//import com.samyak.smalitm.callbacks.EventListener
//import com.samyak.smalitm.callbacks.MessageFetchedCallback
//import com.samyak.smalitm.util.Account
//import com.samyak.smalitm.util.Message
//import com.samyak.smalitm.util.Response
//import com.samyak.smalitm.util.SMaliBuilder
//import javax.security.auth.login.LoginException
//
///**
// * Example usage of SMaliTM library
// */
//object SMaliTMExamples {
//
//    /**
//     * Example 1: Create a random account
//     */
//    fun createRandomAccount() {
//        try {
//            val mailTM = SMaliBuilder.createDefault("myPassword123")
//            mailTM.init()
//
//            val account = mailTM.getSelf()
//            println("Created account: ${account.email}")
//            println("Account ID: ${account.id}")
//        } catch (e: LoginException) {
//            println("Failed to create account: ${e.message}")
//        }
//    }
//
//    /**
//     * Example 2: Create account with specific email
//     */
//    fun createSpecificAccount() {
//        try {
//            // First, get available domains
//            val domains = com.samyak.smalitm.util.Domains.fetchDomains()
//            val domain = domains.firstOrNull()?.domainName ?: return
//
//            val email = "myemail@$domain"
//            val mailTM = SMaliBuilder.createAndLogin(email, "password123")
//
//            println("Created account: ${mailTM.getSelf().email}")
//        } catch (e: LoginException) {
//            println("Failed to create account: ${e.message}")
//        }
//    }
//
//    /**
//     * Example 3: Login to existing account
//     */
//    fun loginToAccount() {
//        try {
//            val mailTM = SMaliBuilder.login("email@domain.com", "password")
//            println("Logged in as: ${mailTM.getSelf().email}")
//        } catch (e: LoginException) {
//            println("Login failed: ${e.message}")
//        }
//    }
//
//    /**
//     * Example 4: Login with token
//     */
//    fun loginWithToken() {
//        try {
//            val token = "your_jwt_token_here"
//            val mailTM = SMaliBuilder.loginWithToken(token)
//            println("Logged in with token: ${mailTM.getSelf().email}")
//        } catch (e: LoginException) {
//            println("Token login failed: ${e.message}")
//        }
//    }
//
//    /**
//     * Example 5: Fetch all messages
//     */
//    fun fetchAllMessages(mailTM: com.samyak.smalitm.SMaliTM) {
//        mailTM.fetchMessages(object : MessageFetchedCallback {
//            override fun onMessagesFetched(messages: List<Message>) {
//                println("Total messages: ${messages.size}")
//                messages.forEach { message ->
//                    println("---")
//                    println("From: ${message.senderAddress} (${message.senderName})")
//                    println("Subject: ${message.subject}")
//                    println("Content: ${message.content}")
//                    println("Seen: ${message.seen}")
//                    println("Has Attachments: ${message.hasAttachments}")
//                }
//            }
//
//            override fun onError(error: Response) {
//                println("Error fetching messages: ${error.responseCode}")
//            }
//        })
//    }
//
//    /**
//     * Example 6: Fetch limited messages
//     */
//    fun fetchLimitedMessages(mailTM: com.samyak.smalitm.SMaliTM) {
//        mailTM.fetchMessages(5, object : MessageFetchedCallback {
//            override fun onMessagesFetched(messages: List<Message>) {
//                println("Fetched ${messages.size} messages")
//            }
//
//            override fun onError(error: Response) {
//                println("Error: ${error.responseCode}")
//            }
//        })
//    }
//
//    /**
//     * Example 7: Async fetch messages
//     */
//    fun asyncFetchMessages(mailTM: com.samyak.smalitm.SMaliTM) {
//        mailTM.asyncFetchMessages(object : MessageFetchedCallback {
//            override fun onMessagesFetched(messages: List<Message>) {
//                println("Async fetched ${messages.size} messages")
//            }
//
//            override fun onError(error: Response) {
//                println("Async error: ${error.responseCode}")
//            }
//        })
//    }
//
//    /**
//     * Example 8: Get specific message by ID
//     */
//    fun getMessageById(mailTM: com.samyak.smalitm.SMaliTM, messageId: String) {
//        try {
//            val message = mailTM.getMessageById(messageId)
//            println("Message: ${message.subject}")
//            println("From: ${message.senderAddress}")
//            println("Content: ${message.content}")
//        } catch (e: Exception) {
//            println("Failed to get message: ${e.message}")
//        }
//    }
//
//    /**
//     * Example 9: Mark message as read
//     */
//    fun markMessageAsRead(message: Message) {
//        // Synchronous
//        val success = message.markAsRead()
//        println("Mark as read: $success")
//
//        // With callback
//        message.markAsRead { status ->
//            println("Mark as read callback: $status")
//        }
//
//        // Async
//        message.asyncMarkAsRead()
//
//        // Async with callback
//        message.asyncMarkAsRead { status ->
//            println("Async mark as read: $status")
//        }
//    }
//
//    /**
//     * Example 10: Delete message
//     */
//    fun deleteMessage(message: Message) {
//        // Synchronous
//        val success = message.delete()
//        println("Delete: $success")
//
//        // With callback
//        message.delete { status ->
//            println("Delete callback: $status")
//        }
//
//        // Async
//        message.asyncDelete()
//
//        // Async with callback
//        message.asyncDelete { status ->
//            println("Async delete: $status")
//        }
//    }
//
//    /**
//     * Example 11: Handle attachments
//     */
//    fun handleAttachments(message: Message) {
//        if (message.hasAttachments) {
//            message.attachments.forEach { attachment ->
//                println("Attachment: ${attachment.filename}")
//                println("Size: ${attachment.size} bytes")
//                println("Type: ${attachment.contentType}")
//
//                // Save attachment
//                attachment.save() // Saves to current directory
//
//                // Save with custom filename
//                attachment.save("custom_name.pdf")
//
//                // Save with callback
//                attachment.save("file.pdf") { status ->
//                    println("Download status: $status")
//                }
//
//                // Save to specific path
//                attachment.save("/path/to/save/", "filename.pdf") { status ->
//                    println("Saved: $status")
//                }
//            }
//        }
//    }
//
//    /**
//     * Example 12: Event listener for real-time updates
//     */
//    fun setupEventListener(mailTM: com.samyak.smalitm.SMaliTM) {
//        mailTM.openEventListener(object : EventListener {
//            override fun onReady() {
//                println("Event listener is ready")
//            }
//
//            override fun onClose() {
//                println("Event listener closed")
//            }
//
//            override fun onMessageReceived(message: Message) {
//                println("New message received!")
//                println("From: ${message.senderAddress}")
//                println("Subject: ${message.subject}")
//            }
//
//            override fun onMessageSeen(message: Message) {
//                println("Message seen: ${message.id}")
//            }
//
//            override fun onMessageDelete(id: String) {
//                println("Message deleted: $id")
//            }
//
//            override fun onAccountUpdate(account: Account) {
//                println("Account updated: ${account.email}")
//            }
//
//            override fun onAccountDelete(account: Account) {
//                println("Account deleted: ${account.email}")
//            }
//
//            override fun onError(error: String) {
//                println("Listener error: $error")
//            }
//        })
//    }
//
//    /**
//     * Example 13: Event listener with custom retry interval
//     */
//    fun setupEventListenerWithRetry(mailTM: com.samyak.smalitm.SMaliTM) {
//        mailTM.openEventListener(object : EventListener {
//            override fun onMessageReceived(message: Message) {
//                println("New message: ${message.subject}")
//            }
//
//            override fun onError(error: String) {
//                println("Error: $error")
//            }
//        }, retryInterval = 5000L) // 5 seconds
//    }
//
//    /**
//     * Example 14: Delete account
//     */
//    fun deleteAccount(mailTM: com.samyak.smalitm.SMaliTM) {
//        // Synchronous
//        val success = mailTM.delete()
//        println("Account deleted: $success")
//
//        // With callback
//        mailTM.delete { status ->
//            println("Delete callback: $status")
//        }
//
//        // Async
//        mailTM.asyncDelete()
//
//        // Async with callback
//        mailTM.asyncDelete { status ->
//            println("Async delete: $status")
//        }
//    }
//
//    /**
//     * Example 15: Get account info
//     */
//    fun getAccountInfo(mailTM: com.samyak.smalitm.SMaliTM) {
//        val account = mailTM.getSelf()
//        println("Email: ${account.email}")
//        println("ID: ${account.id}")
//        println("Quota: ${account.quota}")
//        println("Used: ${account.used}")
//        println("Disabled: ${account.isDisabled}")
//        println("Deleted: ${account.isDeleted}")
//        println("Created: ${account.createdAt}")
//        println("Updated: ${account.updatedAt}")
//    }
//
//    /**
//     * Example 16: Get total messages count
//     */
//    fun getTotalMessages(mailTM: com.samyak.smalitm.SMaliTM) {
//        val total = mailTM.getTotalMessages()
//        println("Total messages: $total")
//    }
//
//    /**
//     * Example 17: Working with domains
//     */
//    fun workWithDomains() {
//        // Fetch all domains
//        val domains = com.samyak.smalitm.util.Domains.fetchDomains()
//        println("Available domains:")
//        domains.forEach { domain ->
//            println("- ${domain.domainName} (Active: ${domain.isActive})")
//        }
//
//        // Get random domain
//        val randomDomain = com.samyak.smalitm.util.Domains.getRandomDomain()
//        println("Random domain: ${randomDomain.domainName}")
//
//        // Fetch domain by ID
//        try {
//            val domain = com.samyak.smalitm.util.Domains.fetchDomainById("domain_id")
//            println("Domain: ${domain.domainName}")
//        } catch (e: Exception) {
//            println("Domain not found")
//        }
//    }
//
//    /**
//     * Example 18: Complete workflow
//     */
//    fun completeWorkflow() {
//        try {
//            // 1. Create account
//            val mailTM = SMaliBuilder.createDefault("password123")
//            mailTM.init()
//            println("Account created: ${mailTM.getSelf().email}")
//
//            // 2. Setup event listener
//            mailTM.openEventListener(object : EventListener {
//                override fun onMessageReceived(message: Message) {
//                    println("New message: ${message.subject}")
//
//                    // Mark as read
//                    message.asyncMarkAsRead()
//                }
//
//                override fun onError(error: String) {
//                    println("Error: $error")
//                }
//            })
//
//            // 3. Wait for messages (in real app, this would be event-driven)
//            Thread.sleep(60000) // Wait 1 minute
//
//            // 4. Fetch all messages
//            mailTM.fetchMessages(object : MessageFetchedCallback {
//                override fun onMessagesFetched(messages: List<Message>) {
//                    println("Total messages: ${messages.size}")
//                }
//
//                override fun onError(error: Response) {
//                    println("Error: ${error.responseCode}")
//                }
//            })
//
//            // 5. Close listener
//            mailTM.closeMessageListener()
//
//            // 6. Delete account
//            mailTM.delete()
//            println("Account deleted")
//
//        } catch (e: Exception) {
//            println("Error: ${e.message}")
//        }
//    }
//}
