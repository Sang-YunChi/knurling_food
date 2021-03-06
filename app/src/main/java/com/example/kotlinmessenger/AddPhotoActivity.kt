package com.example.kotlinmessenger

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM=0
    var storage : FirebaseStorage?=null
    var photoUri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        storage= FirebaseStorage.getInstance()

        addphoto_image.setOnClickListener{
            var permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1) }

            else {
                var photoPickerIntent=Intent(Intent.ACTION_PICK)
                photoPickerIntent.type="image/*"
                startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

                addphoto_btn_upload.setOnClickListener{
                    contentUpload();
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_FROM_ALBUM){
            if(resultCode== Activity.RESULT_OK){
                photoUri=data?.data
                addphoto_image.setImageURI(photoUri)
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
                val bitmapDrawable = BitmapDrawable(bitmap)
                addphoto_image.setBackgroundDrawable(bitmapDrawable)
                Log.d(this.toString(),"??????");
            }else{
                Log.d(this.toString(),"?????????");
                finish();
            }
        }
    }





    fun contentUpload(){
        var timestamp=SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName= "IMAGE_"+timestamp+"_.png"
        var storageRef=storage?.reference?.child("images");

        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this,"????????? ??????",Toast.LENGTH_SHORT).show()
        }
    }
}