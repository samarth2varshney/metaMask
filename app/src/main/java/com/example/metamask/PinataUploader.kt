package com.example.metamask

import android.content.Context
import android.net.Uri
import okhttp3.MediaType
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

    fun uploadFileToPinata(context: Context, fileUri: Uri) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // Set a longer connection timeout
            .readTimeout(30, TimeUnit.SECONDS)    // Set a longer read timeout
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val file = uriToFile(context, fileUri)
        val uniqueFileName = "${System.currentTimeMillis()}_${file!!.name}"

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
                // Parse the response JSON to get the IPFS hash or other information
                // You can handle the response according to your requirements
                println("Upload successful. Response: $responseBody")
            } else {
                println("Upload failed. Response code: ${response.code}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            println("Error uploading file to Pinata: ${e.message}")
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val file = File(context.cacheDir, "temp_file")
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(4 * 1024) // 4KB buffer size (adjust as needed)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
                return file
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}