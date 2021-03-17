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

package com.example.loyaltyapidemo.ui.signup

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import com.example.loyaltyapidemo.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var signupViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)

        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val signup = findViewById<Button>(R.id.signup)
        val loading = findViewById<ProgressBar>(R.id.loading)

        signupViewModel = ViewModelProvider(this, SignUpViewModelFactory())
                .get(SignUpViewModel::class.java)

        signupViewModel.signUpFormState.observe(this@SignUpActivity, Observer {
            val signupState = it ?: return@Observer

            // disable sign up button unless both username / password is valid
            signup.isEnabled = signupState.isDataValid

            if (signupState.nameError != null) {
                name.error = getString(signupState.nameError)
            } else if (email.text.isNotBlank() && signupState.emailError != null) {
                email.error = getString(signupState.emailError)
            }
        })

        signupViewModel.signUpResult.observe(this@SignUpActivity, Observer {
            val signupResult = it ?: return@Observer

            loading.visibility = View.GONE

            val intent = Intent(this, SignUpConfirmationActivity::class.java).apply {
                putExtra("jwt", signupResult.success!!.jwt)
            }
            startActivity(intent)
        })

        name.apply {
            afterTextChanged {
                signupViewModel.signupDataChanged(
                        name.text.toString(),
                        email.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        signupViewModel.signup(
                                name.text.toString(),
                                email.text.toString()
                        )
                }
                false
            }
        }

        email.afterTextChanged {
            signupViewModel.signupDataChanged(
                    name.text.toString(),
                    email.text.toString()
            )
        }

        signup.setOnClickListener {
            loading.visibility = View.VISIBLE
            signupViewModel.signup(name.text.toString(), email.text.toString())
        }
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}