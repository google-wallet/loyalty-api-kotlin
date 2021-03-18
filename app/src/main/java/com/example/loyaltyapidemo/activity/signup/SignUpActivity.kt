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

package com.example.loyaltyapidemo.activity.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.loyaltyapidemo.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signUpViewModel = ViewModelProvider(this, SignUpViewModelFactory())
            .get(SignUpViewModel::class.java)

        signUpViewModel.signUpFormState.observe(
            this@SignUpActivity,
            Observer {
                val signUpState = it ?: return@Observer

                // disable sign up button unless both name and email are valid
                binding.signUp.isEnabled = signUpState.isDataValid

                binding.name.error = signUpState.nameError?.let(::getString)
                binding.email.error =
                    if (binding.email.text.isNotBlank()) {
                        signUpState.emailError?.let(::getString)
                    } else {
                        null
                    }
            }
        )

        // watch for signUpResult
        signUpViewModel.signUpResult.observe(
            this@SignUpActivity,
            Observer {
                val signUpResult = it ?: return@Observer

                binding.loading.visibility = View.GONE

                // start the SignUpConfirmationActivity passing in the JWT from the signUpResult
                val intent = Intent(this, SignUpConfirmationActivity::class.java).apply {
                    putExtra("jwt", signUpResult.success!!.jwt)
                }
                startActivity(intent)
            }
        )

        binding.name.afterTextChanged {
            signUpViewModel.signUpDataChanged(
                binding.name.text.toString(),
                binding.email.text.toString()
            )
        }

        binding.email.afterTextChanged {
            signUpViewModel.signUpDataChanged(
                binding.name.text.toString(),
                binding.email.text.toString()
            )
        }

        binding.signUp.setOnClickListener {
            binding.loading.visibility = View.VISIBLE

            // on signUp click, initiate the signUp process
            signUpViewModel.signUp(binding.name.text.toString(), binding.email.text.toString())
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
