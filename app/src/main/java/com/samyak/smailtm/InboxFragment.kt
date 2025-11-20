package com.samyak.smailtm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.samyak.smailtm.callbacks.MessageFetchedCallback
import com.samyak.smailtm.util.Message
import com.samyak.smailtm.util.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class InboxFragment : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var recyclerMessages: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var tvMessageCount: TextView
    private lateinit var btnRefresh: ImageButton
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inbox, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerMessages = view.findViewById(R.id.recyclerMessages)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        tvMessageCount = view.findViewById(R.id.tvMessageCount)
        btnRefresh = view.findViewById(R.id.btnRefresh)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }
    
    private fun observeViewModel() {
        viewModel.mailTM.observe(viewLifecycleOwner) { mail ->
            if (mail != null) {
                fetchMessages()
            } else {
                updateUI(emptyList())
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = MessageAdapter()
        adapter.setOnItemClickListener { message ->
            openMessageDetails(message)
        }
        recyclerMessages.layoutManager = LinearLayoutManager(requireContext())
        recyclerMessages.adapter = adapter
    }
    
    private fun openMessageDetails(message: Message) {
        // Set shared mailTM for DetailsActivity
        DetailsActivity.sharedMailTM = viewModel.mailTM.value
        
        val intent = Intent(requireContext(), DetailsActivity::class.java).apply {
            putExtra("MESSAGE_ID", message.id)
            putExtra("SENDER", message.senderAddress)
            putExtra("SENDER_NAME", message.senderName)
            putExtra("SUBJECT", message.subject)
            putExtra("CONTENT", message.content)
            putExtra("TIMESTAMP", message.createdAt)
            putExtra("HAS_HTML", message.html.isNotEmpty())
            putExtra("IS_SEEN", message.seen)
            // Pass HTML content directly if available
            if (message.html.isNotEmpty()) {
                putExtra("HTML_CONTENT", message.html.joinToString(""))
            }
        }
        startActivity(intent)
    }
    
    private fun formatTime(timestamp: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'", Locale.getDefault())
            val date = sdf.parse(timestamp)
            val now = Date()
            val diff = now.time - (date?.time ?: 0)
            
            when {
                diff < 60000 -> "Just now"
                diff < 3600000 -> "${diff / 60000}m ago"
                diff < 86400000 -> "${diff / 3600000}h ago"
                else -> "${diff / 86400000}d ago"
            }
        } catch (e: Exception) {
            "Recently"
        }
    }

    private fun setupListeners() {
        btnRefresh.setOnClickListener {
            fetchMessages()
        }
    }

    private fun fetchMessages() {
        val mail = viewModel.mailTM.value
        if (mail == null) {
            Toast.makeText(requireContext(), "No account available", Toast.LENGTH_SHORT).show()
            updateUI(emptyList())
            return
        }
        
        lifecycleScope.launch {
            btnRefresh.isEnabled = false
            withContext(Dispatchers.IO) {
                try {
                    mail.fetchMessages(object : MessageFetchedCallback {
                        override fun onMessagesFetched(messages: List<Message>) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                updateUI(messages)
                                btnRefresh.isEnabled = true
                            }
                        }

                        override fun onError(error: Response) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "Error: ${error.responseCode}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnRefresh.isEnabled = true
                            }
                        }
                    })
                } catch (e: Exception) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Failed to fetch messages",
                            Toast.LENGTH_SHORT
                        ).show()
                        btnRefresh.isEnabled = true
                    }
                }
            }
        }
    }

    private fun updateUI(messages: List<Message>) {
        if (messages.isEmpty()) {
            recyclerMessages.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
            tvMessageCount.text = "0 messages"
        } else {
            recyclerMessages.visibility = View.VISIBLE
            layoutEmpty.visibility = View.GONE
            tvMessageCount.text = "${messages.size} message${if (messages.size > 1) "s" else ""}"
            adapter.submitList(messages)
        }
    }

    fun addNewMessage(message: Message) {
        val currentList = adapter.currentList.toMutableList()
        currentList.add(0, message)
        adapter.submitList(currentList)
        
        recyclerMessages.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        tvMessageCount.text = "${currentList.size} message${if (currentList.size > 1) "s" else ""}"
    }

    companion object {
        fun newInstance() = InboxFragment()
    }
}
