package com.example.electonexus_project

import android.annotation.SuppressLint
import android.content.Intent
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
import org.checkerframework.checker.initialization.qual.FBCBottom
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

class voter_cast_vote : ComponentActivity() {

    private  var lastTextViewId: Int? = R.id.EIDdii


    private lateinit var fbrefelc : DatabaseReference
    private lateinit var fbrefacc : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val Vun: String = getCredentialsFile()

        setContentView(R.layout.activity_cast_vote)

        fbrefelc =
            FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Election")
        fbrefacc =
            FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Account")

        //ACTIVE ELECTIONS DISPLAY CODE
        fbrefacc.child("$Vun/ElectionRequest").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(elc in snapshot.children){
                        val eid = elc.key.toString()
                        fbrefelc.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){
                                    for(el in snapshot.children){
                                        val eidd = el.key.toString()
                                        if(eidd == eid){
                                            val status = el.child("Status").getValue(String::class.java)
                                            if(status == "Active"){
                                                val name = el.child("ename").getValue(String::class.java)
                                                createTextView(eid,name)
                                            }
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@voter_cast_vote,error.message,Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
                else{
                    Toast.makeText(this@voter_cast_vote,"No Elections are currently Active",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@voter_cast_vote,error.message,Toast.LENGTH_SHORT).show()
            }
        })



        val searchbtn: Button = findViewById(R.id.Vcv_search)
        searchbtn.setOnClickListener {

            val eidet : EditText = findViewById(R.id.Vcv_eid)
            val eid = eidet.text.toString()
            fbrefelc.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var isEIDActive = false
                        for(elc in snapshot.children){
                            val eidd = elc.key.toString()
                            val estat = elc.child("Status").value.toString()
                            val votestat = elc.child("Voter/$Vun/votestat").value
                            if(eid == eidd) {
                                isEIDActive = true
                                Toast.makeText(this@voter_cast_vote,"EID Found",Toast.LENGTH_SHORT).show()
                                if (estat == "Active"&&(votestat == false)) {
                                    createintent()
                                }
                                else{
                                    if(estat != "Active"){
                                        Toast.makeText(this@voter_cast_vote,"Election hasn't started yet",Toast.LENGTH_SHORT).show()
                                    }
                                    else if(votestat == true){
                                        Toast.makeText(this@voter_cast_vote,"You have already voted",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                        }
                        if(!isEIDActive) {
                            Toast.makeText(this@voter_cast_vote,"EID not Found",Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(this@voter_cast_vote,"EID not Found",Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@voter_cast_vote,error.message,Toast.LENGTH_SHORT).show()
                }
            })

        }


    }

    private fun createintent(){

        var eidtovp : EditText = findViewById(R.id.Vcv_eid)
        val eid : String = eidtovp.text.toString()
        Toast.makeText(this,eid,Toast.LENGTH_SHORT).show()
        val intent = Intent(this, voter_voting_portal::class.java).apply {
            putExtra("Eid",eid ) // Put the string data as an extra
        }
        startActivity(intent)
    }
    @SuppressLint("RestrictedApi")
    private fun createTextView(eid : String,name:String? ){
        val containerLayout: ConstraintLayout = findViewById(R.id.containercastvote)
        val newTextView = TextView(this).apply {
            id = View.generateViewId()
            text = eid
            textSize = 18f
            setPadding(16, 16, 16, 16)

            typeface = Typeface.create(typeface, Typeface.BOLD)

            transformationMethod = AllCapsTransformationMethod(context)
        }
        val newTextView1 = TextView(this).apply {
            id = View.generateViewId()
            text = name
            textSize = 18f
            setPadding(16, 16, 16, 16)

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