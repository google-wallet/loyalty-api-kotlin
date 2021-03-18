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

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.loyaltyapidemo.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)

        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val signUp = findViewById<Button>(R.id.signUp)
        val loading = findViewById<ProgressBar>(R.id.loading)

        signUpViewModel = ViewModelProvider(this, SignUpViewModelFactory())
            .get(SignUpViewModel::class.java)

        signUpViewModel.signUpFormState.observe(
            this@SignUpActivity,
            Observer {
                val signUpState = it ?: return@Observer

                // disable sign up button unless both username / password is valid
                signUp.isEnabled = signUpState.isDataValid

                if (signUpState.nameError != null) {
                    name.error = getString(signUpState.nameError)
                } else if (email.text.isNotBlank() && signUpState.emailError != null) {
                    email.error = getString(signUpState.emailError)
                }
            }
        )

        signUpViewModel.signUpResult.observe(
            this@SignUpActivity,
            Observer {
                val signUpResult = it ?: return@Observer

                loading.visibility = View.GONE

                val intent = Intent(this, SignUpConfirmationActivity::class.java).apply {
                    putExtra("jwt", signUpResult.success!!.jwt)
                }
                startActivity(intent)
            }
        )

        name.apply {
            afterTextChanged {
                signUpViewModel.signUpDataChanged(
                    name.text.toString(),
                    email.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        signUpViewModel.signUp(
                            name.text.toString(),
                            email.text.toString()
                        )
                }
                false
            }
        }

        email.afterTextChanged {
            signUpViewModel.signUpDataChanged(
                name.text.toString(),
                email.text.toString()
            )
        }

        signUp.setOnClickListener {
            loading.visibility = View.VISIBLE
            signUpViewModel.signUp(name.text.toString(), email.text.toString())
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
