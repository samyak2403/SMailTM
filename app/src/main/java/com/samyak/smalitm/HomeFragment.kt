package com.samyak.smalitm

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.samyak.smalitm.callbacks.EventListener
import com.samyak.smalitm.util.Account
import com.samyak.smalitm.util.Message
import com.samyak.smalitm.util.SMaliBuilder
import com.samyak.smalitm.SMaliTM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.security.auth.login.LoginException

class HomeFragment : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels()
    private val currentPassword = "password123"
    private var currentEmail = ""
    private var mailTM: SMaliTM? = null
    private var isListenerActive = false
    private var onMessageReceived: ((Message) -> Unit)? = null
    
    private lateinit var emailTextView: TextView
    private lateinit var btnEmailCopy: LinearLayout
    private lateinit var btnEmailChange: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()
        observeViewModel()
    }
    
    private fun observeViewModel() {
        viewModel.email.observe(viewLifecycleOwner) { email ->
            if (!email.isNullOrEmpty()) {
                emailTextView.text = email
                currentEmail = email
            }
        }
        
        viewModel.isListenerActive.observe(viewLifecycleOwner) { isActive ->
            isListenerActive = isActive
        }
    }

    private fun initViews(view: View) {
        emailTextView = view.findViewById(R.id.emailTextView)
        btnEmailCopy = view.findViewById(R.id.btnEmailCopy)
        btnEmailChange = view.findViewById(R.id.btnEmailChange)
        
        // Auto-generate email and create account only if not already created
        if (viewModel.mailTM.value == null) {
            autoGenerateAndCreateAccount()
        } else {
            // If account already exists, display it
            viewModel.email.value?.let {
                emailTextView.text = it
                currentEmail = it
            }
        }
    }

    private fun setupListeners() {
        btnEmailCopy.setOnClickListener {
            copyToClipboard(emailTextView.text.toString())
        }
        
        btnEmailChange.setOnClickListener {
            changeEmail()
        }
    }
    
    private fun autoGenerateAndCreateAccount() {
        // Prevent creating multiple accounts
        if (viewModel.mailTM.value != null) {
            return
        }
        
        lifecycleScope.launch {
            emailTextView.text = "Generating..."

            try {
                // Create random account
                val mail = withContext(Dispatchers.IO) {
                    SMaliBuilder.createDefault(currentPassword)
                }
                
                mail.init()

                val account = withContext(Dispatchers.IO) {
                    mail.getSelf()
                }

                emailTextView.text = account.email
                currentEmail = account.email
                
                viewModel.setMailTM(mail, account.email)
                
                // Auto-start event listener
                startEventListener()
                
                Toast.makeText(requireContext(), "✅ Email ready: ${account.email}", Toast.LENGTH_LONG).show()

            } catch (e: LoginException) {
                emailTextView.text = "Failed to generate"
                Toast.makeText(requireContext(), "Failed to create account", Toast.LENGTH_SHORT).show()
                viewModel.clearAccount()
            } catch (e: Exception) {
                emailTextView.text = "Error occurred"
                Toast.makeText(requireContext(), "Unexpected error", Toast.LENGTH_SHORT).show()
                viewModel.clearAccount()
            }
        }
    }
    
    private fun changeEmail() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Change Email")
            .setMessage("This will delete your current email and generate a new one. Continue?")
            .setPositiveButton("Yes") { _, _ ->
                deleteAndCreateNewAccount()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun deleteAndCreateNewAccount() {
        val mail = viewModel.mailTM.value
        
        lifecycleScope.launch {
            emailTextView.text = "Changing..."
            
            // Delete old account if exists
            if (mail != null) {
                withContext(Dispatchers.IO) {
                    try {
                        mail.delete()
                    } catch (e: Exception) {
                        // Ignore deletion errors
                    }
                }
                viewModel.clearAccount()
            }
            
            // Create new account
            autoGenerateAndCreateAccount()
        }
    }
    
    private fun copyToClipboard(text: String) {
        if (text.isEmpty() || text == "Generating..." || text == "Loading...") {
            Toast.makeText(requireContext(), "No email to copy", Toast.LENGTH_SHORT).show()
            return
        }
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Email", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "📋 Email copied!", Toast.LENGTH_SHORT).show()
    }



    private fun startEventListener() {
        val mail = viewModel.mailTM.value
        if (mail == null) {
            Toast.makeText(requireContext(), "Create an account first", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (viewModel.isListenerActive.value == true) {
            Toast.makeText(requireContext(), "Listener already active", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                mail.openEventListener(object : EventListener {
                    override fun onReady() {
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.setListenerActive(true)
                        }
                    }

                    override fun onMessageReceived(message: Message) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            onMessageReceived?.invoke(message)
                            Toast.makeText(requireContext(), "📧 New message from ${message.senderAddress}", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onMessageSeen(message: Message) {
                        // Message seen
                    }

                    override fun onMessageDelete(id: String) {
                        // Message deleted
                    }

                    override fun onAccountUpdate(account: Account) {
                        // Account updated
                    }

                    override fun onAccountDelete(account: Account) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.setListenerActive(false)
                        }
                    }

                    override fun onError(error: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.setListenerActive(false)
                        }
                    }
                })
            } catch (e: Exception) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to start listener", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    fun setOnMessageReceivedListener(listener: (Message) -> Unit) {
        onMessageReceived = listener
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
