package com.example.electonexus_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class voter_sign_up : ComponentActivity() {

    private lateinit var nameInsert: EditText
    private lateinit var unInsert: EditText
    private lateinit var pwInsert: EditText

    private lateinit var fbref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voter_sign_up)
        nameInsert = findViewById(R.id.LVoterName)
        unInsert = findViewById(R.id.LVoterEmailAddress)
        pwInsert = findViewById(R.id.LVoterPassword)

        fbref = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Account")
        val signup : Button = findViewById(R.id.LVoterSignup)

        var noerror : kotlin.Boolean = true

        signup.setOnClickListener {
                noerror = saveVoterData()
                if(noerror){
                Intent(this, MainActivity::class.java).also { startActivity(it) }
                finish()
                }
        }
    }

    private fun saveVoterData():Boolean{
        var b: Boolean = true
        val name = nameInsert.text.toString()
        val username = unInsert.text.toString()
        val password = pwInsert.text.toString()

        if(name.isEmpty()||(name.length>10)){
            nameInsert.error = "Please enter name under 10 characters"
            b=false
        }
        if(username.isEmpty()||(username.length>10)){
            unInsert.error = "Please enter username under 10 characters"
            b=false
        }
        if(password.isEmpty()||(password.length>10)){
            pwInsert.error = "Please enter password under 10 characters"
            b=false
        }
        if(b) {
            fbref.child(username).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        Toast.makeText(this@voter_sign_up, "Username Exists", Toast.LENGTH_SHORT)
                            .show()
                        b = false
                    }
                    else{
                        val voter = VoterModel(name,username, password,"V")
                        fbref.child(username).setValue(voter).addOnCompleteListener {
                            fbref.child("$username/ElectionRequest/11/name").setValue(" ")
                            fbref.child("$username/ElectionRequest/11/reqstat").setValue(" ")
                            fbref.child("$username/CandidateRequest/11/name").setValue(" ")
                            fbref.child("$username/CandidateRequest/11/reqstat").setValue(" ")
                            Toast.makeText(this@voter_sign_up, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { err ->
                            Toast.makeText(this@voter_sign_up, "${err.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@voter_sign_up, "$error", Toast.LENGTH_SHORT).show()
                }
            })

        }
        return b
    }
}