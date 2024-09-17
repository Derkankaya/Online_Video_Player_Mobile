package com.example.videp_v1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private ImageView videpLogo;
    private TextView singupRedirectText;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.loginUserName);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.login_button);
        videpLogo = findViewById(R.id.login_videp_logo);
        singupRedirectText = findViewById(R.id.singupRedirectText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUserName() || !validatePassword()) {
                    return;
                }
                LogInUser();
            }
        });

        singupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SingUpActivity.class);
                startActivity(intent);
            }
        });

        videpLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public Boolean validateUserName() {
        String val = loginEmail.getText().toString().trim();
        if (val.isEmpty()) {
            loginEmail.setError("Email boş bırakılamaz!");
            return false;
        } else {
            loginEmail.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginPassword.getText().toString().trim();
        if (val.isEmpty()) {
            loginPassword.setError("Parola boş bırakılamaz!");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    private void LogInUser() {
        String userEmail = loginEmail.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        currentUser = auth.getCurrentUser();
                        if (currentUser != null) {
                            // Kullanıcı kayıtlı, diğer işlemleri yapabilirsiniz
                            Toast.makeText(LoginActivity.this, "Hoş geldiniz!", Toast.LENGTH_SHORT).show();
                            updateIsLoggedStatus(true);
                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        // Oturum açma işlemi başarısız oldu
                        Log.e("LoginActivity", "Oturum açma hatası: " + task.getException());
                        Toast.makeText(LoginActivity.this, "Oturum açma başarısız!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateIsLoggedStatus(boolean isLogged) {
        // Kullanıcı bilgilerini güncelle
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child("VidepUsers").child(currentUser.getUid());
        userRef.child("isLogged").setValue(isLogged);
    }

}
