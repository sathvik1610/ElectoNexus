package com.example.electonexus_project

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
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

class voter_election_results : ComponentActivity() {
    private var lastTextViewId: Int? = R.id.FnameRR
    private lateinit var fbrefelc : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_voter_election_results)



        val button : Button = findViewById(R.id.Vr_check)

        fbrefelc = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Election/")

        button.setOnClickListener {
            val eidedittext : TextView = findViewById(R.id.Vr_eid)
            val eid = eidedittext.text.toString()
            fbrefelc.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (elc in snapshot.children) {
                            if (elc.key.toString() == eid) {
                                val status = elc.child("Status").getValue(String::class.java)
                                if (status == "Election Ended") {
                                    Toast.makeText(
                                        this@voter_election_results,
                                        "In Ended",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    fbrefelc.child("$eid/Candidate")
                                        .addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    for (candidate in snapshot.children) {
                                                        if (candidate.key.toString() == "uunn") {
                                                            continue
                                                        }
                                                        val name = candidate.child("name")
                                                            .getValue(String::class.java)
                                                        val numvote =
                                                            candidate.child("numvot").value.toString()
                                                        createTextView(name!!, numvote)
                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        this@voter_election_results,
                                                        "No 2nd snapshot found",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Toast.makeText(
                                                    this@voter_election_results,
                                                    error.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        })
                                }
                                else{
                                    Toast.makeText(this@voter_election_results,"Election has not ended",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@voter_election_results,
                            "No 1st snapshot found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@voter_election_results, error.message, Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    private fun createTextView(name: String, numvote : String) {
        val containerLayout: ConstraintLayout = findViewById(R.id.containervelectionresults)
        val newTextView = TextView(this).apply {
            id = View.generateViewId()
            text = name
            textSize = 18f
            setPadding(16, 16, 16, 16)
        }
        val newTextView1 = TextView(this).apply {
            id = View.generateViewId()
            text = numvote
            textSize = 18f
            setPadding(16, 16, 16, 16)
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