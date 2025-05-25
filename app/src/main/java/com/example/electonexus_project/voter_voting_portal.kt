package com.example.electonexus_project

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.text.AllCapsTransformationMethod
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

class voter_voting_portal : ComponentActivity() {

    private var lastTextViewId: Int? = R.id.USERNAMEVPVP
    private lateinit var fbrefelc : DatabaseReference
    private lateinit var fbrefacc : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_voting_portal)

        val Vun: String = getCredentialsFile()

        val eid = intent.getStringExtra("Eid")

        fbrefelc = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Election/$eid")

        fbrefelc.child("Candidate").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(candidate in snapshot.children){
                        val un = candidate.key.toString()
                        val name = candidate.child("name").getValue(String::class.java)
                        val status = candidate.child("reqstat").getValue(String::class.java)
                        if(status == "Accepted"){
                            createTextView(un,name)
                        }
                    }
                }
                else{
                    Toast.makeText(this@voter_voting_portal,"No snapshot",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@voter_voting_portal,error.message,Toast.LENGTH_SHORT).show()
            }
        })

        val gobutton : Button = findViewById(R.id.Vp_vote)

        gobutton.setOnClickListener {
            val cidet : EditText = findViewById(R.id.Vp_cid)
            val cid = cidet.text.toString()
            fbrefelc = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Election/$eid/Candidate")
            fbrefelc.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var isUnValid = false
                        for(candidate in snapshot.children){
                            val un = candidate.key.toString()
                            if(cid == un){
                                isUnValid=true
                                fbrefacc = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Account/$Vun/ElectionRequest/$eid/votestat")
                                fbrefacc.setValue(true)
                                fbrefacc = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Election/$eid/Voter/$Vun/votestat")
                                fbrefacc.setValue(true)
                                var numvote = candidate.child("numvot").getValue(Int::class.java)
                                numvote = numvote!! + 1
                                fbrefelc.child("$cid/numvot").setValue(numvote)
                                Toast.makeText(this@voter_voting_portal,"Successfully Voted for $un",Toast.LENGTH_SHORT).show()
                                ipp()


                            }
                        }
                        if(!isUnValid){
                            Toast.makeText(this@voter_voting_portal,"Invalid Candidate Username Found",Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@voter_voting_portal,error.message,Toast.LENGTH_SHORT).show()
                }
            })
        }



    }
    private fun ipp(){

        val intentt = Intent(this, voter_dashboard::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intentt)
    }
    @SuppressLint("RestrictedApi")
    private fun createTextView(cn : String , cun : String?){
        val containerLayout: ConstraintLayout = findViewById(R.id.containervotingportal)
        val newTextView = TextView(this).apply {
            id = View.generateViewId()
            text = cn
            textSize = 18f
            setPadding(16, 16, 16, 16)
            setTextColor(Color.BLACK)

            typeface = Typeface.create(typeface, Typeface.BOLD)

            //transformationMethod = AllCapsTransformationMethod(context)
        }
        val newTextView1 = TextView(this).apply {
            id = View.generateViewId()
            text = cun
            textSize = 18f
            setPadding(16, 16, 16, 16)
            setTextColor(Color.BLACK)

            typeface = Typeface.create(typeface, Typeface.BOLD)

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

        constraintSet.connect(textView.id, ConstraintSet.START, parent.id, ConstraintSet.START,dpToPx(60f) )

        constraintSet.connect(textView1.id, ConstraintSet.TOP, lastTextViewId!!, ConstraintSet.BOTTOM, 16)

        constraintSet.connect(textView1.id, ConstraintSet.START, textView.id, ConstraintSet.START,dpToPx(180f) )



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
            //var Acctype: String? = null

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                line?.let {
                    when {
                        it.startsWith("Username:") -> {
                            username = it.substringAfter("Username:").trim()
                            return username!!
                        }
                        it.startsWith("Acctype:") -> {
                            // Acctype = it.substringAfter("Acctype:").trim()
                        }
                    }
                }
            }

            bufferedReader.close()



        } catch (e: Exception) {
            Toast.makeText(this, "No credentials found", Toast.LENGTH_SHORT).show()
        }
        return ""
    }
}