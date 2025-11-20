package com.samyak.smalitm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    
    private val _mailTM = MutableLiveData<SMailTM?>()
    val mailTM: LiveData<SMailTM?> = _mailTM
    
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    
    private val _isAccountActive = MutableLiveData<Boolean>(false)
    val isAccountActive: LiveData<Boolean> = _isAccountActive
    
    private val _isListenerActive = MutableLiveData<Boolean>(false)
    val isListenerActive: LiveData<Boolean> = _isListenerActive
    
    fun setMailTM(mail: SMailTM?, email: String) {
        _mailTM.value = mail
        _email.value = email
        _isAccountActive.value = mail != null
    }
    
    fun setListenerActive(active: Boolean) {
        _isListenerActive.value = active
    }
    
    fun clearAccount() {
        _mailTM.value = null
        _email.value = ""
        _isAccountActive.value = false
        _isListenerActive.value = false
    }
    
    override fun onCleared() {
        super.onCleared()
        _mailTM.value?.closeMessageListener()
    }
}
