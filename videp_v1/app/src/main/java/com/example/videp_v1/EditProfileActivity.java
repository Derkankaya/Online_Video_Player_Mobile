package com.example.videp_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.videp_v1.users.VidepUsersManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {


    EditText editFirstName, editLastName, editUserName, editPassword,editEmail, editFacultyOfUniversity;
    Button saveButton;
    ImageView videpLogo;
    String userFistName, userLastName, userUserName, userPassword, userEmail,userFacultyOfUniversity;
    DatabaseReference reference;
    FirebaseAuth auth;

    FirebaseUser user;

    String userUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userUid = user.getUid();

        reference = FirebaseDatabase.getInstance("https://videp-v1-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child("VidepUsers").child(userUid);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editUserName = findViewById(R.id.editUserName);
        editPassword = findViewById(R.id.editPassword);
        editEmail = findViewById(R.id.editEmail);
        editFacultyOfUniversity = findViewById(R.id.editFacultyOfUniversityName);
        saveButton = findViewById(R.id.saveButton);
        videpLogo = findViewById(R.id.edit_videp_logo);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            boolean firstNameChanged = isFirstNameChanged();
            boolean lastNameChanged = isLastNameChanged();
            boolean userNameChanged = isUserNameChanged();
            boolean passwordChanged = isPasswordChanged();
            boolean emailChanged = isEmailChanged();
            boolean facultyOfUniversityChanged = isFacultyOfUniversityChanged();

                if (firstNameChanged || lastNameChanged || userNameChanged ||
                        passwordChanged || emailChanged || facultyOfUniversityChanged) {
                    Toast.makeText(EditProfileActivity.this, "Güncellendi", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(EditProfileActivity.this, "Kaydedilemedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
        videpLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        showData();
    }


    public boolean isFirstNameChanged() {
        String newValue = editFirstName.getText().toString();
        userFistName = VidepUsersManager.getInstance().getLoggedManager(userUid).getFirstName();
        if (newValue.isEmpty()) {
            editFirstName.setText(userFistName);
            return false;
            } else if (!userFistName.equals(newValue)) {
            reference.child("firstName").setValue(newValue);
            userFistName = newValue;
            return true;
        } else {
            return false;
        }
    }


    public boolean isLastNameChanged() {
        String newValue = editLastName.getText().toString();
        userLastName = VidepUsersManager.getInstance().getLoggedManager(userUid).getLastName();
        if (newValue.isEmpty()) {
            editLastName.setText(userLastName);
            return false;
        } else if (!userLastName.equals(newValue)) {
            reference.child("lastName").setValue(newValue);
            userLastName = newValue;
            return true;
        } else {
            return false;
        }
    }
    public boolean isUserNameChanged() {
        String newValue = editUserName.getText().toString();
        userUserName = VidepUsersManager.getInstance().getLoggedManager(userUid).getUserName();

        if (newValue.isEmpty()) {
            editUserName.setText(userUserName);
            return false;
        } else if (!userUserName.equals(newValue)) {
            reference.child("userName").setValue(newValue);
            userUserName = newValue;
            return true;
        } else {
            return false;
        }
    }
    public boolean isPasswordChanged() {
        String newValue = editPassword.getText().toString();
        userPassword = VidepUsersManager.getInstance().getLoggedManager(userUid).getPassword();
        if (newValue.isEmpty()) {
            editPassword.setText(userPassword);
            return false;
        } else if (!userPassword.equals(newValue)) {
            reference.child("password").setValue(newValue);
            userPassword = newValue;
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmailChanged() {
        String newValue = editEmail.getText().toString();
        userEmail = VidepUsersManager.getInstance().getLoggedManager(userUid).getEmail();
        if (newValue.isEmpty()) {
            editEmail.setText(userEmail);
            return false;
        } else if (!userEmail.equals(newValue)) {
            reference.child("email").setValue(newValue);
            userEmail = newValue;
            return true;
        } else {
            return false;
        }
    }
    public boolean isFacultyOfUniversityChanged() {
        String newValue = editFacultyOfUniversity.getText().toString();
        userFacultyOfUniversity = VidepUsersManager.getInstance().getLoggedManager(userUid).getFacultyOfUniversity();
        if (newValue.isEmpty()) {
            editFacultyOfUniversity.setText(userFacultyOfUniversity);
            return false;
        } else if (!userFacultyOfUniversity.equals(newValue)) {
            reference.child("facultyOfUniversity").setValue(newValue);
            userFacultyOfUniversity = newValue;
            return true;
        } else {
            return false;
        }
    }

    public void showData() {
        Intent intent = getIntent();

        userFistName = intent.getStringExtra("firstName");
        userLastName = intent.getStringExtra("lastName");
        userUserName = intent.getStringExtra("userName");
        userEmail = intent.getStringExtra("email");
        userPassword = intent.getStringExtra("password");
        userFacultyOfUniversity = intent.getStringExtra("facultyOfUniversity");

        if (userFistName != null) {
            editFirstName.setText(userFistName);
        }
        if (userLastName != null) {
            editLastName.setText(userLastName);
        }
        if (userUserName != null) {
            editUserName.setText(userUserName);
        }
        if (userPassword != null) {
            editPassword.setText(userPassword);
        }
        if (userEmail != null) {
            editEmail.setText(userEmail);
        }
        if (userFacultyOfUniversity != null) {
            editFacultyOfUniversity.setText(userFacultyOfUniversity);
        }
    }

}