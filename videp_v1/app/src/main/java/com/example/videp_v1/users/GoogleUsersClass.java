package com.example.videp_v1.users;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.videp_v1.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class GoogleUsersClass implements UserAuthentication {

    private Activity activity;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private boolean hasGoogleAuthenticator = false;

    private static final int RC_SIGN_IN = 9001;


    public GoogleUsersClass(Activity activity, FirebaseAuth auth) {
        this.activity = activity;
        this.auth = auth;
        configureGoogleSignIn();
    }
    public boolean hasGoogleAuthenticator() {
        return hasGoogleAuthenticator;
    }


    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleGoogleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = result.getResult(ApiException.class);

            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            } else {
                Toast.makeText(activity, "Google oturum açma hatası", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Toast.makeText(activity, "Google oturum açma hatası", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        // Kullanıcı oturum açtıktan sonra yapılacak işlemler
                        hasGoogleAuthenticator = true;
                        checkUserSignInStatus();
                    } else {
                        Toast.makeText(activity, "Kimlik doğrulama hatası", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserSignInStatus() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Toast.makeText(activity, "Oturum açıldı: " + user.getEmail(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Oturum açılmadı", Toast.LENGTH_SHORT).show();
        }
    }
    public List<Playlist> getUserPlaylists() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null && hasGoogleAuthenticator) {
            GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(activity);

            if (googleAccount != null) {
                // YouTube API istemcisini oluştur
                YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), null)
                        .setApplicationName("videp_v1")
                        .build();

                try {
                    // Kullanıcının oynatma listelerini al
                    YouTube.Playlists.List playlistRequest = youtube.playlists().list(Collections.singletonList("snippet,contentDetails"));
                    playlistRequest.setMine(true);
                    PlaylistListResponse playlistListResponse = playlistRequest.execute();
                    return playlistListResponse.getItems();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("GoogleUsersClass", "YouTube API ile ilgili bir hata oluştu: " + e.getMessage());
                }
            } else {
                Log.e("GoogleUsersClass", "GoogleSignInAccount is null.");
            }
        } else {
            Log.e("GoogleUsersClass", "User is not signed in or Google authentication is not successful.");
        }

        // Herhangi bir hata durumunda veya oynatma listesi bulunamadığında null döndür
        return null;
    }
    public String sendChannelInfoToServer() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null && hasGoogleAuthenticator) {
            GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(activity);

            if (googleAccount != null) {
                // Kullanıcının Google hesabından kanal ile ilgili bilgileri alabilirsiniz.
                String googleId = googleAccount.getId();
                return googleId;


                // Şimdi bu bilgileri sunucunuza veya başka bir yerde kullanabilirsiniz.
                // Örneğin, bu bilgileri bir API'ye gönderebilir veya veritabanınıza kaydedebilirsiniz.
                // Bu kısımda kullanıcıya özgü işlemleri gerçekleştirebilirsiniz.

                // Örnek olarak sadece loglama yapılıyor:

            } else {
                Log.e("GoogleUsersClass", "GoogleSignInAccount is null.");
            }
        } else {
            Log.e("GoogleUsersClass", "User is not signed in or Google authentication is not successful.");
        }
        return null;
    }

    @Override
    public String getAuthenticationUserName() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            return user.getDisplayName();
        }
        return null;
    }

    @Override
    public String getAuthenticationEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            return user.getEmail();
        }
        return null;
    }
}