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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videp_v1.users.VidepUsersClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SingUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signUpEmail, signUpPassword, signUpFirstName, signUpLastName, signUpUserName, signUpFacultyOfUniversity;
    private Button signUpButton;
    private TextView loginRedirectText;
    private ImageView videpLogo;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        auth = FirebaseAuth.getInstance();

        signUpEmail = findViewById(R.id.singupEmail);  // Değiştirildi
        signUpPassword = findViewById(R.id.singupPassword);  // Değiştirildi
        signUpButton = findViewById(R.id.singup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signUpFirstName = findViewById(R.id.singupFirstName);  // Değiştirildi
        signUpLastName = findViewById(R.id.singupLastName);  // Değiştirildi
        signUpUserName = findViewById(R.id.singupUserName);  // Değiştirildi
        signUpFacultyOfUniversity = findViewById(R.id.singupFacultyOfUniversityName);  // Değiştirildi
        videpLogo = findViewById(R.id.singup_videp_logo);

        videpLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
    }

    private void createUser() {
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        reference = database.getReference("Users").child("VidepUsers");

        String userMail = signUpEmail.getText().toString().trim();
        String password = signUpPassword.getText().toString().trim();
        String firstName = signUpFirstName.getText().toString().trim();
        String lastName = signUpLastName.getText().toString().trim();
        String userName = signUpUserName.getText().toString().trim();
        String facultyOfUniversity = signUpFacultyOfUniversity.getText().toString().trim();
        Boolean isLogged = false;
        VidepUsersClass helperClass = new VidepUsersClass(firstName, lastName, facultyOfUniversity, userName, userMail, password, isLogged);

        auth.createUserWithEmailAndPassword(userMail, password).addOnCompleteListener(SingUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    reference.child(task.getResult().getUser().getUid()).setValue(helperClass);

                    Toast.makeText(SingUpActivity.this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(SingUpActivity.this, "Kayıt işlemi başarısız: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("SignUpError", "Kayıt işlemi başarısız: " + errorMessage);
                }
            }
        });
    }
}
