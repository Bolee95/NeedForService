package ynca.nfs.Activities.StartActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ynca.nfs.Activities.MainScreenServisActivity;
import ynca.nfs.Activities.mainScreenClientActivity;
import ynca.nfs.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailView;
    private EditText mPasswordView;
    private ImageView mImageView;
    private ProgressDialog mProgressDialog;

    private Button registerButton;
    private Button mEmailSignInButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    FirebaseUser currentUser;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Views
        mEmailView = (EditText) findViewById(R.id.email);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mPasswordView = (EditText) findViewById(R.id.password);

        //Buttons
        registerButton = (Button) findViewById(R.id.register);
        mEmailSignInButton = (Button) findViewById(R.id.sign_in);

        //OnClickListeners
        registerButton.setOnClickListener(this);
        mEmailSignInButton.setOnClickListener(this);

        //Firebase setup
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseDatabase.getReference();


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            showProgressDialog();
        }

        updateUI(currentUser);
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            Toast.makeText(this,getResources().getString(R.string.loginFail),Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this,getResources().getString(R.string.AuthSucc),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginFail),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailView.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getResources().getString(R.string.Requested));
            valid = false;
        } else if(!email.contains("@")){
            mEmailView.setError(getResources().getString(R.string.InvalidMail));
            valid = false;
        } else {
            mEmailView.setError(null);
        }

        String password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getResources().getString(R.string.Requested));
            valid = false;
        } else if(password.length() < 8){
            mPasswordView.setError(getResources().getString(R.string.PassLength));
            valid = false;
        } else {
            mPasswordView.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String email = user.getEmail();
            mDatabase.child("Korisnik").child("Servis").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        hideProgressDialog();
                        startActivity(new Intent(getBaseContext(), MainScreenServisActivity.class));
                    } else {
                        hideProgressDialog();
                        startActivity(new Intent(getBaseContext(), mainScreenClientActivity.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in) {
            signIn(mEmailView.getText().toString(), mPasswordView.getText().toString());
        } else if (i == R.id.register) {
            startActivity(new Intent(getBaseContext(), RegisterActivity.class));
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}