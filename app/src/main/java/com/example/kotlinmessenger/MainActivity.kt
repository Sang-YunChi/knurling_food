package com.example.kotlinmessenger

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_text_view.setOnClickListener {
            Log.d("MainActivity", "로그인 페이지로 넘어가려 시도")

            //login activity 연결
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        selectphoto_button_register.setOnClickListener {
            Log.d("MainAcitivity", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0, )
        }
        register_button_food.setOnClickListener{
            val intent=Intent(this,AddPhotoActivity::class.java)
            startActivity(intent)
        }
    }
    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            // proceed and check waht the selected image was..
            Log.d("MainActivity", "Photo was welected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }

    }

//  회원가입을 진행
    private fun performRegister(){

        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "이메일, 패스워드를 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MainActivity", "Email is: "+email)
        Log.d("MainActivity", "Password: $password")

        //Firebase Authentication를 활용한 유저 정보 생성(email/pw 활용)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.d("Main", "Creation Failed")
                    return@addOnCompleteListener
                }

                //else if successful
                Log.d("Main", "Successfully created user with uid: ${it.result?.user?.uid}")
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener{
                Log.d("Main", "Failed to create userL ${it.message}")
                Toast.makeText(this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
    }
    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null) return


        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("MainActivity", "Successfully uploaded image: ${it.metadata?.path}")

            ref.downloadUrl.addOnSuccessListener {

                Log.d("MainActivity", "File: Location: $it")
                saveUserToFirebaseDatabase(it.toString())
            }
        }
            .addOnFailureListener{

            }

    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val db = Firebase.firestore
        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)
        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally we saved the user to Firebase Database")
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "We Failed to save the user to Firebase Database")
            }
    }
}

data class User(
    val uid: String,
    val username: String,
    val profileImageUrl: String
)
//    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
//        val uid = FirebaseAuth.getInstance().uid ?: ""
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)
//
//        ref.setValue(user).addOnSuccessListener {
//            Log.d("MainActivity", "Finally we saved the user to Firebase Database")
//        }
//    }
//}
//
//class User(val uid: String, val username: String, val profileImageUrl: String)