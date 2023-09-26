package com.example.metamask

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import io.metamask.androidsdk.Dapp
import io.metamask.androidsdk.EthereumViewModel
import io.metamask.androidsdk.RequestError
import io.metamask.androidsdk.TAG
import org.web3j.crypto.Keys
import javax.crypto.Cipher

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView: WebView = findViewById(R.id.webView)
        val WebButton: Button = findViewById(R.id.button2)
        var address: String? = null

        webView.settings.javaScriptEnabled = true
        webView.getSettings().setSupportZoom(true)
        webView.addJavascriptInterface(this, "Dialog")

        webView.loadUrl("file:///android_asset/metamask.html")

        val ethereumViewModel: EthereumViewModel =
            ViewModelProvider(this).get(EthereumViewModel::class.java)

        val dapp = Dapp("Droid Dapp", "https://droiddapp.com")

        ethereumViewModel.disconnect()

        findViewById<Button>(R.id.button3).setOnClickListener {
            ethereumViewModel.connect(dapp) { result ->
                if (result is RequestError) {
                    Log.e(TAG, "Ethereum connection error: ${result.message}")
                } else {
                    findViewById<TextView>(R.id.textView2).text = result.toString()
                    address = result.toString()
                }
            }
        }

        WebButton.setOnClickListener {
//            val javascriptCode =
//                "requestEncryptionPublicKey(0xdFcAa3c303D83E3Bd6223BF0745972a4852bFf1C);"
//
//            webView.post {
//                webView.evaluateJavascript(javascriptCode, null)
//            }
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

    }

    @JavascriptInterface
    fun showMsg(n: String) {
        Toast.makeText(this, "$n", Toast.LENGTH_SHORT).show()
    }

    fun getPublicKey(address: String) {
        val publicKey = Keys.toChecksumAddress(address)

    }


}
