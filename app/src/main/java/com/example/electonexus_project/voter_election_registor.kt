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
import androidx.compose.animation.core.snap
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

class voter_election_registor : ComponentActivity() {

    private var lastTextViewId: Int? = R.id.RS
    private lateinit var eidtext : TextView

    private var ename : String =""


    private lateinit var fbrefelc : DatabaseReference
    private lateinit var fbrefacc : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_election_registor)

        val Vun: String = getCredentialsFile()

        val eidbutton : Button = findViewById(R.id.Veidbutton)

        fbrefacc = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Account")
        fbrefelc = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Election")

            fbrefacc.child("$Vun/ElectionRequest")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (voter in snapshot.children) {
                                val eidd = voter.key.toString()
                                if(eidd == "11") {
                                    continue
                                }
//                        Toast.makeText(this@voter_election_registor,"Eid = $eidd",Toast.LENGTH_SHORT).show()
                                val enamee = voter.child("ename").getValue().toString()
                                val stat = voter.child("reqstat").getValue().toString()
                                createTextView(eidd, enamee, stat)
                            }
                        } else {
                            Toast.makeText(
                                this@voter_election_registor,
                                "Not registered for any election",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@voter_election_registor,
                            error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        eidbutton.setOnClickListener {

            Intent(this, voter_election_registor::class.java).also{startActivity(it) }
            finish()

            val eidtext : EditText = findViewById(R.id.VeidText)
            val eid : String = eidtext.text.toString()
            fbrefelc = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Election")

            fbrefelc.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var isEIDFound = false
                    if(snapshot.exists()){
                        var a =""
                        for(election in snapshot.children) {
                            val eidn = election.key
                            if(eid == "11"){
                                continue
                            }
                            if(eid.equals(eidn)) {
                                isEIDFound = true
                                var isUserInE = false
                                ename = election.child("ename").getValue().toString()
                                Toast.makeText(this@voter_election_registor, "Election ID found", Toast.LENGTH_SHORT).show()
                                fbrefelc.child("$eid/Candidate").addListenerForSingleValueEvent(object :ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            for(voter in snapshot.children){
                                                val vk = voter.key.toString()
                                                if(Vun == vk) {
                                                    isUserInE = true
                                                    Toast.makeText(this@voter_election_registor,"Already enrolled in election",Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            if(isUserInE==false){
                                                fbrefacc.addListenerForSingleValueEvent(object :ValueEventListener{
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        if(snapshot.exists()){
                                                            for(user in snapshot.children){
                                                                if(Vun == user.key.toString()){
                                                                    a = user.child("name").value.toString()
                                                                    fbrefelc.child("$eid/Voter/$Vun/name").setValue(a)

                                                                    fbrefacc.child("$Vun/ElectionRequest/$eid/ename").setValue(ename)
                                                                    val status = user.child("ElectionRequest/$eid/restat").getValue().toString()
                                                                    if(status != "ReqSent"){
                                                                        fbrefelc.child("$eid/Voter/$Vun/reqstat").setValue("ReqSent")
                                                                        fbrefacc.child("$Vun/ElectionRequest/$eid/reqstat").setValue("ReqSent")
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    override fun onCancelled(error: DatabaseError) {
                                                        Toast.makeText(this@voter_election_registor,error.message,Toast.LENGTH_SHORT).show()
                                                    }
                                                })
                                            }
                                        }
                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(this@voter_election_registor,error.message,Toast.LENGTH_SHORT).show()
                                    }
                                })
                                break
                            }
                        }
                        if (isEIDFound==false){
                            Toast.makeText(
                                this@voter_election_registor,
                                "Invalid Election ID",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        isEIDFound = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@voter_election_registor,error.message,Toast.LENGTH_SHORT).show()
                }
            })

//            Intent(this, voter_election_registor::class.java).also{startActivity(it) }
//            finish()

        }

    }
    @SuppressLint("RestrictedApi")
    private fun createTextView(eid : String, ename: String,estatus:String){
        val containerLayout: ConstraintLayout = findViewById(R.id.containerelectionregistor)
        val newTextView = TextView(this).apply {
            id = View.generateViewId()
            text = eid
            textSize = 18f
            setPadding(16, 16, 16, 16)
            setTextColor(Color.BLACK)

            typeface = Typeface.create(typeface, Typeface.BOLD)

            transformationMethod = AllCapsTransformationMethod(context)
        }
        val newTextView1 = TextView(this).apply {
            id = View.generateViewId()
            text = ename
            textSize = 18f
            setPadding(16, 16, 16, 16)
            setTextColor(Color.BLACK)

            typeface = Typeface.create(typeface, Typeface.BOLD)

            transformationMethod = AllCapsTransformationMethod(context)
        }
        val newTextView2 = TextView(this).apply {
            id = View.generateViewId()
            text = estatus
            textSize = 18f
            setPadding(16, 16, 16, 16)
            setTextColor(Color.BLACK)

            typeface = Typeface.create(typeface, Typeface.BOLD)

            transformationMethod = AllCapsTransformationMethod(context)
        }

        containerLayout.addView(newTextView)
        containerLayout.addView(newTextView1)
        containerLayout.addView(newTextView2)

        applyConstraintsToView(containerLayout, newTextView,newTextView1,newTextView2)

        lastTextViewId = newTextView.id
    }
    private fun applyConstraintsToView(parent: ConstraintLayout, textView: TextView,textView1: TextView,textView2: TextView) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(parent)


        constraintSet.connect(textView.id, ConstraintSet.TOP, lastTextViewId!!, ConstraintSet.BOTTOM, 16)

        constraintSet.connect(textView.id, ConstraintSet.START, parent.id, ConstraintSet.START,dpToPx(20f) )

        constraintSet.connect(textView1.id, ConstraintSet.TOP, lastTextViewId!!, ConstraintSet.BOTTOM, 16)

        constraintSet.connect(textView1.id, ConstraintSet.START, textView.id, ConstraintSet.START,dpToPx(120f) )

        constraintSet.connect(textView2.id, ConstraintSet.TOP, lastTextViewId!!, ConstraintSet.BOTTOM, 16)

        constraintSet.connect(textView2.id, ConstraintSet.START, textView1.id, ConstraintSet.START,dpToPx(120f) )




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