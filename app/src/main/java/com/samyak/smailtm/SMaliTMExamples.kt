//package com.samyak.smailtm
//
///**
// * SMailTM Library - Complete Usage Examples
// *
// * This file demonstrates various ways to use the SMailTM library
// * for creating temporary email accounts and managing messages.
// *
// * @author Samyak
// * @version 1.0.1
// */
//
//import com.samyak.smailtm.SMailTM
//import com.samyak.smailtm.util.*
//import com.samyak.smailtm.callbacks.*
//import javax.security.auth.login.LoginException
//
///**
// * Example 1: Create a temporary email account with random address
// */
//fun example1_CreateRandomAccount() {
//    println("\n=== Example 1: Create Random Account ===")
//
//    try {
//        // Create account with random email address
//        val mailTM = SMailBuilder.createDefault("mySecurePassword123")
//        mailTM.init()
//
//        // Get account details
//        val account = mailTM.getSelf()
//        println("✅ Account created successfully!")
//        println("📧 Email: ${account.address}")
//        println("🆔 ID: ${account.id}")
//        println("💾 Quota: ${account.quota} bytes")
//        println("📊 Used: ${account.used} bytes")
//
//    } catch (e: LoginException) {
//        println("❌ Failed to create account: ${e.message}")
//    }
//}
//
///**
// * Example 2: Create account with specific email address
// */
//fun example2_CreateSpecificAccount() {
//    println("\n=== Example 2: Create Specific Account ===")
//
//    try {
//        // First, get available domains
//        val domains = Domains.fetchDomains()
//        val domain = domains.firstOrNull { it.isActive }?.domain ?: return
//
//        // Create account with custom username
//        val email = "myusername@$domain"
//        val password = "securePass456"
//
//        val mailTM = SMailBuilder.createAndLogin(email, password)
//        println("✅ Account created: ${mailTM.getSelf().address}")
//
//    } catch (e: LoginException) {
//        println("❌ Failed: ${e.message}")
//    }
//}
//
///**
// * Example 3: Login to existing account
// */
//fun example3_LoginExistingAccount() {
//    println("\n=== Example 3: Login to Existing Account ===")
//
//    try {
//        val email = "existing@mail.tm"
//        val password = "yourPassword"
//
//        val mailTM = SMailBuilder.login(email, password)
//        val account = mailTM.getSelf()
//
//        println("✅ Logged in successfully!")
//        println("📧 Email: ${account.address}")
//        println("📬 Total messages: ${mailTM.getTotalMessages()}")
//
//    } catch (e: LoginException) {
//        println("❌ Login failed: ${e.message}")
//    }
//}
//
///**
// * Example 4: Login with JWT token
// */
//fun example4_LoginWithToken() {
//    println("\n=== Example 4: Login with Token ===")
//
//    try {
//        val token = "your_jwt_token_here"
//        val mailTM = SMailBuilder.loginWithToken(token)
//
//        println("✅ Authenticated with token!")
//        println("📧 Email: ${mailTM.getSelf().address}")
//
//    } catch (e: LoginException) {
//        println("❌ Token authentication failed: ${e.message}")
//    }
//}
//
///**
// * Example 5: Fetch all messages
// */
//fun example5_FetchAllMessages(mailTM: SMailTM) {
//    println("\n=== Example 5: Fetch All Messages ===")
//
//    mailTM.fetchMessages(object : MessageFetchedCallback {
//        override fun onMessagesFetched(messages: List<Message>) {
//            println("📬 Received ${messages.size} messages\n")
//
//            messages.forEach { message ->
//                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
//                println("📧 From: ${message.from.name} <${message.from.address}>")
//                println("📝 Subject: ${message.subject}")
//                println("📄 Preview: ${message.intro}")
//                println("👁️ Seen: ${message.seen}")
//                println("📎 Has Attachments: ${message.hasAttachments}")
//                println("📅 Date: ${message.createdAt}")
//                println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
//            }
//        }
//
//        override fun onError(response: Response) {
//            println("❌ Error fetching messages: ${response.response}")
//        }
//    })
//}
//
///**
// * Example 6: Fetch limited number of messages
// */
//fun example6_FetchLimitedMessages(mailTM: SMailTM) {
//    println("\n=== Example 6: Fetch Limited Messages ===")
//
//    // Fetch only the latest 5 messages
//    mailTM.asyncFetchMessages(5, object : MessageFetchedCallback {
//        override fun onMessagesFetched(messages: List<Message>) {
//            println("📬 Fetched ${messages.size} latest messages")
//
//            messages.forEach { message ->
//                println("• ${message.subject} - from ${message.from.address}")
//            }
//        }
//
//        override fun onError(response: Response) {
//            println("❌ Error: ${response.response}")
//        }
//    })
//}
//
///**
// * Example 7: Get specific message by ID
// */
//fun example7_GetMessageById(mailTM: SMailTM, messageId: String) {
//    println("\n=== Example 7: Get Message by ID ===")
//
//    try {
//        val message = mailTM.getMessageById(messageId)
//
//        println("📧 Message Details:")
//        println("From: ${message.from.name} <${message.from.address}>")
//        println("Subject: ${message.subject}")
//        println("\n📄 Content:")
//        println(message.text)
//
//        // Get HTML content
//        if (message.html.isNotEmpty()) {
//            println("\n🌐 HTML Content available")
//        }
//
//    } catch (e: Exception) {
//        println("❌ Failed to get message: ${e.message}")
//    }
//}
//
///**
// * Example 8: Real-time message listener
// */
//fun example8_RealtimeListener(mailTM: SMailTM) {
//    println("\n=== Example 8: Real-time Message Listener ===")
//
//    mailTM.openEventListener(object : EventListener {
//        override fun onReady() {
//            println("✅ Event listener is ready!")
//            println("🔔 Waiting for new messages...")
//        }
//
//        override fun onMessageReceived(message: Message) {
//            println("\n📧 NEW MESSAGE RECEIVED!")
//            println("From: ${message.from.address}")
//            println("Subject: ${message.subject}")
//            println("Preview: ${message.intro}")
//
//            // Automatically mark as read
//            message.asyncMarkAsRead()
//        }
//
//        override fun onError(error: String) {
//            println("⚠️ Error: $error")
//        }
//
//        override fun onClose() {
//            println("🔌 Event listener closed")
//        }
//    })
//
//    // Remember to close when done
//    // mailTM.closeMessageListener()
//}
//
///**
// * Example 9: Mark message as read
// */
//fun example9_MarkAsRead(message: Message) {
//    println("\n=== Example 9: Mark Message as Read ===")
//
//    // Synchronous
//    val success = message.markAsRead()
//    println("Marked as read: $success")
//
//    // With callback
//    message.markAsRead { status ->
//        println("Mark as read status: $status")
//    }
//
//    // Asynchronous
//    message.asyncMarkAsRead()
//
//    // Async with callback
//    message.asyncMarkAsRead { status ->
//        println("Async mark as read: $status")
//    }
//}
//
///**
// * Example 10: Delete a message
// */
//fun example10_DeleteMessage(message: Message) {
//    println("\n=== Example 10: Delete Message ===")
//
//    // Synchronous delete
//    val deleted = message.delete()
//    if (deleted) {
//        println("✅ Message deleted successfully")
//    } else {
//        println("❌ Failed to delete message")
//    }
//
//    // Async delete with callback
//    message.asyncDelete { status ->
//        if (status) {
//            println("✅ Message deleted asynchronously")
//        }
//    }
//}
//
///**
// * Example 11: Working with attachments
// */
//fun example11_HandleAttachments(message: Message) {
//    println("\n=== Example 11: Handle Attachments ===")
//
//    if (message.hasAttachments) {
//        println("📎 Message has ${message.attachments.size} attachment(s)")
//
//        message.attachments.forEach { attachment ->
//            println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
//            println("📄 Filename: ${attachment.filename}")
//            println("📊 Size: ${attachment.size} bytes")
//            println("🏷️ Type: ${attachment.contentType}")
//            println("🆔 ID: ${attachment.id}")
//
//            // Save attachment to current directory
//            attachment.save()
//
//            // Save with custom filename
//            attachment.save("downloaded_${attachment.filename}")
//
//            // Save with callback
//            attachment.save(attachment.filename) { status ->
//                if (status) {
//                    println("✅ Downloaded: ${attachment.filename}")
//                } else {
//                    println("❌ Download failed")
//                }
//            }
//        }
//    } else {
//        println("No attachments in this message")
//    }
//}
//
///**
// * Example 12: Get account information
// */
//fun example12_GetAccountInfo(mailTM: SMailTM) {
//    println("\n=== Example 12: Account Information ===")
//
//    val account = mailTM.getSelf()
//
//    println("📧 Email: ${account.address}")
//    println("🆔 ID: ${account.id}")
//    println("💾 Quota: ${account.quota} bytes")
//    println("📊 Used: ${account.used} bytes")
//    println("🚫 Is Disabled: ${account.isDisabled}")
//    println("🗑️ Is Deleted: ${account.isDeleted}")
//    println("📅 Created: ${account.createdAt}")
//    println("🔄 Updated: ${account.updatedAt}")
//
//    // Get total messages
//    val totalMessages = mailTM.getTotalMessages()
//    println("📬 Total Messages: $totalMessages")
//}
//
///**
// * Example 13: Delete account
// */
//fun example13_DeleteAccount(mailTM: SMailTM) {
//    println("\n=== Example 13: Delete Account ===")
//
//    // Synchronous delete
//    val deleted = mailTM.delete()
//    if (deleted) {
//        println("✅ Account deleted successfully")
//    }
//
//    // Async delete with callback
//    mailTM.asyncDelete(object : WorkCallback {
//        override fun workStatus(status: Boolean) {
//            if (status) {
//                println("✅ Account deleted asynchronously")
//            } else {
//                println("❌ Failed to delete account")
//            }
//        }
//    })
//}
//
///**
// * Example 14: Working with domains
// */
//fun example14_ManageDomains() {
//    println("\n=== Example 14: Domain Management ===")
//
//    // Fetch all available domains
//    val domains = Domains.fetchDomains()
//    println("📋 Available domains: ${domains.size}\n")
//
//    domains.forEach { domain ->
//        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
//        println("🌐 Domain: ${domain.domain}")
//        println("🆔 ID: ${domain.id}")
//        println("✅ Active: ${domain.isActive}")
//        println("🔒 Private: ${domain.isPrivate}")
//        println("📅 Created: ${domain.createdAt}")
//    }
//
//    // Get a random domain
//    val randomDomain = Domains.getRandomDomain()
//    println("\n🎲 Random domain: ${randomDomain.domain}")
//
//    // Fetch specific domain by ID
//    try {
//        val specificDomain = Domains.fetchDomainById(domains.first().id)
//        println("🔍 Fetched domain: ${specificDomain.domain}")
//    } catch (e: Exception) {
//        println("❌ Failed to fetch domain: ${e.message}")
//    }
//}
//
///**
// * Example 15: Complete workflow - Create, receive, and process messages
// */
//fun example15_CompleteWorkflow() {
//    println("\n=== Example 15: Complete Workflow ===")
//
//    try {
//        // Step 1: Create account
//        println("Step 1: Creating account...")
//        val mailTM = SMailBuilder.createDefault("workflow123")
//        mailTM.init()
//        val account = mailTM.getSelf()
//        println("✅ Account created: ${account.address}")
//
//        // Step 2: Set up real-time listener
//        println("\nStep 2: Setting up message listener...")
//        mailTM.openEventListener(object : EventListener {
//            override fun onReady() {
//                println("✅ Listener ready - waiting for messages...")
//            }
//
//            override fun onMessageReceived(message: Message) {
//                println("\n📧 New message received!")
//                println("From: ${message.from.address}")
//                println("Subject: ${message.subject}")
//
//                // Process message
//                if (message.hasAttachments) {
//                    println("📎 Downloading attachments...")
//                    message.attachments.forEach { it.save() }
//                }
//
//                // Mark as read
//                message.asyncMarkAsRead()
//            }
//
//            override fun onError(error: String) {
//                println("⚠️ Error: $error")
//            }
//
//            override fun onClose() {
//                println("🔌 Listener closed")
//            }
//        })
//
//        // Step 3: Display account info
//        println("\nStep 3: Account ready for use")
//        println("📧 Send emails to: ${account.address}")
//        println("🔔 Listening for incoming messages...")
//
//        // Keep the program running to receive messages
//        println("\nPress Ctrl+C to stop...")
//        Thread.sleep(Long.MAX_VALUE)
//
//    } catch (e: Exception) {
//        println("❌ Workflow failed: ${e.message}")
//    }
//}
//
///**
// * Main function to run examples
// */
//fun main() {
//    println("╔════════════════════════════════════════════╗")
//    println("║   SMailTM Library - Usage Examples        ║")
//    println("║   Temporary Email Service for Kotlin      ║")
//    println("╚════════════════════════════════════════════╝")
//
//    // Run individual examples
//    example1_CreateRandomAccount()
//    example14_ManageDomains()
//
//    // For interactive examples, uncomment the one you want to test:
//    // example15_CompleteWorkflow()
//
//    println("\n✅ Examples completed!")
//    println("💡 Tip: Uncomment specific examples in main() to test them")
//}
