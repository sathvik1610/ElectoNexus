package com.example.electonexus_project

import android.content.Context
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
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : ComponentActivity() {

    private lateinit var signupButton: Button
    private lateinit var loginButton: Button

    private lateinit var un: EditText
    private lateinit var pw: EditText

    private lateinit var fbref: DatabaseReference

    var type : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        un = findViewById(R.id.LEmailAddress)
        pw = findViewById(R.id.LPassword)
        loginButton = findViewById(R.id.LLoginButton)
        signupButton = findViewById(R.id.LSignupButton)



        loginButton.setOnClickListener {
            checkUsername()
        }
        signupButton.setOnClickListener {
            Intent(this, Account_type::class.java).also { startActivity(it) }
        }

    }

    private fun checkUsername() {
        var b = true
        val username = un.text.toString()
        val password = pw.text.toString()
        fbref =
            FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Account")

        if (username.isEmpty()) {
            un.error = "Please enter Username"
            b = false
        }
        if (password.isEmpty()) {
            pw.error = "Please enter password"
            b = false
        }
        if (b) {
            fbref.child(username).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val tpw = dataSnapshot.child("pw").value
                        if (tpw == password) {
                            Toast.makeText(
                                this@MainActivity,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            type = dataSnapshot.child("atype").value.toString()
                            if(type=="A"){
                                val eID = dataSnapshot.child("eid").value.toString()
                                val elname = dataSnapshot.child("en").value.toString()
                                saveCredentialsToFile(username, "A",eID,elname)
                                Intent(this@MainActivity, admin_dashboard::class.java).also { startActivity(it) }
                            }
                            else if(type=="V"){
                                val name = dataSnapshot.child("name").value.toString()
                                saveCredentialsToFile(username,"V","",name)
                                Intent(this@MainActivity, voter_dashboard::class.java).also { startActivity(it) }
                            }

                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Invalid Password",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Username Not Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "$error", Toast.LENGTH_SHORT).show()

                }

            })
        }

    }
    private fun saveCredentialsToFile(username: String, Acctype: String,eID : String,elname : String) {
        try {

            val fileName = "credentials.txt"
            val fileOutputStream: FileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)


            outputStreamWriter.write("Username: $username\n")
            outputStreamWriter.write("Acctype: $Acctype\n")
            outputStreamWriter.write("eID: $eID\n")
            outputStreamWriter.write("elname: $elname\n")

            outputStreamWriter.close()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save credentials", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCredentialsFile() {
        try {

            val fileName = "credentials.txt"


            val fileInputStream: FileInputStream = openFileInput(fileName)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            var line: String?


            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }


            bufferedReader.close()


            Toast.makeText(this, "File Content:\n${stringBuilder.toString()}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {

            Toast.makeText(this, "No credentials found", Toast.LENGTH_SHORT).show()
        }
    }
    private fun readCredentialsFile() {
        try {

            val fileName = "credentials.txt"


            val fileInputStream: FileInputStream = openFileInput(fileName)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            var line: String?


            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }


            bufferedReader.close()



            Toast.makeText(this, "File Content:\n${stringBuilder.toString()}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            // If the file is not found or other errors occur
            Toast.makeText(this, "No credentials found", Toast.LENGTH_SHORT).show()
        }
    }
}



