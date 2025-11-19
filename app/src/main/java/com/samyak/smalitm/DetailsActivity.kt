package com.samyak.smalitm

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.samyak.smalitm.SMaliTM
import com.samyak.smalitm.callbacks.MessageFetchedCallback
import com.samyak.smalitm.util.Message
import com.samyak.smalitm.util.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvSenderEmail: TextView
    private lateinit var tvSubject: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvAvatar: TextView
    private lateinit var tvContent: TextView
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    
    private var messageId: String = ""
    private var hasHtml: Boolean = false
    private var mailTM: SMaliTM? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        
        setupStatusBar()
        initViews()
        setupListeners()
        loadMessageData()
    }
    
    private fun setupStatusBar() {
        // Set status bar color to match dark theme
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.colorTheme)
        window.navigationBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.colorTheme)
        
        // Set status bar icons to light color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0,
                android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and 
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
    
    companion object {
        var sharedMailTM: SMaliTM? = null
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvSenderEmail = findViewById(R.id.tvSenderEmail)
        tvSubject = findViewById(R.id.tvSubject)
        tvTime = findViewById(R.id.tvTime)
        tvAvatar = findViewById(R.id.tvAvatar)
        tvContent = findViewById(R.id.tvContent)
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        
        // Configure WebView
        webView.settings.apply {
            javaScriptEnabled = false
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = false
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadMessageData() {
        messageId = intent.getStringExtra("MESSAGE_ID") ?: ""
        val sender = intent.getStringExtra("SENDER") ?: ""
        val senderName = intent.getStringExtra("SENDER_NAME") ?: ""
        val subject = intent.getStringExtra("SUBJECT") ?: "(No Subject)"
        val content = intent.getStringExtra("CONTENT") ?: ""
        val timestamp = intent.getStringExtra("TIMESTAMP") ?: ""
        val htmlContent = intent.getStringExtra("HTML_CONTENT") ?: ""
        hasHtml = intent.getBooleanExtra("HAS_HTML", false)

        // Display sender with name if available
        val displayName = if (senderName.isNotEmpty() && senderName != sender) {
            senderName
        } else {
            sender.substringBefore("@")
        }
        
        tvSenderEmail.text = sender
        tvSubject.text = subject
        tvTime.text = formatDateTimeFromTimestamp(timestamp)
        
        // Set avatar initial
        tvAvatar.text = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        tvAvatar.background = resources.getDrawable(R.drawable.avatar_circle, null)
        
        mailTM = sharedMailTM
        
        // If HTML content was passed directly, display it
        if (htmlContent.isNotEmpty()) {
            displayHtml(htmlContent)
            markMessageAsRead()
        } else if (hasHtml && messageId.isNotEmpty() && mailTM != null) {
            // Fetch full message with HTML content
            fetchFullMessage()
        } else {
            // Display plain text content
            displayPlainText(content)
        }
    }
    
    private fun formatDateTimeFromTimestamp(timestamp: String): String {
        if (timestamp.isEmpty()) return "Unknown time"
        
        return try {
            // Use SMaliTM's Utility class to parse the timestamp
            val zonedDateTime = com.samyak.smalitm.util.Utility.parseToDefaultTimeZone(
                timestamp, 
                "yyyy-MM-dd'T'HH:mm:ss'+00:00'"
            )
            
            // Convert to Date for formatting
            val date = java.util.Date.from(zonedDateTime.toInstant())
            val now = java.util.Date()
            val diff = now.time - date.time
            
            // Format based on time difference
            when {
                diff < 60000 -> "Just now"
                diff < 3600000 -> "${diff / 60000} minutes ago"
                diff < 86400000 -> "${diff / 3600000} hours ago"
                diff < 172800000 -> "Yesterday at ${formatTime(date)}"
                diff < 604800000 -> "${diff / 86400000} days ago"
                else -> formatFullDate(date)
            }
        } catch (e: Exception) {
            // Fallback to simple date parsing if SMaliTM utility fails
            try {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", java.util.Locale.getDefault())
                val date = sdf.parse(timestamp)
                if (date != null) {
                    formatFullDate(date)
                } else {
                    "Unknown time"
                }
            } catch (e2: Exception) {
                "Unknown time"
            }
        }
    }
    
    private fun formatTime(date: java.util.Date): String {
        val timeFormat = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
        return timeFormat.format(date)
    }
    
    private fun formatFullDate(date: java.util.Date): String {
        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", java.util.Locale.getDefault())
        return dateFormat.format(date)
    }
    
    private fun markMessageAsRead() {
        if (messageId.isEmpty() || mailTM == null) return
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                mailTM?.fetchMessages(object : MessageFetchedCallback {
                    override fun onMessagesFetched(messages: List<Message>) {
                        val message = messages.find { it.id == messageId }
                        message?.asyncMarkAsRead()
                    }
                    override fun onError(error: Response) {
                        // Ignore
                    }
                })
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
    
    private fun fetchFullMessage() {
        progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    mailTM?.fetchMessages(object : MessageFetchedCallback {
                        override fun onMessagesFetched(messages: List<Message>) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                val message = messages.find { it.id == messageId }
                                if (message != null) {
                                    displayMessage(message)
                                } else {
                                    displayPlainText("Message not found")
                                }
                                progressBar.visibility = View.GONE
                            }
                        }

                        override fun onError(error: Response) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                displayPlainText("Error loading message: ${error.responseCode}")
                                progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@DetailsActivity,
                                    "Failed to load message",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
                } catch (e: Exception) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        displayPlainText("Error: ${e.message}")
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }
    
    private fun displayMessage(message: Message) {
        // Update UI with message details using SMaliTM properties
        val displayName = if (message.senderName.isNotEmpty() && message.senderName != message.senderAddress) {
            message.senderName
        } else {
            message.senderAddress.substringBefore("@")
        }
        
        tvSenderEmail.text = message.senderAddress
        tvSubject.text = message.subject.ifEmpty { "(No Subject)" }
        
        // Set avatar initial
        tvAvatar.text = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        tvAvatar.background = resources.getDrawable(R.drawable.avatar_circle, null)
        
        // Use SMaliTM's built-in date/time method
        try {
            val zonedDateTime = message.getCreatedDateTime()
            val date = java.util.Date.from(zonedDateTime.toInstant())
            val now = java.util.Date()
            val diff = now.time - date.time
            
            tvTime.text = when {
                diff < 60000 -> "Just now"
                diff < 3600000 -> "${diff / 60000} minutes ago"
                diff < 86400000 -> "${diff / 3600000} hours ago"
                diff < 172800000 -> "Yesterday at ${formatTime(date)}"
                diff < 604800000 -> "${diff / 86400000} days ago"
                else -> formatFullDate(date)
            }
        } catch (e: Exception) {
            tvTime.text = "Unknown time"
        }
        
        // Mark as read
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                message.markAsRead()
            } catch (e: Exception) {
                // Ignore errors
            }
        }
        
        // Display HTML content if available
        if (message.html.isNotEmpty()) {
            val htmlContent = message.html.joinToString("")
            displayHtml(htmlContent)
        } else {
            // Fallback to plain text
            displayPlainText(message.text.ifEmpty { "(No Content)" })
        }
    }
    
    private fun displayHtml(html: String) {
        tvContent.visibility = View.GONE
        webView.visibility = View.VISIBLE
        
        val styledHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        font-size: 16px;
                        line-height: 1.6;
                        color: #1A1A1A;
                        padding: 16px;
                        margin: 0;
                    }
                    img {
                        max-width: 100%;
                        height: auto;
                    }
                    a {
                        color: #4361EE;
                        text-decoration: none;
                    }
                    pre {
                        background: #F5F5F5;
                        padding: 12px;
                        border-radius: 8px;
                        overflow-x: auto;
                    }
                    code {
                        background: #F5F5F5;
                        padding: 2px 6px;
                        border-radius: 4px;
                    }
                </style>
            </head>
            <body>
                $html
            </body>
            </html>
        """.trimIndent()
        
        webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
    }
    
    private fun displayPlainText(text: String) {
        tvContent.visibility = View.VISIBLE
        webView.visibility = View.GONE
        tvContent.text = text
    }
}
