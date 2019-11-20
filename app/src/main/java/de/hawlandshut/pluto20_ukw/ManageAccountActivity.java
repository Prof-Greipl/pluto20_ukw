package de.hawlandshut.pluto20_ukw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ManageAccountActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "xx ManageAccount Activi";

    TextView mEmail, mAccountState, mTechnicalId;
    Button mButtonSignOut, mButtonDeleteAccount, mButtonSendActivationMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textview = (TextView) findViewById( R.id.manageAccountTechnicalId);
        setContentView(R.layout.activity_manage_account);

        // UI Elemente-Initialiseren
        mEmail = findViewById( R.id.manageAccountEmail );
        mAccountState = findViewById( R.id.manageAccountVerificationState );
        mTechnicalId = findViewById( R.id.manageAccountTechnicalId );

        mButtonDeleteAccount = findViewById( R.id.manageAccountButtonDeleteAccount);
        mButtonSendActivationMail = findViewById( R.id.manageAccountButtonSendActivationMail );
        mButtonSignOut = findViewById( R.id.manageAccountButtonSignOut );

        // Listener registrieren
        mButtonSignOut.setOnClickListener( this );
        mButtonSendActivationMail.setOnClickListener( this );
        mButtonDeleteAccount.setOnClickListener( this );

        // Nur zur Sicherheit; die aufrufende Activity muss garantieren,
        // dass es einen user gibt.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user==null)
            finish();

        mEmail.setText( "E-Mail: "+ user.getEmail());
        mTechnicalId.setText("Technical Id : "+user.getUid() );
        mAccountState.setText("Account verified : " + user.isEmailVerified());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i) {
            case R.id.manageAccountButtonDeleteAccount:
                doDeleteAccount();
                return;
            case R.id.manageAccountButtonSignOut:
                doSignOut();
                return;

            case R.id.manageAccountButtonSendActivationMail:
                // TODO: Ist das hier sinnvoll?
                doSendActivationMail();
                return;
        }
    }

    private void doDeleteAccount() {
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Toast.makeText( getApplicationContext(), "Cannot delete account: not signed in! ", Toast.LENGTH_LONG).show();
            return;
        }

        user.delete().addOnCompleteListener(
                this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText( getApplicationContext(), "Account deleted.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText( getApplicationContext(), "Account deletion failed (check log)", Toast.LENGTH_LONG).show();
                            Log.d(TAG,task.getException().getLocalizedMessage());
                        }
                    }
                }
        );
    }

    private void doSendActivationMail() {
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Toast.makeText( getApplicationContext(), "Sending not possible: not signed in! ", Toast.LENGTH_LONG).show();
            return;
        }

        // At this point we have a valid user object
        if (user.isEmailVerified())
        {
            Toast.makeText( getApplicationContext(), "Account already verified.", Toast.LENGTH_LONG).show();
            return;
        }

        user.sendEmailVerification().addOnCompleteListener(
                this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText( getApplicationContext(), "Verification mails sent.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText( getApplicationContext(), "Sending mail failed (check log)", Toast.LENGTH_LONG).show();
                            Log.d(TAG,task.getException().getLocalizedMessage());
                        }
                    }
                }
        );
    }

    private void doSignOut() {
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Toast.makeText( getApplicationContext(), "Your are not signed in!", Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseAuth.getInstance().signOut();
        user =  FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Toast.makeText( getApplicationContext(), "Signed out.", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText( getApplicationContext(), "Signed out failed.", Toast.LENGTH_LONG).show();

        }
    }
}
