package com.example.videp_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videp_v1.fragment.HomeFragment;
import com.example.videp_v1.users.GoogleUsersClass;
import com.example.videp_v1.users.VidepUsersClass;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private FirebaseAuth auth;
    private GoogleUsersClass googleUsersClass;
    private VidepUsersClass videpUsersClass;
    private ImageView userProfileImage;
    private ImageView videpLogoRefleshImage;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      /*  frameLayout = findViewById(R.id.frame_layout);
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, homeFragment);
        fragmentTransaction.commit();*/

        auth = FirebaseAuth.getInstance();

        userProfileImage = findViewById(R.id.toolbar_account);
        videpLogoRefleshImage = findViewById(R.id.main_videp_logo_reflesh);

        googleUsersClass = new GoogleUsersClass(this, auth);
        videpUsersClass = new VidepUsersClass();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogue();
            }
        });
        videpLogoRefleshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                if (homeFragment != null) {
                    homeFragment.isVideoListFetchedMain = false;
                    homeFragment.refreshHomeFragment(false);
                }
            }
        });


    }




    public void showDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);

        ViewGroup viewGroup = findViewById(android.R.id.content);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_singin_dialogue, viewGroup, false);
        builder.setView(view);

        TextView txtGoogleSignin = view.findViewById(R.id.txt_google_singin);
        TextView txtVidepSignin = view.findViewById(R.id.txt_videp_singin);


        txtGoogleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Google hesabına giriş butonuna tıklandığında
                googleUsersClass.signInWithGoogle();
            }
        });

        txtVidepSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!videpUsersClass.isLogged()) {
                    logInWithVidep();
                } else if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()) {
                    showVidepProfile();
                } else {
                    Toast.makeText(MainActivity.this, "E-posta doğrulaması gerekiyor", Toast.LENGTH_SHORT).show();
                }
            }
        });


        builder.create().show();
    }

    private void showVidepProfile() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }

    private void logInWithVidep() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googleUsersClass.handleGoogleSignInResult(data);
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this, "Google oturum açma hatası", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
