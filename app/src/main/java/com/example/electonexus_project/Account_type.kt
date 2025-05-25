package com.example.electonexus_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class Account_type : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_type)

        val radioGroup: RadioGroup = findViewById(R.id.LradioGroup)
        val continueButton : Button = findViewById(R.id.LAccountTypeContinueButton)

        continueButton.setOnClickListener {

            val checkedId = radioGroup.checkedRadioButtonId

            if(checkedId == R.id.LradioButtonAdmin){
                Intent(this, admin_sign_up::class.java).also { startActivity(it) }
            }else if (checkedId == R.id.LradioButtonVoter){
                Intent(this, voter_sign_up::class.java).also { startActivity(it) }
            }else{

                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
            }
        finish()
        }
    }
}