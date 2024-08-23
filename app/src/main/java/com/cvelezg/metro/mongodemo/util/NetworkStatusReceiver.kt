package com.cvelezg.metro.mongodemo.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NetworkStatusReceiver(private val context: Context) : BroadcastReceiver() {
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> get() = _isConnected

    init {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(this, filter)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        _isConnected.postValue(NetworkUtils.isNetworkAvailable(context!!))
    }

    fun unregister() {
        context.unregisterReceiver(this)
    }
}