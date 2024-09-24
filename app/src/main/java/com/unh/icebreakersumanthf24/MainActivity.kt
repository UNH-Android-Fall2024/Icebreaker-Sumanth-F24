package com.unh.icebreakersumanthf24

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unh.icebreakersumanthf24.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val className = "Android-Fall24"
    private val db = Firebase.firestore
    private var TAG = "IcebreakerF24"
    private var questionBank: MutableList<Questions>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getQuestionsFromFirebase()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSetRandomQuestion.setOnClickListener {
            binding.txtQuestion.text = questionBank!!.random().text

        }

        binding.btnSubmit.setOnClickListener {
            binding.txtQuestion.text = ""

            writeStudentToFirebase()
        }
    }

    private fun getQuestionsFromFirebase(){
        db.collection("Questions")
            .get()
            .addOnSuccessListener { result ->
                questionBank = mutableListOf()
                for(document in result) {
                    val question = document.toObject(Questions::class.java)
                    questionBank!!.add(question)
                    Log.d(TAG,"$question")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error", exception)
            }
    }

    private fun writeStudentToFirebase() {
        val firstName = binding.txtFirstName
        val lastName  = binding.txtLastName
        val prefName  = binding.txtPrefName
        val answer    = binding.txtAnswer

        Log.d(TAG, "Variables: ${firstName.text} $lastName $prefName $answer")

        val student = hashMapOf(
            "firstname" to firstName.text.toString(),
            "lastname"  to lastName.text.toString(),
            "prefname"  to prefName.text.toString(),
            "answer"    to answer.text.toString(),
            "class"     to className,
            "question"  to binding.txtQuestion.text.toString()
        )

        db.collection("students")
            .add(student)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Doocument written successfully with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding documents", exception)
            }

        firstName.setText("")
        lastName.setText("")
        prefName.setText("")
        answer.setText("")
    }

}