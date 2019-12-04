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

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "xx Create Acc Activity";

    // Deklarieren der UI Variablen
    EditText mEditTextMail;
    EditText mEditTextPassword1;
    EditText mEditTextPassword2;
    Button mButtonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // UI Variablen initialisieren
        mEditTextMail = (EditText) findViewById( R.id.create_account_edittext_email);
        mEditTextPassword1 = (EditText) findViewById( R.id.create_account_edittext_password1);
        mEditTextPassword2 = (EditText) findViewById( R.id.create_account_edittext_password2);
        mButtonCreateAccount = (Button) findViewById(R.id.create_account_button_create);

        // Listener registrieren
        mButtonCreateAccount.setOnClickListener( this );

        // TODO: Remove! Just for testing
        mEditTextMail.setText("dietergreipl@gmail.com");
        mEditTextPassword1.setText("123456");
        mEditTextPassword2.setText("123456");

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i) {
            case R.id.create_account_button_create:
                doCreateAccount();
                return;
        }
    }

    private void doCreateAccount() {

        String email = mEditTextMail.getText().toString();
        String password1 = mEditTextPassword1.getText().toString();
        String password2 = mEditTextPassword2.getText().toString();

        // TODO Validation: password equal?
        FirebaseAuth.getInstance().createUserWithEmailAndPassword( email, password1).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText( getApplicationContext(), "User created.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Toast.makeText( getApplicationContext(), "User creation failed.", Toast.LENGTH_LONG).show();
                            Log.d(TAG,task.getException().getLocalizedMessage());
                        }
                    }
                }
        );
    }
}
