package com.example.electonexus_project;

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.text.AllCapsTransformationMethod
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.electonexus_project.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

class result : ComponentActivity() {

    private var lastTextViewId: Int? = R.id.Fname

    private lateinit var fbrefelc : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        val eid : String = getCredentialsFile()

        fbrefelc = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Election/")

        fbrefelc.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(elc in snapshot.children){
                        if(elc.key.toString() == eid){
                            val status = elc.child("Status").getValue(String::class.java)
                            if(status == "Election Ended"){
                                Toast.makeText(this@result,"In Ended",Toast.LENGTH_SHORT).show()
                                fbrefelc.child("$eid/Candidate").addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            for(candidate in snapshot.children){
                                                if(candidate.key.toString() == "uunn"){
                                                    continue
                                                }
                                                val name = candidate.child("name").getValue(String::class.java)
                                                val numvote = candidate.child("numvot").value.toString()
                                                createTextView(name!!,numvote)
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(this@result,error.message,Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                            else{
                                Toast.makeText(this@result,"Election has not ended",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@result,error.message,Toast.LENGTH_SHORT).show()
            }
        })


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
    @SuppressLint("RestrictedApi")
    private fun createTextView(name : String,eid :String){
        val containerLayout: ConstraintLayout = findViewById(R.id.containerresult)
        val newTextView = TextView(this).apply {
            id = View.generateViewId()
            text = name
            textSize = 18f
            setPadding(16, 16, 16, 16)
            setTextColor(Color.BLACK)


            typeface = Typeface.create(typeface, Typeface.BOLD)


            transformationMethod = AllCapsTransformationMethod(context)
        }
        val newTextView1 = TextView(this).apply {
            id = View.generateViewId() // Generate a unique ID
            text = eid
            textSize = 18f
            setPadding(16, 16, 16, 16)
            setTextColor(Color.BLACK)

            // Set font weight to 600 (using Typeface.BOLD for similar effect)
            typeface = Typeface.create(typeface, Typeface.BOLD)

            // Make text all caps
            transformationMethod = AllCapsTransformationMethod(context)
        }


        containerLayout.addView(newTextView)
        containerLayout.addView(newTextView1)

        applyConstraintsToView(containerLayout, newTextView,newTextView1)

        lastTextViewId = newTextView.id
    }
    private fun applyConstraintsToView(parent: ConstraintLayout, textView: TextView,textView1: TextView) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(parent)


        constraintSet.connect(textView.id, ConstraintSet.TOP, lastTextViewId!!, ConstraintSet.BOTTOM, 16)

        constraintSet.connect(textView.id, ConstraintSet.START, parent.id, ConstraintSet.START,dpToPx(70f) )
        constraintSet.connect(textView1.id, ConstraintSet.TOP, lastTextViewId!!, ConstraintSet.BOTTOM, 16)

        constraintSet.connect(textView1.id, ConstraintSet.START, textView.id, ConstraintSet.START,dpToPx(180f) )



        constraintSet.applyTo(parent)
    }
    private fun dpToPx(dp: Float): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}