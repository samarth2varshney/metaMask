package com.example.metamask

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class MainActivity2 : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var button: Button
    private lateinit var fileButton: Button
    lateinit var ivValue: ByteArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        textView = findViewById(R.id.textView1)
        button = findViewById(R.id.button1)
        fileButton = findViewById(R.id.uploadButton)

        textView.text = "SECURE TEXT" //TODO generate random secrete key

        fileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"

            pickFileLauncher.launch(intent)
        }
    }

    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val selectedFileUri: Uri? = data.data
                    Log.d("FILE", "SELECTEDFILEURI: ${selectedFileUri}")
                    if (selectedFileUri != null) {
                        val fileBytes = readFileFromUri(selectedFileUri) //byte array conversion
                        Log.d("FILE", "FILBYTES: ${fileBytes}")
                        if (fileBytes != null) {
                            val encryptedBytes = encrypt(fileBytes)
                            Log.d("FILE", "ENCRYPTEDBYTES: ${encryptedBytes}")
                            findViewById<TextView>(R.id.textView3).text = encryptedBytes.toString()
                            GlobalScope.launch {
                                if (selectedFileUri != null) {
                                    PinataUploader().uploadFileToPinata(encryptedBytes)
                                }
                            }
                        }
                    }
                }

            }
        }

    private fun readFileFromUri(uri: Uri): ByteArray? {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            return inputStream?.readBytes()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    private fun encrypt(dataToEncrypt: ByteArray): ByteArray {
        val key = generateKey(textView.text.toString())
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val cipherText = cipher.doFinal(dataToEncrypt)
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