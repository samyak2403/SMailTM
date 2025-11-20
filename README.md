# SMailTM - Kotlin Mail.tm API Wrapper

[![JitPack](https://jitpack.io/v/samyak2403/SMailTM.svg)](https://jitpack.io/#yourusername/SMailTM)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org/)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Mail.tm](https://img.shields.io/badge/Mail.tm-API-red.svg)](https://mail.tm)

A powerful and easy-to-use Kotlin library for Android that provides a complete interface to interact with the [Mail.tm](https://mail.tm) temporary email service API. This library is a Kotlin conversion of the popular [JMailTM](https://github.com/shivam1608/JMailTM) library, optimized for modern Android development with Kotlin Coroutines support.

## 📱 Apps Using SMailTM

### TempBox Lite - Disposable Email Shield

<a href="https://play.google.com/store/apps/details?id=com.samyak.tempboxlite">
  <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="80"/>
</a>

**TempBox Lite** is a lightweight, ad-free temporary email app built with SMailTM library. Generate disposable email addresses instantly to protect your privacy and keep your inbox spam-free.

**Key Features:**
- 🚀 Instant temporary email generation
- 📬 Real-time email receiving with attachments
- 🔒 Complete privacy & anonymity (no registration required)
- 🪶 Lightweight & fast performance
- 📋 Easy copy & QR code sharing
- 🔄 Auto-refresh inbox
- 🧹 Self-cleaning inboxes for security

Perfect for sign-ups, verifications, and protecting your personal email from spam!

[**Download TempBox Lite on Google Play**](https://play.google.com/store/apps/details?id=com.samyak.tempboxlite)

---

## 📋 Table of Contents

- [Apps Using SMailTM](#-apps-using-smailtm)
- [Features](#-features)
- [Installation](#-installation)
- [Quick Start](#-quick-start)
- [Usage](#-usage)
  - [Creating an Account](#creating-an-account)
  - [Login](#login-to-existing-account)
  - [Fetching Messages](#fetching-messages)
  - [Event Listener](#real-time-event-listener)
  - [Message Operations](#message-operations)
  - [Attachments](#working-with-attachments)
  - [Account Management](#account-management)
  - [Domain Management](#domain-management)
- [API Reference](#-api-reference)
- [Advanced Usage](#-advanced-usage)
- [Best Practices](#-best-practices)
- [Contributing](#-contributing)
- [License](#-license)
- [Credits](#-credits)

## ✨ Features

- 📧 **Account Management** - Create, login, and delete temporary email accounts
- 📨 **Message Operations** - Fetch, read, mark as seen, and delete messages
- 🔔 **Real-time Notifications** - Server-Sent Events (SSE) for instant message updates
- 📎 **Attachment Support** - Download and save email attachments
- 🌐 **Domain Management** - List and select from available email domains
- 🔐 **Token Authentication** - Login with JWT tokens for persistent sessions
- ⚡ **Async Operations** - Built-in asynchronous methods for better performance
- 🎯 **Kotlin-First** - Designed for Kotlin with coroutines-friendly callbacks
- 🛡️ **Type-Safe** - Full Kotlin type safety with data classes
- 📱 **Android Optimized** - Lightweight and efficient for mobile apps


## 📦 Installation

### JitPack (Recommended)

#### Step 1: Add JitPack repository

Add the JitPack repository to your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Or in your root `build.gradle.kts`:

```kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

#### Step 2: Add the dependency

Add SMailTM to your app's `build.gradle.kts`:

```kotlin
dependencies {
      implementation 'com.github.samyak2403:SMailTM:SMailTM-v1.0.2'

}
```

**Latest Version:** [![JitPack](https://jitpack.io/v/samyak2403/SMailTM.svg)](https://jitpack.io/#yourusername/SMailTM)

### Gradle (Local Module)

If you want to include the library as a local module:

```kotlin
// settings.gradle.kts
include(":SMailTM")

// app/build.gradle.kts
dependencies {
    implementation(project(":SMailTM"))
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.yourusername</groupId>
    <artifactId>SMailTM</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Requirements

- **Min SDK:** 24 (Android 7.0)
- **Kotlin:** 1.9.0 or higher
- **Internet Permission:** Required in AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 🚀 Quick Start

### Basic Example

```kotlin
import com.samyak.SMailTM.util.SMaliBuilder
import com.samyak.SMailTM.callbacks.MessageFetchedCallback
import javax.security.auth.login.LoginException

// Create a temporary email account
val mailTM = SMaliBuilder.createDefault("password123")
mailTM.init()

// Get account info
val account = mailTM.getSelf()
println("Your temporary email: ${account.email}")

// Fetch messages
mailTM.fetchMessages(object : MessageFetchedCallback {
    override fun onMessagesFetched(messages: List<Message>) {
        messages.forEach { message ->
            println("From: ${message.senderAddress}")
            println("Subject: ${message.subject}")
        }
    }
    
    override fun onError(error: Response) {
        println("Error: ${error.responseCode}")
    }
})
```


## 📖 Usage

### Creating an Account

#### Create with Random Email

```kotlin
import com.samyak.SMailTM.util.SMaliBuilder
import javax.security.auth.login.LoginException

try {
    // Create a random account
    val mailTM = SMaliBuilder.createDefault("your_password")
    mailTM.init()
    
    val account = mailTM.getSelf()
    println("Account created: ${account.email}")
    println("Account ID: ${account.id}")
    println("Quota: ${account.quota} bytes")
} catch (e: LoginException) {
    println("Failed to create account: ${e.message}")
}
```

#### Create with Specific Email

```kotlin
try {
    val email = "myemail@example.com"
    val mailTM = SMaliBuilder.createAndLogin(email, "password")
    
    println("Account created: ${mailTM.getSelf().email}")
} catch (e: LoginException) {
    println("Failed to create account: ${e.message}")
}
```

### Login to Existing Account

#### Login with Email and Password

```kotlin
try {
    val mailTM = SMaliBuilder.login("email@domain.com", "password")
    val account = mailTM.getSelf()
    
    println("Logged in as: ${account.email}")
    println("Used storage: ${account.used} bytes")
} catch (e: LoginException) {
    println("Login failed: ${e.message}")
}
```

#### Login with Token

```kotlin
try {
    val mailTM = SMaliBuilder.loginWithToken("your_jwt_token")
    println("Logged in with token")
} catch (e: LoginException) {
    println("Token login failed: ${e.message}")
}
```


### Fetching Messages

#### Fetch All Messages

```kotlin
import com.samyak.SMailTM.callbacks.MessageFetchedCallback
import com.samyak.SMailTM.util.Message
import com.samyak.SMailTM.util.Response

mailTM.fetchMessages(object : MessageFetchedCallback {
    override fun onMessagesFetched(messages: List<Message>) {
        messages.forEach { message ->
            println("From: ${message.senderAddress}")
            println("Subject: ${message.subject}")
            println("Content: ${message.content}")
            println("Seen: ${message.seen}")
            println("Has Attachments: ${message.hasAttachments}")
        }
    }
    
    override fun onError(error: Response) {
        println("Error: ${error.response}")
    }
})
```

#### Fetch Limited Messages

```kotlin
// Fetch only the latest 10 messages
mailTM.fetchMessages(10, object : MessageFetchedCallback {
    override fun onMessagesFetched(messages: List<Message>) {
        println("Fetched ${messages.size} messages")
    }
    
    override fun onError(error: Response) {
        println("Error: ${error.response}")
    }
})
```

#### Async Message Fetching

```kotlin
mailTM.asyncFetchMessages(object : MessageFetchedCallback {
    override fun onMessagesFetched(messages: List<Message>) {
        println("Messages fetched asynchronously")
    }
    
    override fun onError(error: Response) {
        println("Error: ${error.response}")
    }
})
```

#### Get Specific Message

```kotlin
val message = mailTM.getMessageById("message_id")
println("Subject: ${message.subject}")
println("From: ${message.senderName} <${message.senderAddress}>")
```


### Real-time Event Listener

```kotlin
import com.samyak.SMailTM.callbacks.EventListener
import com.samyak.SMailTM.util.Account

mailTM.openEventListener(object : EventListener {
    override fun onReady() {
        println("Event listener is ready")
    }
    
    override fun onMessageReceived(message: Message) {
        println("📧 New message from: ${message.senderAddress}")
        println("Subject: ${message.subject}")
        
        // Auto-mark as read
        message.asyncMarkAsRead()
    }
    
    override fun onMessageSeen(message: Message) {
        println("👁️ Message marked as seen: ${message.id}")
    }
    
    override fun onMessageDelete(id: String) {
        println("🗑️ Message deleted: $id")
    }
    
    override fun onAccountUpdate(account: Account) {
        println("🔄 Account updated")
    }
    
    override fun onAccountDelete(account: Account) {
        println("❌ Account deleted")
    }
    
    override fun onError(error: String) {
        println("⚠️ Error: $error")
    }
})

// Close listener when done
mailTM.closeMessageListener()
```

#### Custom Retry Interval

```kotlin
// Retry connection every 5 seconds on failure
mailTM.openEventListener(eventListener, retryInterval = 5000L)
```


### Message Operations

#### Mark as Read

```kotlin
// Synchronous
val success = message.markAsRead()
println("Marked as read: $success")

// With callback
message.markAsRead { status ->
    println("Mark as read status: $status")
}

// Asynchronous
message.asyncMarkAsRead()

// Async with callback
message.asyncMarkAsRead { status ->
    println("Async mark as read: $status")
}
```

#### Delete Message

```kotlin
// Synchronous
val deleted = message.delete()
println("Message deleted: $deleted")

// With callback
message.delete { status ->
    println("Delete status: $status")
}

// Asynchronous
message.asyncDelete()

// Async with callback
message.asyncDelete { status ->
    println("Async delete: $status")
}
```

#### Get Message Details

```kotlin
// Get receivers
val receivers = message.getReceivers()
receivers.forEach { receiver ->
    println("To: ${receiver.address}")
}

// Get HTML content
val htmlContent = message.getRawHTML()
println("HTML: $htmlContent")

// Get raw JSON
val jsonData = message.getRawJson()
println("JSON: $jsonData")

// Get date/time
val createdDateTime = message.getCreatedDateTime()
println("Created: $createdDateTime")
```


### Working with Attachments

```kotlin
val message = mailTM.getMessageById("message_id")

if (message.hasAttachments) {
    message.attachments.forEach { attachment ->
        println("Filename: ${attachment.filename}")
        println("Size: ${attachment.size} bytes")
        println("Type: ${attachment.contentType}")
        println("ID: ${attachment.id}")
        
        // Save to current directory
        attachment.save()
        
        // Save with custom filename
        attachment.save("custom_name.pdf")
        
        // Save with callback
        attachment.save("file.pdf") { status ->
            if (status) {
                println("✅ Downloaded: ${attachment.filename}")
            } else {
                println("❌ Download failed")
            }
        }
        
        // Save to custom path
        attachment.save("/path/to/directory", "filename.pdf") { status ->
            println("Save status: $status")
        }
    }
}
```

### Account Management

#### Get Account Information

```kotlin
val account = mailTM.getSelf()
println("Email: ${account.email}")
println("ID: ${account.id}")
println("Quota: ${account.quota} bytes")
println("Used: ${account.used} bytes")
println("Is Disabled: ${account.isDisabled}")
println("Is Deleted: ${account.isDeleted}")
println("Created At: ${account.createdAt}")
println("Updated At: ${account.updatedAt}")
```

#### Delete Account

```kotlin
// Synchronous
val deleted = mailTM.delete()
println("Account deleted: $deleted")

// With callback
mailTM.delete { status ->
    println("Delete status: $status")
}

// Asynchronous
mailTM.asyncDelete()

// Async with callback
mailTM.asyncDelete { status ->
    println("Async delete: $status")
}
```

#### Get Total Messages

```kotlin
val totalMessages = mailTM.getTotalMessages()
println("Total messages: $totalMessages")
```


### Domain Management

```kotlin
import com.samyak.SMailTM.util.Domains

// Fetch all available domains
val domains = Domains.fetchDomains()
domains.forEach { domain ->
    println("Domain: ${domain.domainName}")
    println("Active: ${domain.isActive}")
    println("Private: ${domain.isPrivate}")
}

// Get a random domain
val randomDomain = Domains.getRandomDomain()
println("Random domain: ${randomDomain.domainName}")

// Fetch domain by ID
val domain = Domains.fetchDomainById("domain_id")
println("Domain: ${domain.domainName}")

// Create account with specific domain
val email = "myname@${randomDomain.domainName}"
val mailTM = SMaliBuilder.createAndLogin(email, "password")
```

## 🔧 Advanced Usage

### Using with Kotlin Coroutines

```kotlin
import kotlinx.coroutines.*

lifecycleScope.launch {
    try {
        // Create account on IO thread
        val mail = withContext(Dispatchers.IO) {
            SMaliBuilder.createDefault("password")
        }
        mail.init()
        
        // Get account info
        val account = withContext(Dispatchers.IO) {
            mail.getSelf()
        }
        
        println("Email: ${account.email}")
        
        // Fetch messages
        withContext(Dispatchers.IO) {
            mail.fetchMessages(object : MessageFetchedCallback {
                override fun onMessagesFetched(messages: List<Message>) {
                    // Update UI on Main thread
                    launch(Dispatchers.Main) {
                        println("Received ${messages.size} messages")
                    }
                }
                
                override fun onError(error: Response) {
                    println("Error: ${error.responseCode}")
                }
            })
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}
```


### Persistent Sessions with Tokens

```kotlin
// First login - save token
val mailTM = SMaliBuilder.login("email@domain.com", "password")
val account = mailTM.getSelf()
val token = account.token

// Save token to SharedPreferences
val prefs = context.getSharedPreferences("mail_prefs", Context.MODE_PRIVATE)
prefs.edit().putString("auth_token", token).apply()

// Later - login with saved token
val savedToken = prefs.getString("auth_token", null)
if (savedToken != null) {
    val mailTM = SMaliBuilder.loginWithToken(savedToken)
    println("Logged in with saved token")
}
```

### Custom Domain Selection

```kotlin
import com.samyak.SMailTM.util.Domains

// Get available domains
val domains = Domains.fetchDomains()

// Filter active domains
val activeDomains = domains.filter { it.isActive }

// Select preferred domain
val preferredDomain = activeDomains.firstOrNull { 
    it.domainName.contains("mail.tm") 
} ?: activeDomains.first()

// Create account with selected domain
val email = "myusername@${preferredDomain.domainName}"
val mailTM = SMaliBuilder.createAndLogin(email, "password")
```

### Handling Multiple Accounts

```kotlin
class EmailManager {
    private val accounts = mutableMapOf<String, SMailTM>()
    
    fun addAccount(email: String, password: String) {
        try {
            val mailTM = SMaliBuilder.login(email, password)
            accounts[email] = mailTM
            println("Added account: $email")
        } catch (e: LoginException) {
            println("Failed to add account: ${e.message}")
        }
    }
    
    fun getAccount(email: String): SMailTM? {
        return accounts[email]
    }
    
    fun removeAccount(email: String) {
        accounts[email]?.delete()
        accounts.remove(email)
    }
    
    fun getAllAccounts(): List<String> {
        return accounts.keys.toList()
    }
}
```


## 📚 API Reference

### SMaliBuilder

Factory class for creating and managing SMailTM instances.

| Method | Parameters | Returns | Description |
|--------|------------|---------|-------------|
| `login()` | email: String, password: String | SMailTM | Login to existing account |
| `create()` | email: String, password: String | Boolean | Create new account |
| `createAndLogin()` | email: String, password: String | SMailTM | Create and login in one step |
| `createDefault()` | password: String | SMailTM | Create account with random email |
| `loginWithToken()` | token: String | SMailTM | Login using JWT token |

### SMailTM

Main class for interacting with Mail.tm API.

#### Account Methods

| Method | Returns | Description |
|--------|---------|-------------|
| `init()` | void | Initialize the account |
| `getSelf()` | Account | Get current account info |
| `delete()` | Boolean | Delete account (sync) |
| `delete(callback)` | void | Delete account with callback |
| `asyncDelete()` | void | Delete account (async) |
| `asyncDelete(callback)` | void | Delete account async with callback |
| `getAccountById(id)` | Account | Get account by ID |

#### Message Methods

| Method | Returns | Description |
|--------|---------|-------------|
| `getTotalMessages()` | Int | Get total message count |
| `getMessageById(id)` | Message | Get specific message |
| `fetchMessages(callback)` | void | Fetch all messages |
| `fetchMessages(limit, callback)` | void | Fetch limited messages |
| `asyncFetchMessages(callback)` | void | Fetch messages async |
| `asyncFetchMessages(limit, callback)` | void | Fetch limited messages async |

#### Event Listener Methods

| Method | Returns | Description |
|--------|---------|-------------|
| `openEventListener(listener)` | void | Start SSE listener |
| `openEventListener(listener, retry)` | void | Start with custom retry interval |
| `closeMessageListener()` | void | Stop SSE listener |


### Message

Represents an email message.

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `id` | String | Message ID |
| `msgid` | String | Message identifier |
| `from` | Sender | Sender information |
| `to` | Any? | Recipient(s) |
| `subject` | String | Email subject |
| `text` | String | Plain text content |
| `html` | List<String> | HTML content |
| `seen` | Boolean | Read status |
| `flagged` | Boolean | Flagged status |
| `isDeleted` | Boolean | Deletion status |
| `hasAttachments` | Boolean | Has attachments |
| `attachments` | List<Attachment> | Attachment list |
| `size` | Long | Message size in bytes |
| `createdAt` | String | Creation timestamp |
| `updatedAt` | String | Update timestamp |
| `senderAddress` | String | Sender email (computed) |
| `senderName` | String | Sender name (computed) |
| `content` | String | Message content (computed) |

#### Methods

| Method | Returns | Description |
|--------|---------|-------------|
| `delete()` | Boolean | Delete message (sync) |
| `delete(callback)` | void | Delete with callback |
| `asyncDelete()` | void | Delete async |
| `asyncDelete(callback)` | void | Delete async with callback |
| `markAsRead()` | Boolean | Mark as read (sync) |
| `markAsRead(callback)` | void | Mark as read with callback |
| `asyncMarkAsRead()` | void | Mark as read async |
| `asyncMarkAsRead(callback)` | void | Mark as read async with callback |
| `getReceivers()` | List<Receiver> | Get recipient list |
| `getRawHTML()` | String | Get raw HTML |
| `getRawJson()` | String | Get raw JSON |
| `getCreatedDateTime()` | ZonedDateTime | Get creation date |
| `getUpdatedDateTime()` | ZonedDateTime | Get update date |


### Attachment

Represents an email attachment.

| Property | Type | Description |
|----------|------|-------------|
| `id` | String | Attachment ID |
| `filename` | String | File name |
| `contentType` | String | MIME type |
| `size` | Long | File size in bytes |
| `downloadUrl` | String | Download URL |

| Method | Returns | Description |
|--------|---------|-------------|
| `saveSync()` | Boolean | Save to current directory |
| `saveSync(filename)` | Boolean | Save with custom name |
| `saveSync(path, filename)` | Boolean | Save to custom path |
| `save()` | void | Save async |
| `save(callback)` | void | Save with callback |
| `save(filename)` | void | Save with custom name async |
| `save(filename, callback)` | void | Save with callback |
| `save(path, filename, callback)` | void | Save to custom path with callback |

### Domains

Utility class for domain management.

| Method | Returns | Description |
|--------|---------|-------------|
| `fetchDomains()` | List<Domain> | Get all available domains |
| `getRandomDomain()` | Domain | Get random domain |
| `fetchDomainById(id)` | Domain | Get specific domain |

### Callbacks

#### EventListener

```kotlin
interface EventListener {
    fun onReady()
    fun onMessageReceived(message: Message)
    fun onMessageSeen(message: Message)
    fun onMessageDelete(id: String)
    fun onAccountUpdate(account: Account)
    fun onAccountDelete(account: Account)
    fun onError(error: String)
}
```

#### MessageFetchedCallback

```kotlin
interface MessageFetchedCallback {
    fun onMessagesFetched(messages: List<Message>)
    fun onError(error: Response)
}
```

#### WorkCallback

```kotlin
interface WorkCallback {
    fun workStatus(status: Boolean)
}
```


## 🐛 Error Handling

### Exception Handling

```kotlin
import javax.security.auth.login.LoginException

try {
    val mailTM = SMaliBuilder.login("email@domain.com", "password")
} catch (e: LoginException) {
    when {
        e.message?.contains("401") == true -> {
            println("❌ Invalid credentials")
        }
        e.message?.contains("404") == true -> {
            println("❌ Account not found")
        }
        e.message?.contains("429") == true -> {
            println("⚠️ Too many requests - rate limited")
        }
        else -> {
            println("❌ Login failed: ${e.message}")
        }
    }
}
```

### Response Error Handling

```kotlin
mailTM.fetchMessages(object : MessageFetchedCallback {
    override fun onMessagesFetched(messages: List<Message>) {
        println("✅ Fetched ${messages.size} messages")
    }
    
    override fun onError(error: Response) {
        when (error.responseCode) {
            401 -> println("❌ Unauthorized - token expired")
            404 -> println("❌ Not found")
            429 -> println("⚠️ Rate limit exceeded")
            500 -> println("❌ Server error")
            503 -> println("⚠️ Service unavailable")
            else -> println("❌ Error ${error.responseCode}: ${error.response}")
        }
    }
})
```

### Network Error Handling

```kotlin
try {
    mailTM.fetchMessages(callback)
} catch (e: java.net.UnknownHostException) {
    println("❌ No internet connection")
} catch (e: java.net.SocketTimeoutException) {
    println("⚠️ Connection timeout")
} catch (e: Exception) {
    println("❌ Unexpected error: ${e.message}")
}
```


## 📝 Best Practices

### 1. Always Close Event Listeners

```kotlin
class MainActivity : AppCompatActivity() {
    private var mailTM: SMailTM? = null
    
    override fun onDestroy() {
        super.onDestroy()
        mailTM?.closeMessageListener()
    }
}
```

### 2. Use Async Methods for Better Performance

```kotlin
// ✅ Good - Non-blocking
mailTM.asyncFetchMessages(callback)
message.asyncMarkAsRead()
message.asyncDelete()

// ❌ Avoid - Blocking
mailTM.fetchMessages(callback) // Blocks current thread
message.markAsRead() // Blocks current thread
```

### 3. Store Tokens for Persistent Sessions

```kotlin
// Save token after login
val token = mailTM.getSelf().token
sharedPreferences.edit().putString("auth_token", token).apply()

// Reuse token on app restart
val savedToken = sharedPreferences.getString("auth_token", null)
if (savedToken != null) {
    mailTM = SMaliBuilder.loginWithToken(savedToken)
}
```

### 4. Handle Network Operations on Background Thread

```kotlin
lifecycleScope.launch(Dispatchers.IO) {
    try {
        val mail = SMaliBuilder.createDefault("password")
        mail.init()
        
        withContext(Dispatchers.Main) {
            // Update UI
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            // Show error
        }
    }
}
```

### 5. Implement Proper Error Handling

```kotlin
mailTM.fetchMessages(object : MessageFetchedCallback {
    override fun onMessagesFetched(messages: List<Message>) {
        // Success
    }
    
    override fun onError(error: Response) {
        // Always handle errors
        when (error.responseCode) {
            401 -> refreshToken()
            429 -> scheduleRetry()
            else -> showError(error.response)
        }
    }
})
```

### 6. Clean Up Resources

```kotlin
// Delete account when no longer needed
mailTM.asyncDelete { success ->
    if (success) {
        println("✅ Account cleaned up")
    }
}
```


## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### How to Contribute

1. **Fork the repository**
   ```bash
   git clone https://github.com/samyak2403/SMailTM.git
   ```

2. **Create your feature branch**
   ```bash
   git checkout -b feature/AmazingFeature
   ```

3. **Commit your changes**
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```

4. **Push to the branch**
   ```bash
   git push origin feature/AmazingFeature
   ```

5. **Open a Pull Request**

### Development Setup

```bash
# Clone the repository
git clone https://github.com/samyak2403/SMailTM.git
cd SMailTM

# Open in Android Studio
# Build the project
./gradlew build

# Run tests
./gradlew test
```

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Write unit tests for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Samyak

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```


## 🙏 Credits

This library is a Kotlin conversion of [JMailTM](https://github.com/shivam1608/JMailTM) created by [shivzee](https://github.com/shivam1608).

### Original Java Library
- **Repository:** https://github.com/shivam1608/JMailTM
- **Author:** [shivzee](https://github.com/shivam1608)
- **License:** MIT

### Mail.tm Service
- **Website:** https://mail.tm
- **API Documentation:** https://docs.mail.tm
- **Service Provider:** Mail.tm Team

### Special Thanks
- **JMailTM Contributors** - For the original Java implementation
- **Mail.tm Team** - For providing the temporary email service
- **Kotlin Community** - For the amazing language and ecosystem

## 📧 Support

For issues, questions, or contributions, please visit:

- **GitHub Issues:** [Report a bug](https://github.com/samyak2403/SMailTM/issues)
- **GitHub Discussions:** [Ask a question](https://github.com/samyak2403/SMailTM/discussions)
- **Email:** arrowwould@gmail.com

## 🔗 Links

- **Mail.tm API Documentation:** https://docs.mail.tm/
- **JMailTM Original Library:** https://github.com/shivam1608/JMailTM
- **Kotlin Documentation:** https://kotlinlang.org/docs/home.html
- **Android Developers:** https://developer.android.com/

## 📊 Stats

- **Language:** Kotlin 100%
- **Min SDK:** 24 (Android 7.0)
- **License:** MIT
- **Status:** Active Development

## 🗺️ Roadmap

- [ ] Add support for custom SMTP servers
- [ ] Implement message filtering and search
- [ ] Add support for message forwarding
- [ ] Implement email templates
- [ ] Add support for scheduled messages
- [ ] Improve error handling and retry logic
- [ ] Add comprehensive unit tests
- [ ] Publish to Maven Central
- [ ] Add support for multiple languages
- [ ] Implement message encryption

---

**Made with ❤️ in Kotlin**

**Star ⭐ this repository if you find it helpful!**




**⭐ Star this repository if you find it helpful!**

**🐛 Found a bug? [Report it here](https://github.com/samyak2403/SMailTM/issues)**

**💡 Have a feature request? [Let us know](https://github.com/samyak2403/SMailTM/discussions)**
#






