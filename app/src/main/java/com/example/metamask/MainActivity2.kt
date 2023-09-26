package com.example.metamask

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class MainActivity2 : AppCompatActivity() {

    private lateinit var textView : TextView
    private lateinit var button : Button
    lateinit var ivValue: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        textView = findViewById(R.id.textView1)
        button = findViewById(R.id.button1)

        textView.text = "SECURE TEXT"

        button.setOnClickListener {
            findViewById<TextView>(R.id.textView3).text = encrypt("HELLO WORLD").toString()
        }
    }

    private fun encrypt(strToEncrypt: String): ByteArray {
        val plainText = strToEncrypt.toByteArray(Charsets.UTF_8)
        val key = generateKey(textView.text.toString())
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val cipherText = cipher.doFinal(plainText)
        ivValue = cipher.iv
        return cipherText
    }

    private fun generateKey(password: String): SecretKeySpec {
        val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
        val bytes = password.toByteArray()
        digest.update(bytes, 0, bytes.size)
        val key = digest.digest()
        val secretKeySpec = SecretKeySpec(key, "AES")
        return secretKeySpec
    }
}