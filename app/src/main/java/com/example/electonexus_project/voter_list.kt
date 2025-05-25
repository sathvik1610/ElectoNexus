package com.example.electonexus_project;

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
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

class voter_list : ComponentActivity() {

    private var lastTextViewId: Int? = R.id.textView4

    private lateinit var fbref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voter_list)

        val eid : String = getCredentialsFile()



        fbref = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Election/$eid/Voter")

        val containerLayout: ConstraintLayout = findViewById(R.id.containervoterlist)

        fbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    for(voter in snapshot.children){
                        val chk = voter.child("reqstat").getValue(String::class.java)
                        if(chk=="ReqSent"||chk == "Rejected"){
                            continue
                        }
                        val name = voter.child("name").getValue(String::class.java)
                        createTextView(name!!)
                    }
                }
                else{
                    Toast.makeText(this@voter_list,"Not Found in Local File",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@voter_list, "$error", Toast.LENGTH_SHORT).show()
            }
        })
    }
    @SuppressLint("RestrictedApi")
    private fun createTextView(name : String){
        val containerLayout: ConstraintLayout = findViewById(R.id.containervoterlist)
        val newTextView = TextView(this).apply {
            id = View.generateViewId()
            text = name
            textSize = 18f
            setPadding(16, 16, 16, 16)
            setTextColor(Color.BLACK)

            typeface = Typeface.create(typeface, Typeface.BOLD)

            transformationMethod = AllCapsTransformationMethod(context)
        }

        containerLayout.addView(newTextView)

        applyConstraintsToView(containerLayout, newTextView)

        lastTextViewId = newTextView.id
    }
    private fun applyConstraintsToView(parent: ConstraintLayout, textView: TextView) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(parent)


        constraintSet.connect(textView.id, ConstraintSet.TOP, lastTextViewId!!, ConstraintSet.BOTTOM, 16)

        constraintSet.connect(textView.id, ConstraintSet.START, parent.id, ConstraintSet.START,dpToPx(60f) )



        constraintSet.applyTo(parent)
    }
    private fun dpToPx(dp: Float): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
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