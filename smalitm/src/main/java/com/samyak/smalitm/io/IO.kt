package com.samyak.smalitm.io

import com.samyak.smalitm.util.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

/**
 * The IO class handles HTTP communication for the SMaliTM library.
 */
object IO {
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val PATCH = "application/merge-patch+json".toMediaType()

    /**
     * Makes a POST request to the specified URL with authentication and JSON content.
     */
    fun requestPOST(baseUrl: String, auth: String?, contentJSON: String): Response {
        return try {
            val url = URL(baseUrl)
            val requestBuilder = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("accept", "application/json")
                .post(contentJSON.toRequestBody(JSON))
            
            if (auth != null) {
                requestBuilder.addHeader("Authorization", "Bearer $auth")
            }
            
            val response = client.newCall(requestBuilder.build()).execute()
            Response(response.code, response.body?.string() ?: "")
        } catch (e: Exception) {
            Response(0, "")
        }
    }

    /**
     * Makes a POST request to the specified URL with JSON content.
     */
    fun requestPOST(baseUrl: String, contentJSON: String): Response {
        return try {
            requestPOST(baseUrl, null, contentJSON)
        } catch (e: Exception) {
            Response(0, e.toString())
        }
    }

    fun requestGET(baseUrl: String, auth: String?): Response {
        return try {
            val url = URL(baseUrl)
            val requestBuilder = Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("accept", "application/json")
            
            if (auth != null) {
                requestBuilder.addHeader("Authorization", "Bearer $auth")
            }
            
            val response = client.newCall(requestBuilder.build()).execute()
            Response(response.code, response.body?.string() ?: "")
        } catch (e: Exception) {
            Response(0, "")
        }
    }

    fun requestGET(baseUrl: String): Response {
        return try {
            requestGET(baseUrl, null)
        } catch (e: Exception) {
            Response(0, "")
        }
    }

    fun requestDELETE(baseUrl: String, auth: String?): Response {
        return try {
            val url = URL(baseUrl)
            val requestBuilder = Request.Builder()
                .url(url)
                .delete()
                .addHeader("Content-Type", "application/json")
                .addHeader("accept", "application/json")
            
            if (auth != null) {
                requestBuilder.addHeader("Authorization", "Bearer $auth")
            }
            
            val response = client.newCall(requestBuilder.build()).execute()
            Response(response.code, response.body?.string() ?: "")
        } catch (e: Exception) {
            Response(0, "")
        }
    }

    fun requestPATCH(baseUrl: String, auth: String?, data: String): Response {
        return try {
            val url = URL(baseUrl)
            val requestBuilder = Request.Builder()
                .url(url)
                .patch(data.toRequestBody(PATCH))
                .addHeader("accept", "application/json")
            
            if (auth != null) {
                requestBuilder.addHeader("Authorization", "Bearer $auth")
            }
            
            val response = client.newCall(requestBuilder.build()).execute()
            Response(response.code, response.body?.string() ?: "")
        } catch (e: Exception) {
            Response(0, "")
        }
    }

    fun requestPATCH(baseUrl: String, auth: String?): Response {
        return requestPATCH(baseUrl, auth, "{\"seen\" : true}")
    }
}
