package com.tiparo.tripway.views.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.tiparo.tripway.R;

public class Authentication extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private SignInButton buttonSignInGoogle;
    private Button buttonSignIn;
    private Button buttonSignOut;
    private Button buttonExit;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView tvEmail;
    private TextView tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_authentication);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    System.out.println(user.getEmail());
                } else {
                    // User is signed out
                    System.out.println("null");
                }

            }
        };

        buttonSignIn = findViewById(R.id.button_sign_in);
        buttonSignOut = findViewById(R.id.button_sign_up);
        buttonExit = findViewById(R.id.exit);
        buttonSignInGoogle = findViewById(R.id.sign_in_button_google);
        tvEmail = findViewById(R.id.et_email);
        tvPassword = findViewById(R.id.et_password);

        buttonSignIn.setOnClickListener(this);
        buttonSignOut.setOnClickListener(this);
        buttonExit.setOnClickListener(this);
        buttonSignInGoogle.setOnClickListener(this);
    }
    @Override
    public void onStart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            //intent.putExtra(FirebaseAuth.class.getSimpleName(), (Parcelable) mAuth);
            //System.out.println(currentUser.getEmail());
            startActivity(intent);
            System.out.println("Firebase Ok");
        }
        else {
            //пользователь не авторизован
            System.out.println("Firebase Error");
        }

        super.onStart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign_in: {
                signIn(tvEmail.getText().toString(), tvPassword.getText().toString());
                break;
            }
            case R.id.sign_in_button_google: {
                googleSignIn();
                break;
            }
            case R.id.button_sign_up: {
                signUp(tvEmail.getText().toString(), tvPassword.getText().toString());
                break;
            }
            case R.id.exit: {
                signOut();
            }
        }
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            System.out.println(task.isSuccessful());
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                System.out.println("failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Task<AuthResult> authResultTask = mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            startNewActivity();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w("signInWithCredential:failure", task.getException());
                            //Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                            System.out.println("Authentication Failed.");
                        }
                    }
                });
    }

    private void startNewActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void signIn(String email, String password) {
        Task<AuthResult> authResultTask = mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                System.out.println(task.isSuccessful());
                if (task.isSuccessful()) {
                    System.out.println("Ok");
                    startNewActivity();
                } else System.out.println("Error");
            }
        });
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                System.out.println(task.isSuccessful());
                if (task.isSuccessful()) {
                    System.out.println("Ok");
                }
                else System.out.println("Error");
            }
        });
    }
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Ok");
                        }
                        else System.out.println("Error");
                    }
                });
    }
}
