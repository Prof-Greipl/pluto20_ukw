package de.hawlandshut.pluto20_ukw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "xx SignIn Activity";

    // UI Variablen deklarieren
    EditText mEditTextEmail;
    EditText mEditTextPassword;
    Button mButtonSignIn;
    Button mButtonResetPassword;
    Button mButtonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        // Initialiseren der UT Variablen
        mEditTextEmail = (EditText) findViewById( R.id.sign_in_edittext_email);
        mEditTextPassword = (EditText) findViewById( R.id.sign_in_edittext_password);
        mButtonSignIn = (Button) findViewById( R.id.sign_in_button_sign_in);
        mButtonCreateAccount = (Button) findViewById( R.id.sign_in_button_create_account );
        mButtonResetPassword = (Button) findViewById( R.id.sign_in_button_reset_password );

        // Listener registrieren
        mButtonSignIn.setOnClickListener( this );
        mButtonResetPassword.setOnClickListener( this );
        mButtonCreateAccount.setOnClickListener( this );

        // TODO: Only for testing
        mEditTextEmail.setText("dieter.greipl@gmail.com");
        mEditTextPassword.setText("123456");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i) {
            case R.id.sign_in_button_create_account:
                Toast.makeText( getApplicationContext(), "Create Account pressed", Toast.LENGTH_LONG).show();
                return;
            case R.id.sign_in_button_reset_password:
                doSendResetPasswordMail();
                return;

            case R.id.sign_in_button_sign_in:
                doSignIn();
                return;
        }
    }

    private void doSignIn() {
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Toast.makeText( getApplicationContext(), "Your are signed in! Sign out first!", Toast.LENGTH_LONG).show();
            return;
        }

        String email = mEditTextEmail.getText().toString();
        String password = mEditTextPassword.getText().toString();
        // TODO: Validation
        FirebaseAuth.getInstance().signInWithEmailAndPassword( email, password).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText( getApplicationContext(), "Signed In.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText( getApplicationContext(), "Sign in failed (check log)", Toast.LENGTH_LONG).show();
                            Log.d(TAG,task.getException().getLocalizedMessage());
                        }
                    }
                }
        );
    }

    private void doSendResetPasswordMail() {
        String email = mEditTextEmail.getText().toString();
        FirebaseAuth.getInstance().sendPasswordResetEmail( email ).addOnCompleteListener(
                this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText( getApplicationContext(), "Mail sent!.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText( getApplicationContext(), "Sending mail failed (check log)", Toast.LENGTH_LONG).show();
                            Log.d(TAG,task.getException().getLocalizedMessage());
                        }
                    }
                }
        );
    }
}
