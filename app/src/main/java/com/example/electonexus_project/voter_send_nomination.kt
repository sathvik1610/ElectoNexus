package com.example.electonexus_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.snap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

class voter_send_nomination : ComponentActivity(){

    private lateinit var ename : String

    private lateinit var fbrefacc : DatabaseReference
    private lateinit var fbrefelc : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_send_nomination)
        val un = getCredentialsFile()


        val addbutton : Button = findViewById(R.id.Vsnsnbutton)

        var isEIDFound = false
        var isUserInE = false

        fbrefacc = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Account")


        addbutton.setOnClickListener {
            val eidtext : EditText= findViewById(R.id.Vsneid)
            val eid : String = eidtext.text.toString()
            fbrefelc = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Election")

            fbrefelc.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var a =""
                        for(election in snapshot.children) {
                            val eidn = election.key

                            if(eid.equals(eidn)) {
                                isEIDFound = true
                                ename = election.child("ename").getValue().toString()
                                Toast.makeText(this@voter_send_nomination, "Election ID found", Toast.LENGTH_SHORT).show()
                                fbrefelc.child("$eid/Candidate").addListenerForSingleValueEvent(object :ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            for(voter in snapshot.children){
                                                val vk = voter.key.toString()
                                                if(un == vk) {
                                                    isUserInE = true
                                                    Toast.makeText(this@voter_send_nomination,"Already enrolled in election",Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            if(isUserInE==false){
                                                fbrefacc.addListenerForSingleValueEvent(object :ValueEventListener{
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        if(snapshot.exists()){
                                                            for(user in snapshot.children){
                                                                if(un == user.key.toString()){
                                                                    a = user.child("name").value.toString()
                                                                    fbrefelc.child("$eid/Candidate/$un/name").setValue(a)
                                                                    fbrefelc.child("$eid/Candidate/$un/reqstat").setValue("ReqSent")
                                                                    fbrefelc.child("$eid/Candidate/$un/numvot").setValue(0)
                                                                    fbrefacc.child("$un/CandidateRequest/$eid/reqstat").setValue("ReqSent")
                                                                    fbrefacc.child("$un/CandidateRequest/$eid/ename").setValue(ename)
                                                                    Toast.makeText(this@voter_send_nomination,"Nomination Sent Successfully",Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                        }
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                        Toast.makeText(this@voter_send_nomination,error.message,Toast.LENGTH_SHORT).show()
                                                    }
                                                })
                                            }
                                            isUserInE = false
                                        }
                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(this@voter_send_nomination,error.message,Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                        }
                        if (isEIDFound==false){
                            Toast.makeText(
                                this@voter_send_nomination,
                                "Invalid Election ID",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        isEIDFound = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@voter_send_nomination,error.message,Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
    private fun getCredentialsFile() :String {
        try {

            val fileName = "credentials.txt"
            //val votername : TextView = findViewById(R.id.voterdbname)
           // val voterun : TextView = findViewById(R.id.voterdbun)

            val fileInputStream: FileInputStream = openFileInput(fileName)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)


            var username: String? = null
            var Accname: String? = null


            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                line?.let {
                    when {
                        it.startsWith("Username:") -> {
                            username = it.substringAfter("Username:").trim()
                            return username!!
                            //un.setText("ELECTION NAME : $username")
                        }
                        it.startsWith("Acctype:") -> {
                          //  Acctype = it.substringAfter("Acctype:").trim()
                        }
                    }
                }
            }


            bufferedReader.close()

            /*if (username != null && Acctype != null) {
                Toast.makeText(this, "Username: $username", Toast.LENGTH_LONG).show()
                Toast.makeText(this, "Password: $Acctype", Toast.LENGTH_LONG).show()
            } else {

                Toast.makeText(this, "Incomplete credentials found", Toast.LENGTH_SHORT).show()
            }*/

        } catch (e: Exception) {

            Toast.makeText(this, "No credentials found", Toast.LENGTH_SHORT).show()
        }
        return ""
    }
}