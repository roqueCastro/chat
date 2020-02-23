package com.example.chat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;



public class RegistroActivity extends AppCompatActivity {


    MaterialEditText username, email, password;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registro de usuario");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        btn_register = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_username)|| TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) ){
                    Toast.makeText(RegistroActivity.this, "LLenar todos los campos.", Toast.LENGTH_SHORT).show();
                }else if (txt_password.length() < 6) {
                    Toast.makeText(RegistroActivity.this, "Debe tener minimo 7 caracteres.", Toast.LENGTH_SHORT).show();
                }else {
                    register(txt_username, txt_email, txt_password);
                }
                //
            }
        });
    }

    private void register(final String username, String email, String password){

        //Cargando
        final ProgressDialog pd = new ProgressDialog(RegistroActivity.this);
        pd.setMessage("Registrando..");
        pd.show();
        btn_register.setVisibility(View.GONE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        //
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete( Task<Void> task) {
                                    //
                                    if (task.isSuccessful()){

                                        pd.dismiss();
                                        btn_register.setVisibility(View.VISIBLE);

                                        Intent intent = new Intent(RegistroActivity.this, Main2Activity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                    //
                                }
                            });
                        }else {
                            Toast.makeText(RegistroActivity.this, "No se pudo registrar.", Toast.LENGTH_SHORT).show();

                            pd.dismiss();
                            btn_register.setVisibility(View.VISIBLE);
                        }
                        //
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistroActivity.this, "ERROR: \n" + e.toString(), Toast.LENGTH_SHORT).show();

                pd.dismiss();
                btn_register.setVisibility(View.VISIBLE);
            }
        });
    }
}
