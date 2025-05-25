package com.example.electonexus_project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.google.firebase.components.Component
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

class admin_dashboard : ComponentActivity() {

    //private lateinit var un: TextView
    //private lateinit var Acctype: TextView

    private lateinit var fbref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    // un = findViewById(R.id.electionname)

        fbref =
            FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Account")

        setContentView(R.layout.activity_admin_dashboard)
        val voterlistButton : Button = findViewById(R.id.Avoterlistbutton)
        val candidatelistButton : Button = findViewById(R.id.Acandidatelistbutton)
        val voterrequestButton : Button = findViewById(R.id.Avoterrequestbutton)
        val candidaterequestbutton : Button = findViewById(R.id.Acandidaterequestbutton)
        val statusbtn : Button = findViewById(R.id.AElectionStatus)
        val reultsbtn : Button = findViewById(R.id.AElectionResults)
        val Asoutbtn : Button = findViewById(R.id.Asignoutbutton)

        setCredentialsFile()
        statusbtn.setOnClickListener {
            Intent(this, Startandstopelection::class.java).also{startActivity(it) }
        }
        reultsbtn.setOnClickListener {
            Intent(this, result::class.java).also{startActivity(it) }
        }
        Asoutbtn.setOnClickListener{
            val intentt = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intentt)
        }

        //setCredentialsFile()
        voterlistButton.setOnClickListener {
            Intent(this, voter_list::class.java).also{startActivity(it) }
        }
        candidatelistButton.setOnClickListener {
            Intent(this, candidate_list::class.java).also { startActivity(it) }
        }
        voterrequestButton.setOnClickListener {
            Intent(this, Voterrequest::class.java).also { startActivity(it) }
        }
        candidaterequestbutton.setOnClickListener {
            Intent(this, candidate_request::class.java).also { startActivity(it) }
        }


    }

    @SuppressLint("SetTextI18n")
    private fun setCredentialsFile() {
        try {

            val fileName = "credentials.txt"
            val enview : TextView = findViewById(R.id.electionname)
            val eidview : TextView = findViewById(R.id.electionid)

            val fileInputStream: FileInputStream = openFileInput(fileName)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)


            var username: String? = null
            var Acctype: String? = null


            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                line?.let {
                    when {
                        it.startsWith("Username:") -> {
                            username = it.substringAfter("Username:").trim()
                            fbref.child(username!!).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        val foundun = snapshot.child("en").value
                                        enview.setText("ELECTION NAME : $foundun")
                                        val foundeid = snapshot.child("eid").value
                                        eidview.setText("ELECTION ID : $foundeid")

                                    }
                                    else{
                                        Toast.makeText(this@admin_dashboard,"Not Found in Local File",Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@admin_dashboard, "$error", Toast.LENGTH_SHORT).show()
                                }
                            })

                        }
                        it.startsWith("Acctype:") -> {
                            Acctype = it.substringAfter("Acctype:").trim()
                        }
                    }
                }
            }


            bufferedReader.close()

            /*if (username != null && Acctype != null) {
                Toast.makeText(this, "Username: $username", Toast.LENGTH_LONG).show()
                Toast.makeText(this, "Password: $Acctype", Toast.LENGTH_LONG).show()
            } else {
                // If either username or password is missing
                Toast.makeText(this, "Incomplete credentials found", Toast.LENGTH_SHORT).show()
            }*/

        } catch (e: Exception) {
            // If the file is not found or other errors occur
            Toast.makeText(this, "No credentials found", Toast.LENGTH_SHORT).show()
        }
    }

}
