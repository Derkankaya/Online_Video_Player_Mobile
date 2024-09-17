package com.example.videp_v1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videp_v1.users.VidepUsersClass;
import com.example.videp_v1.users.VidepUsersManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    TextView profileUserName, profileLastName, profileFirstName, profilePassword, profileEmail, profileFacultyOfUniversity;
    Button editProfileButton, exitButton;
    private ImageView videpLogo;
    FirebaseAuth auth;

    FirebaseUser user;

    public VidepUsersClass videpUsersClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileFirstName = findViewById(R.id.firstNameProfile);
        profileLastName = findViewById(R.id.lastNameProfile);
        profileUserName = findViewById(R.id.userNameProfile);
        profileEmail = findViewById(R.id.emailProfile);
        profilePassword = findViewById(R.id.passwordProfile);
        profileFacultyOfUniversity = findViewById(R.id.profileFacultyOfUniversity);
        editProfileButton = findViewById(R.id.editButton);
        videpLogo = findViewById(R.id.profile_videp_logo);
        exitButton = findViewById(R.id.exitButton);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String userUid = user.getUid();


        showUserData(userUid);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutUser();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        videpLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void logOutUser() {
        if (user != null) {
            String email = user.getEmail();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("VidepUsers").child(user.getUid());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    VidepUsersClass user = VidepUsersManager.getInstance().getLoggedManager(auth.getUid());//snapshot.getValue(VidepUsersClass.class);
                    if (user != null && user.isLogged()) {
                        snapshot.child("isLogged").getValue(Boolean.class);
                        snapshot.getRef().child("isLogged").setValue(false);
                        user.setLogged(false);

                    }
                    FirebaseAuth.getInstance().signOut();

                    Toast.makeText(ProfileActivity.this, "Oturum kapatıldı", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Veritabanına erişim hatası", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ProfileActivity.this, "Kullanıcı oturumu açık değil", Toast.LENGTH_SHORT).show();
        }
    }

    public void showUserData(String userUid) {
        if(user!=null){
        if (userUid != null && !userUid.isEmpty()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("VidepUsers").child(user.getUid());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        VidepUsersClass userData = snapshot.getValue(VidepUsersClass.class);

                        if (userData != null) {

                            userData.setLogged(Boolean.TRUE.equals(snapshot.child("isLogged").getValue(Boolean.class)));
                            userData.setFirstName(snapshot.child("firstName").getValue(String.class));
                            userData.setLastName(snapshot.child("lastName").getValue(String.class));
                            userData.setUserName(snapshot.child("userName").getValue(String.class));
                            userData.setEmail(snapshot.child("email").getValue(String.class));
                            userData.setPassword(snapshot.child("password").getValue(String.class));
                            userData.setFacultyOfUniversity(snapshot.child("facultyOfUniversity").getValue(String.class));
                            VidepUsersManager.getInstance().setLoggedManager(userUid, userData);
                            profileFirstName.setText(userData.getFirstName()!= null? userData.getFirstName() : "");

                            Log.d("ProfileDebug", "FirstName set: " + profileFirstName.getText().toString());

                            profileLastName.setText(userData.getLastName() != null ? userData.getLastName() : "");
                            Log.d("ProfileDebug", "LastName set: " + profileLastName.getText().toString());

                            profileUserName.setText(userData.getUserName() != null ? userData.getUserName() : "");
                            Log.d("ProfileDebug", "UserName set: " + profileUserName.getText().toString());

                            profileEmail.setText(userData.getEmail() != null ? userData.getEmail() : "");
                            Log.d("ProfileDebug", "Email set: " + profileEmail.getText().toString());

                            profilePassword.setText(userData.getPassword() != null ? userData.getPassword() : "");
                            Log.d("ProfileDebug", "Password set: " + profilePassword.getText().toString());

                            profileFacultyOfUniversity.setText(userData.getFacultyOfUniversity() != null ? userData.getFacultyOfUniversity() : "");
                            Log.d("ProfileDebug", "Faculty set: " + profileFacultyOfUniversity.getText().toString());
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show();
                    }

            }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ProfileDebug", "Database Error: " + error.getMessage());
                    Toast.makeText(ProfileActivity.this, "Veritabanına erişim hatası", Toast.LENGTH_SHORT).show();
                }
            });
        }}
    }

}
