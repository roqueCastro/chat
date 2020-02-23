package com.example.chat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {

    EditText send_email;
    Button btn_reset;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        send_email = findViewById(R.id.send_email);
        btn_reset = findViewById(R.id.btn_reset);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = send_email.getText().toString();
                if (email.equals("")){
                    Toast.makeText(getApplicationContext(), "Required fields!", Toast.LENGTH_SHORT).show();
                }else{

                    //Cargando
                    final ProgressDialog pd = new ProgressDialog(ResetActivity.this);
                    pd.setMessage("Autenticando..");
                    pd.show();
                    btn_reset.setVisibility(View.GONE);

                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            /**/

                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Please check your Email", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ResetActivity.this, LoginActivity.class);
                                startActivity(intent);

                                pd.dismiss();
                                btn_reset.setVisibility(View.VISIBLE);

                            }else {
                                String error = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

                                pd.dismiss();
                                btn_reset.setVisibility(View.VISIBLE);
                            }

                            /**/
                        }
                    });
                }
            }
        });

    }
}
