package com.example.todolistapp.Log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.todolistapp.HomePageActivity;
import com.example.todolistapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class Log_in_upActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextView singUp,forgetPassword;
    TextInputEditText username, passwordLogin;
    AppCompatButton login;
    private ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        _FindId();
        _Listener();

    }

    public void _FindId() {
        singUp = findViewById(R.id.SingUp);
        username = findViewById(R.id.username);
        passwordLogin = findViewById(R.id.passwordLogin);
        login = findViewById(R.id.loginto);
        forgetPassword = findViewById(R.id.forgetPassword);

    }

    public void _Listener() {
        singUp.setOnClickListener(v -> {
            Intent intent = new Intent(Log_in_upActivity.this,SignUpActivity.class);
            startActivity(intent);

        });

        login.setOnClickListener(this::onClick);
        forgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(Log_in_upActivity.this,ForgetPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void onClick(View view) {
        String usernameText = Objects.requireNonNull(username.getText()).toString();
        String passwordText = Objects.requireNonNull(passwordLogin.getText()).toString();

        if (usernameText.isEmpty()) {

            Toast.makeText(Log_in_upActivity.this, "Username Cant Be Empty !", Toast.LENGTH_SHORT).show();

            username.setError("Can't be empty");

        } else if (passwordText.isEmpty()) {
            Toast.makeText(Log_in_upActivity.this, "Password Cant Be Empty !", Toast.LENGTH_SHORT).show();
            passwordLogin.setError("Can't be empty");
        } else if (passwordText.length() < 6) {

            Toast.makeText(Log_in_upActivity.this, "Password Must Be More Than 6 Numbers", Toast.LENGTH_SHORT).show();
            passwordLogin.setError("Can't be less than 6 numbers");

        } else {
            progressDialog.setMessage("Login In...!");
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(usernameText, passwordText).
                    addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(Log_in_upActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
//                            Log.d("TAG", "onComplete: successful");
                            sharedPreferences =getSharedPreferences("logged_user",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String user_id = sharedPreferences.getString("user_id", "");
                            firestore.collection("users").addSnapshotListener((value, error) -> {
                                if(error !=null){
                                    Log.d("TAG", "onEvent: error");
                                }
                                for (DocumentChange documentChange : value.getDocumentChanges()){

                                    String fullName1 = documentChange.getDocument().getData().get("DisplayName").toString();
                                    String phoneNumber1 = documentChange.getDocument().getData().get("PhoneNumber").toString();
                                    Log.d("TAG", "onEvent: "+ fullName1 +"  "+ phoneNumber1);
                                    editor.putString("user_fullName", fullName1);
                                    editor.putString("user_phone", phoneNumber1);
                                    editor.putString("user_id", firebaseAuth.getCurrentUser().getUid());
                                    editor.putString("user_email", usernameText);
                                    editor.apply();

                                }
                            });


                            startActivity(new Intent(Log_in_upActivity.this, HomePageActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Log_in_upActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "onComplete: Failed");
                        }
                    });
        }
    }
}