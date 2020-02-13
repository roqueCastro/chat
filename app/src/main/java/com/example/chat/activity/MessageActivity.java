package com.example.chat.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.chat.Adapter.MessageAdapter;
import com.example.chat.R;
import com.example.chat.model.Chat;
import com.example.chat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    Intent intent;

    ImageButton send;
    EditText message;

    MessageAdapter messageAdapter;
    List<Chat> chats;

    RecyclerView recyclerView;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar_m);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MessageActivity.this, Main2Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        profile_image = findViewById(R.id.profile_image_m);
        username = findViewById(R.id.textViewusername_m);
        message = findViewById(R.id.text_send);
        send = findViewById(R.id.btn_imageview_send);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msj = message.getText().toString();
                if (!msj.equals("")){
                    sendMessage(firebaseUser.getUid(), userid, msj);
                    message.setText("");
                }else {
                    Toast.makeText(getApplicationContext(), "Tu mensaje esta vacio.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**/

                User user = dataSnapshot.getValue(User.class);

                username.setText(user.getUsername());

                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMessage(firebaseUser.getUid(), userid, user.getImageURL());

                /**/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /**/
        /**/
        recyclerView = findViewById(R.id.recycler_view_m);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        seenMessage(userid);
    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**/

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReciver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }

                /**/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*ENVIO DE MENSAJE*/
    private  void  sendMessage(String sender, final String reciver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("reciver", reciver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(reciver);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**/
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(reciver);
                }
                /**/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /*LEER MENSAJES*/
    private  void readMessage(final String myid, final String userid, final String imagenurl){
        chats = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**/
                chats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReciver().equals(myid)
                            && chat.getSender().equals(userid)
                            || chat.getReciver().equals(userid)
                            && chat.getSender().equals(myid)
                    ){

                        chats.add(chat);
                    }

                    messageAdapter = new MessageAdapter(getApplicationContext(), chats, imagenurl);
                    recyclerView.setAdapter(messageAdapter);
                }
                /**/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*STATUD*/
    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    /*APP EN EJECUCCION*/
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    /*APP EN PAUSA O FUERA DE ELLA*/

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
    }

}
