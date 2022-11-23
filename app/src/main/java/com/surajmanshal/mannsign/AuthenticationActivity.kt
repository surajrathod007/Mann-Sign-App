package com.surajmanshal.mannsign

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.stringPreferencesKey
import com.surajrathod.authme.util.DataStore
import com.surajrathod.authme.util.DataStore.preferenceDataStoreAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CoroutineScope(Dispatchers.IO).launch{
            val token = isLoggedIn(DataStore.JWT_TOKEN)
            if(token!=null){
                val intent = Intent(this@AuthenticationActivity, ProfileActivity::class.java)
                intent.putExtra(DataStore.JWT_TOKEN,token)
                startActivity(intent)
                finish()
            }
        }


    }
    suspend fun isLoggedIn(key : String) : String? {
        val data = preferenceDataStoreAuth.data.first()
        return data[stringPreferencesKey(key)]
    }
}