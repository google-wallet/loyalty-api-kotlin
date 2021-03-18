// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.loyaltyapidemo.data

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.NoCache
import com.example.loyaltyapidemo.BuildConfig
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SignUpRepository {
    private val requestQueue: RequestQueue

    init {
        // Instantiate the cache
        val cache = NoCache()

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

        // Instantiate the RequestQueue with the cache and network. Start the queue.
        requestQueue = RequestQueue(cache, network).apply {
            start()
        }
    }

    /**
     * Calls the sample app's backend API to create a loyalty pass.
     *
     * The API returns a token in the form of a JWT which is returned by this method
     *
     * @return A JWT that can be used to save the pass with Google Pay
     */
    suspend fun signUp(name: String, email: String): String {
        // Step 1: call our API to create a loyalty pass
        val response = executeJsonRequest(
            Request.Method.POST,
            "${BuildConfig.API_HOST}/api/loyalty/create",
            JSONObject(
                mapOf(
                    "name" to name,
                    "email" to email
                )
            )
        )

        // Step 2: return the JWT from the token field
        return response.getString("token")
    }

    private suspend fun executeJsonRequest(method: Int, url: String, body: JSONObject? = null) =
        suspendCoroutine<JSONObject> { cont ->
            val request = JsonObjectRequest(
                method,
                url,
                body,
                { response ->
                    cont.resume(response)
                },
                { error ->
                    cont.resumeWithException(error)
                }
            )

            requestQueue.add(request)
        }
}
