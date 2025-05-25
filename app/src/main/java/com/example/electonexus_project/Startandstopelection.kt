package com.example.electonexus_project;

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.snap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.electonexus_project.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

class Startandstopelection : ComponentActivity() {

    private lateinit var fbref : DatabaseReference
    private lateinit var fbreft : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_startandstopelection)
        val eid : String = getCredentialsFile()


        fbref = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Election/$eid/Status")

        var textviewstatus : TextView = findViewById(R.id.AtextViewStatus)

        var startButton : Button = findViewById(R.id.AStatusStartButton)


        fbref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val stat = snapshot.getValue().toString()
                    updateStatus(stat)
                    textviewstatus.setText(stat)
                    if(stat == "Not Active"){
                        startButton.setText("Start Election")
                    }
                    else if(stat == "Active"){
                        startButton.setText("Stop Election")
                    }
                    else if(stat == "Election Ended"){
                        startButton.setText("Election Ended")
                    }
                    startButton.setOnClickListener {
                        if(stat == "Not Active"){
                            fbref.setValue("Active")
                            textviewstatus.setText("Active")
                            startButton.setText("Stop Election")
                        }
                        else if(stat == "Active"){
                            fbref.setValue("Election Ended")
                            textviewstatus.setText("Election Ended")
                            startButton.setText("Election Ended")
                        }
                        else if(stat == "Rlection Ended"){

                        }
                    }
                }
                else{
                    Toast.makeText(this@Startandstopelection,"Snapshot Not Found",Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Startandstopelection,"${error.message}",Toast.LENGTH_SHORT).show()
            }
        })

    }
    private fun updateStatus(stat : String ){

        }


    private fun getCredentialsFile(): String {
        try {
            val fileName = "credentials.txt"
            val fileInputStream: FileInputStream = openFileInput(fileName)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            var username: String? = null
            var Acctype: String? = null

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                line?.let {
                    when {
                        it.startsWith("eID:") -> {
                            username = it.substringAfter("eID:").trim()
                            return username!!
                        }
                        it.startsWith("Acctype:") -> {
                            Acctype = it.substringAfter("Acctype:").trim()
                        }
                    }
                }
            }

            bufferedReader.close()

            if (username != null && Acctype != null) {
                Toast.makeText(this, "Username: $username", Toast.LENGTH_LONG).show()
                Toast.makeText(this, "Password: $Acctype", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Incomplete credentials found", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "No credentials found", Toast.LENGTH_SHORT).show()
        }
        return ""
    }

}