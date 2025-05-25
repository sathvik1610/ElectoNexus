package com.example.electonexus_project

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.text.AllCapsTransformationMethod
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

class Voterrequest : ComponentActivity() {

    private var lastTextViewId: Int? = R.id.textViewvr

    private lateinit var eid : String

    private lateinit var fbref : DatabaseReference
    private lateinit var fbrefacc : DatabaseReference
    private var blist = mutableListOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voterrequest)

        eid = getCredentialsFile()

        fbref = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Election/$eid/Voter")
        fbrefacc = FirebaseDatabase.getInstance("https://electonexusmain-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Account")
        fbinitialise()

    }
    private fun fbinitialise(){
        fbref.get().addOnSuccessListener { snapshot ->
            if(snapshot.exists()){
                for(voter in snapshot.children){
                    if(voter.key == "Voter"){
                        continue
                    }
                    createView(voter)
                }
            }
        }.addOnFailureListener{error ->
            Toast.makeText(this@Voterrequest,"In addValue ${error.message}",Toast.LENGTH_SHORT).show()

        }
    }
    @SuppressLint("RestrictedApi")
    private fun createView(voter : DataSnapshot){
        val uname = voter.key
        val name = voter.child("name").getValue(String::class.java)
        val stat = voter.child("reqstat").getValue(String::class.java)



        if(stat != "ReqSent"){
            return
        }

        @SuppressLint("RestrictedApi")
            val containerLayout: ConstraintLayout = findViewById(R.id.containervoterrequestlist)
            val newTextView = TextView(this).apply {
                id = View.generateViewId()
                text = name
                textSize = 18f
                setPadding(16, 16, 16, 16)
                setTextColor(Color.BLACK)

                typeface = Typeface.create(typeface, Typeface.BOLD)

                transformationMethod = AllCapsTransformationMethod(context)
            }
            val yButton = Button(this).apply {
                id = View.generateViewId()
                text = "Yes"
                tag = "ybtn+$uname"
                textSize = 18f
                setPadding(16, 16, 16, 16)
                setBackgroundResource(R.drawable.input_button)


                setTextColor(ContextCompat.getColor(context, R.color.white))

            }
            val nButton = Button(this).apply {
                id = View.generateViewId()
                text = "No"
                tag = "nbtn+$uname"
                textSize = 18f
                setPadding(16, 16, 16, 16)
                setBackgroundResource(R.drawable.input_button)


                setTextColor(ContextCompat.getColor(context, R.color.white))

            }
            yButton.setOnClickListener {
                val updatedstatus = "Accepted"
                fbref.child("$uname/reqstat").setValue(updatedstatus)
                fbref.child("$uname/votestat").setValue(false)
                fbrefacc.child("$uname/ElectionRequest/$eid/reqstat").setValue(updatedstatus)
                fbrefacc.child("$uname/ElectionRequest/$eid/votestat").setValue(false)
                Toast.makeText(this, "$name is Accepted", Toast.LENGTH_SHORT).show()
                delbutton()
                fbinitialise()
            }
            nButton.setOnClickListener {
                val updatedstatus = "Rejected"
                fbrefacc.child("$uname/ElectionRequest/$eid/reqstat").setValue(updatedstatus)
                fbref.child("$uname/reqstat").setValue(updatedstatus)
                Toast.makeText(this, "$name is Rejected", Toast.LENGTH_SHORT).show()
                delbutton()
                fbinitialise()
            }
            containerLayout.addView(newTextView)
            containerLayout.addView(yButton)
            containerLayout.addView(nButton)

            applyConstraintsToView(containerLayout, newTextView,yButton,nButton)

            lastTextViewId = newTextView.id




    }
    private fun delbutton(){
        val layout = findViewById<ViewGroup>(R.id.containervoterrequestlist)
        for(i in layout.childCount -1 downTo  1){
            val child = layout.getChildAt(i)
            if(child.id != R.id.textViewvr){}
            layout.removeViewAt(i)
        }
        lastTextViewId = R.id.textViewvr
    }
    private fun applyConstraintsToView(parent: ConstraintLayout, textView: TextView,ybutton : Button,nbutton: Button) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(parent)


        constraintSet.connect(textView.id, ConstraintSet.TOP, lastTextViewId!!, ConstraintSet.BOTTOM, 16)
        constraintSet.connect(ybutton.id, ConstraintSet.TOP, lastTextViewId!!, ConstraintSet.BOTTOM, 16)
        constraintSet.connect(nbutton.id, ConstraintSet.TOP, lastTextViewId!!, ConstraintSet.BOTTOM, 16)


        constraintSet.connect(textView.id, ConstraintSet.START, parent.id, ConstraintSet.START,dpToPx(20f) )
        constraintSet.connect(ybutton.id, ConstraintSet.START, textView.id, ConstraintSet.START,dpToPx(130f) )
        constraintSet.connect(nbutton.id, ConstraintSet.START, ybutton.id, ConstraintSet.END,dpToPx(10f) )



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
