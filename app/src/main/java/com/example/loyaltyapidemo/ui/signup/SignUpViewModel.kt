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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope

import com.example.loyaltyapidemo.R
import com.example.loyaltyapidemo.data.model.SignUpRepository
import kotlinx.coroutines.launch

class SignUpViewModel(private val signupRepository: SignUpRepository) : ViewModel() {

    private val _signupForm = MutableLiveData<SignUpFormState>()
    val signUpFormState: LiveData<SignUpFormState> = _signupForm

    private val _signupResult = MutableLiveData<SignUpResult>()
    val signUpResult: LiveData<SignUpResult> = _signupResult

    fun signup(name: String, email: String) {
        viewModelScope.launch {
            val jwt = signupRepository.signup(name, email)
            _signupResult.value = SignUpResult(success = SignedUpUserView(name = name, jwt = jwt))
        }
    }

    fun signupDataChanged(name: String, email: String) {
        _signupForm.value = validate(name, email)
    }

    fun validate(name: String, email: String): SignUpFormState {
        return SignUpFormState(
            emailError = if (!isEmailValid(email)) {
                R.string.invalid_email
            } else {
                null
            },
            nameError = if (!isNameValid(name)) {
                R.string.invalid_name
            } else {
                null
            },
            isDataValid = isEmailValid(email) && isNameValid(name)
        )
    }

    // A placeholder username validation check
    private fun isEmailValid(email: String): Boolean {
        return if (email.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            email.isNotBlank()
        }
    }

    // A placeholder name validation check
    private fun isNameValid(name: String): Boolean {
        return name.isNotBlank()
    }
}