package com.example.chat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;



public class LoginActivity extends AppCompatActivity {

    MaterialEditText email, password;
    Button btn_login;

    FirebaseAuth auth;

    TextView forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.lemail);
        password = findViewById(R.id.lpassword);
        btn_login = findViewById(R.id.btn_login);
        forgot_password = findViewById(R.id.forgot_password);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //

                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Cargando..");
                pd.show();
                btn_login.setVisibility(View.GONE);

                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(getApplicationContext(), "Debe llenarse todos los campos correspondientes.", Toast.LENGTH_SHORT).show();

                    pd.dismiss();
                    btn_login.setVisibility(View.VISIBLE);
                }else{
                    auth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete( Task<AuthResult> task) {
                                    //
                                    if (task.isSuccessful()){

                                        pd.dismiss();
                                        btn_login.setVisibility(View.VISIBLE);

                                        Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                                            Toast.makeText(getApplicationContext(), "No existe usuario..!", Toast.LENGTH_SHORT).show();

                                            pd.dismiss();
                                            btn_login.setVisibility(View.VISIBLE);
                                        }else{
                                            Toast.makeText(getApplicationContext(), "Password failds..!", Toast.LENGTH_SHORT).show();

                                            pd.dismiss();
                                            btn_login.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    //
                                }
                            });
                }
                //
            }
        });

    }
}
