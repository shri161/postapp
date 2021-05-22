package com.example.postapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {
 private RecyclerView mBlogList;
 private DatabaseReference mDatabase;
 private FirebaseAuth mAuth;
 private DatabaseReference mDatabaseLike;
 private FirebaseAuth.AuthStateListener mAuthListener;
 private DatabaseReference mDatabaseUsers;
 private FirebaseUser mCurrentUser;
 private Boolean mProcessLike=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Blog" );
        mDatabase.keepSynced(true);
        mAuth=FirebaseAuth.getInstance();
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUsers.keepSynced(true);

        mCurrentUser=mAuth.getCurrentUser();
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("likes");
        mDatabaseLike.keepSynced( true );
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };
        setContentView( R.layout.activity_main );
        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener( mAuthListener );

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>( Blog.class, R.layout.blog_row, BlogViewHolder.class, mDatabase ) {
            @Override
            protected void populateViewHolder(BlogViewHolder blogViewHolder, Blog blog, int i) {
                final String post_key = getRef( i ).toString();
                blogViewHolder.setTitle( blog.getTitle() );
                blogViewHolder.setDesc( blog.getDesc() );
                blogViewHolder.setUsername( blog.getUsername() );
                blogViewHolder.setImage( getApplicationContext(), blog.getImage() );
                blogViewHolder.mLikeBtn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessLike=true;
                        mDatabaseLike.addValueEventListener( new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(mProcessLike) {
                                    if (snapshot.child( post_key ).hasChild( mAuth.getCurrentUser().getUid())) {
                                        mDatabaseLike.child( post_key ).child( mAuth.getCurrentUser().getUid() ).removeValue();
                                        mProcessLike = false;
                                    } else {
                                        mDatabaseLike.child( post_key ).child( mAuth.getCurrentUser().getUid() );
                                        mProcessLike = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        } );

                    }

                } );
            }


        };
                mBlogList.setAdapter( firebaseRecyclerAdapter );

            }


            public static class BlogViewHolder extends RecyclerView.ViewHolder {
                View mView;
                ImageView mLikeBtn;
                DatabaseReference mDatabaseLike;
                FirebaseAuth mAuth;

                public BlogViewHolder(@NonNull View itemView) {
                    super( itemView );
                    mView = itemView;
                    mLikeBtn = (ImageView) mView.findViewById( R.id.likeBtn );
                }

                public void setTitle(String title) {
                    TextView post_title = (TextView) mView.findViewById( R.id.post_title );
                    post_title.setText( title );
                }

                public void setDesc(String desc) {
                    TextView post_desc = (TextView) mView.findViewById( R.id.post_desc );
                    post_desc.setText( desc );
                }

                public void setImage(final Context ctx, final String image) {
                    final ImageView post_image = (ImageView) mView.findViewById( R.id.post_image );
                    Picasso.with( ctx ).load( image ).into( post_image );
                }

                public void setUsername(String username) {
                    TextView user_name = (TextView) mView.findViewById( R.id.username );
                    user_name.setText( username );
                }

            }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate( R.menu.main_menu, menu );
                return super.onCreateOptionsMenu( menu );

            }

            @Override
            public boolean onOptionsItemSelected(@NonNull MenuItem item){

            if (item.getItemId() == R.id.action_add) {
                startActivity( new Intent( MainActivity.this, PostActivity.class ) );
            }
            if (item.getItemId() == R.id.logout) {
                mAuth.signOut();
            }
            return super.onOptionsItemSelected( item );
        }
}