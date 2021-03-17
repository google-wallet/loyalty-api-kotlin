package com.example.loyaltyapidemo.data.model

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.NoCache
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SignUpRepository {
    suspend fun signup(name: String, email: String) = suspendCoroutine<String> { cont ->
        // Instantiate the cache
        val cache = NoCache()

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

        // Instantiate the RequestQueue with the cache and network. Start the queue.
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }

        val request = JsonObjectRequest(Request.Method.POST,
            "https://gpay-loyaltyapi-demo.web.app/api/loyalty/create",
            JSONObject(
                mapOf(
                    "name" to name,
                    "email" to email
                )
            ),
            { response ->
                cont.resume(response.getString("token"))
            },
            { error ->
                cont.resumeWithException(error)
            })

        requestQueue.add(request)
    }
}