package com.example.metamask

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import io.metamask.androidsdk.Dapp
import io.metamask.androidsdk.EthereumViewModel
import io.metamask.androidsdk.RequestError
import io.metamask.androidsdk.TAG

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ethereumViewModel: EthereumViewModel = ViewModelProvider(this).get(EthereumViewModel::class.java)

        val dapp = Dapp("Droid Dapp", "https://droiddapp.com")

        findViewById<Button>(R.id.button3).setOnClickListener {
            Toast.makeText(this,"Hello",Toast.LENGTH_LONG).show()
            ethereumViewModel.connect(dapp) { result ->
                if (result is RequestError) {
                    Log.e(TAG, "Ethereum connection error: ${result.message}")
                } else {
                    startActivity(Intent(this,MainActivity2::class.java))
                    finish()
                }
            }
        }

    }
}
