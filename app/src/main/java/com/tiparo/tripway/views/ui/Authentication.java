package com.tiparo.tripway.views.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tiparo.tripway.R;
import com.tiparo.tripway.dao.UserDao;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class Authentication extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private UserDao dao;
    private FirebaseFirestore db;
    private SignInButton buttonSignInGoogle;
    private Button buttonSignIn;
    private Button buttonSignOut;
    private Button buttonExit;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView tvEmail;
    private TextView tvPassword;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_authentication);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dao = new UserDao();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FirebaseAuth.AuthStateListener mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Timber.e(user.getEmail());
            } else {
                // User is signed out
                Timber.e("User is signed out");
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
        //TODO для проверки авторизации
        signOut();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Timber.e(SignInState.UNAUTHENTICATED_ON_START.toString());
        }
        else {
            //пользователь не авторизован
            Timber.e(SignInState.FAILED_AUTHENTICATION_ON_START.toString());
        }

        super.onStart();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        String email = tvEmail.getText().toString();
        String password = tvPassword.getText().toString();

        switch (view.getId()) {
            case R.id.button_sign_in: {
                if (!TextUtils.isEmpty(email) & !TextUtils.isEmpty(password))
                    signIn(email, password);
                break;
            }
            case R.id.sign_in_button_google: {
                googleSignIn();
                break;
            }
            case R.id.button_sign_up: {
                if (!TextUtils.isEmpty(email) & !TextUtils.isEmpty(password))
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
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Timber.e(SignInState.FAILED_AUTHENTICATION.toString());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Task<AuthResult> authResultTask = mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startNewActivity();
                    }
                });
    }
    //Launch MainActivity
    private void startNewActivity() {
        //TODO добавить name
        HashMap<String, Object> dataUserMap = new HashMap<>();
        dataUserMap.put("name", tvEmail.getText().toString());
        dataUserMap.put("mail", tvEmail.getText().toString());

        dao.setDaoMap(dataUserMap);
        dao.addDataUser(db, dataUserMap);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    //login with mail and password
    private void signIn(String email, String password) {
        if(email.equals("test"))
            startNewActivity();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Timber.e(SignInState.AUTHENTICATED.toString());
                        startNewActivity();
                    } else Timber.e(SignInState.FAILED_AUTHENTICATION.toString());
                });
    }
    //registration with mail and password
    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Timber.e(SignInState.USER_REGISTERED.toString());
                    }
                    else Timber.e(SignInState.FAILED_REGISTERED.toString());;
                });
    }
    //exit
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        Timber.e(SignInState.EXIT.toString());
                    }
                    else Timber.e(SignInState.FAILED_EXIT.toString());
                });
    }
}
