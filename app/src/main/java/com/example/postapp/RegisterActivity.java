package com.example.postapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mRegisterBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    private static final String TAG=RegisterActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );
        mNameField=(EditText) findViewById(R.id.name);
        mEmailField=(EditText) findViewById(R.id.email);
        mPasswordField=(EditText) findViewById(R.id.password);
        mRegisterBtn=(Button) findViewById(R.id.button);
        mProgress=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users");
        mRegisterBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }


        } );
    }

    private void startRegister() {
        String name=mNameField.getText().toString().trim();
        String email=mEmailField.getText().toString().trim();
        String password=mPasswordField.getText().toString().trim();
        Log.d( TAG,password );
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password))
        {    mProgress.setMessage(password);
             mProgress.show();
             Log.d(TAG,password);
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child( user_id );
                        current_user_db.child( "name" ).setValue( name );
                        current_user_db.child( "image" ).setValue( "default" );
                        mProgress.dismiss();
                        Intent mainIntent = new Intent( RegisterActivity.this, MainActivity.class );
                        mainIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                        startActivity( mainIntent );
                    }
                    else
                    {
                        String message=task.getException().toString();
                        Toast.makeText(RegisterActivity.this,message,Toast.LENGTH_LONG).show();
                        mProgress.dismiss();
                    }
                }
            });
        }
    }
}


