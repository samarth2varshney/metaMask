package com.example.metamask

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class PinataUploader {
    private val apiKey = "44fe67688e904e930dbf"
    private val apiSecret = "a077d47a96edbbe8edf743c7709f373721390fbe151144e2e5a7cbf78264baa7"

    fun uploadFileToPinata(fileUri: ByteArray) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // Set a longer connection timeout
            .readTimeout(30, TimeUnit.SECONDS)    // Set a longer read timeout
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val uniqueFileName = "${System.currentTimeMillis()}"

        val file = createTempFile(uniqueFileName, fileUri)

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                uniqueFileName,
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!)
            )
            .build()

        val request = Request.Builder()
            .url("https://api.pinata.cloud/pinning/pinFileToIPFS")
            .addHeader("pinata_api_key", apiKey)
            .addHeader("pinata_secret_api_key", apiSecret)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                println("Upload successful. Response: $responseBody")
                Log.d("PINATAFILE","Upload successful. Response: $responseBody")
            } else {
                println("Upload failed. Response code: ${response.code}")
                Log.d("PINATAFILE","Upload failed. Response code: ${response.code}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("PINATAFILE","Error uploading file to Pinata: ${e.message}")
            println("Error uploading file to Pinata: ${e.message}")
        }
    }

    private fun createTempFile(fileName: String, data: ByteArray): File {
        val file = File.createTempFile(fileName, null)
        val outputStream = FileOutputStream(file)
        outputStream.write(data)
        outputStream.close()
        return file
    }

}