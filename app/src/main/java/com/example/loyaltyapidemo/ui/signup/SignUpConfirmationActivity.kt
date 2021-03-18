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
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loyaltyapidemo.databinding.ActivitySignUpConfirmationBinding

class SignUpConfirmationActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivitySignUpConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivitySignUpConfirmationBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        setSupportActionBar(activityBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val jwt = intent.getStringExtra("jwt")

        activityBinding.content.saveButton.setOnClickListener {
            val uri = Uri.parse("https://pay.google.com/gp/v/save/$jwt")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
