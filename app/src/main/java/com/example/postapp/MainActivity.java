package com.example.postapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
 private RecyclerView mBlogList;
 private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Blog" );
        mDatabase.keepSynced(true);
        setContentView( R.layout.activity_main );
        mBlogList = (RecyclerView) findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

         FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(Blog.class,R.layout.blog_row,BlogViewHolder.class,mDatabase) {
             @Override
             protected void populateViewHolder(BlogViewHolder blogViewHolder, Blog blog, int i) {
                 blogViewHolder.setTitle( blog.getTitle() );
                 blogViewHolder.setDesc( blog.getDesc() );

             }

        };
         mBlogList.setAdapter(firebaseRecyclerAdapter);

    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView mLikeBtn;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;
        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            mDatabaseLike= FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth=FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced( true);
        }
        public void setTitle(String title)
        {
            TextView post_title=(TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setDesc(String desc)
        {
            TextView post_desc=(TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.main_menu, menu );
        return super.onCreateOptionsMenu( menu );

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_add) {
          startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        return super.onOptionsItemSelected( item );
    }
}