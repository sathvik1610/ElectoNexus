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

class admin_sign_up : ComponentActivity() {
    private lateinit var eninsert : EditText
    private lateinit var nameinsert : EditText
    private lateinit var uninsert : EditText
    private lateinit var pwinsert : EditText

    private lateinit var fbref1 : DatabaseReference
    private lateinit var fbref2 : DatabaseReference
    private lateinit var fbref3 : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_sign_up)

        eninsert = findViewById(R.id.LSignupElectionName)
        nameinsert = findViewById(R.id.LSignupName)
        uninsert = findViewById(R.id.LSignupEmailAddress)
        pwinsert = findViewById(R.id.LSignupPassword)

        fbref1 = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Account")
        val signup :  Button = findViewById(R.id.LSignupAdmin)

        signup.setOnClickListener {
                addAdmin()

            }


    }

    private fun addAdmin() {
        var b  =false
        val en = eninsert.text.toString()
        val name = nameinsert.text.toString()
        val un = uninsert.text.toString()
        val pw = pwinsert.text.toString()

        if(en.isEmpty()||(en.length>20)){
            eninsert.error="Please enter Election name under 20 characters"
            b=true
        }
        if(name.isEmpty()||(name.length>10)){
            nameinsert.error="Please enter name under 10 characters"
            b=true

        }
        if(un.isEmpty()||(un.length>10)){
            uninsert.error="Please enter username under 10 characters"
            b=true

        }
        if(pw.isEmpty()||(pw.length>10)){
            pwinsert.error="Please enter password under 10 characters"
            b=true

        }
        val eId = (10000000..99999999).random()

        if(!b) {
            fbref1.child(un).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        Toast.makeText(this@admin_sign_up, "Username Exists", Toast.LENGTH_SHORT)
                            .show()
                        b = true
                    }
                    else{
                        val admin = AdminModel(en,name,un,pw,"A",eId)

                        fbref1.child(un).setValue(admin).addOnCompleteListener {
                            Toast.makeText(this@admin_sign_up, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                            fbref2 = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Election/$eId")
                            fbref2.child("/admin").setValue(un)
                            fbref2.child("/ename").setValue(en)
                            fbref2.child("Status").setValue("Not Active")
                            fbref2.child("Voter/uunn/name").setValue("")
                            fbref2.child("Candidate/uunn/name").setValue("")
                            fbref2.child("Voter/uunn/reqstat").setValue("")
                            fbref2.child("Candidate/uunn/reqstat").setValue("")
                            Intent(this@admin_sign_up, MainActivity::class.java).also { startActivity(it) }
                            finish()
                        }.addOnFailureListener { err ->
                            Toast.makeText(this@admin_sign_up, "${err.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@admin_sign_up, "$error", Toast.LENGTH_SHORT).show()
                }
            })

       }

    }
}