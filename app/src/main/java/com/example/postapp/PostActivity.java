package com.example.postapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {
    private ImageButton mSelectImage;
    private static final int GALLERY_REQUEST = 1;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitBtn;
    private Uri mImageUri = null;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase;
  private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_post );
        mSelectImage=(ImageButton) findViewById(R.id.imageSelect);
        mPostTitle=(EditText) findViewById(R.id.titleField);
        mPostDesc=(EditText) findViewById(R.id.descField);
        mSubmitBtn=(Button) findViewById(R.id.submitBtn);
        mProgress=new ProgressDialog(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mSelectImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent( Intent.ACTION_GET_CONTENT );
                galleryIntent.setType( "image/*" );
                startActivityForResult( galleryIntent, GALLERY_REQUEST );
            }
        } );
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }
    private void startPosting() {
        mProgress.setMessage( "Posting to Blog");
        mProgress.show();
        String title_val=mPostTitle.getText().toString().trim();
        String  desc_val=mPostDesc.getText().toString().trim();
        if(!TextUtils.isEmpty(title_val)&&!TextUtils.isEmpty(desc_val)&&mImageUri!=null)
        {     StorageReference filepath=mStorage.child("Blog_Images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   Uri downloadUri=taskSnapshot.getUploadSessionUri();
                    DatabaseReference newPost=mDatabase.push();
                    newPost.child("title").setValue(title_val);
                    newPost.child("desc").setValue(desc_val);
                    newPost.child("image").setValue(downloadUri.toString());
                 mProgress.dismiss();
                   startActivity(new Intent(PostActivity.this,MainActivity.class));
                        }
                    });

                }
            }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            mSelectImage.setImageURI( mImageUri );
        }
    }
}