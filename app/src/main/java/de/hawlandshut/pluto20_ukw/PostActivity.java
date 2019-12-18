package de.hawlandshut.pluto20_ukw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "xx Post Activity";

    // UI Variablen deklarieren
    EditText mEditTextTitle;
    EditText mEditTextMessage;
    Button mButtonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // UI Elemente initialisieren
        mEditTextTitle = (EditText) findViewById(R.id.post_edittext_title);
        mEditTextMessage = (EditText) findViewById( R.id.post_edittext_text);
        mButtonSend = (Button) findViewById( R.id.post_button_send );

        mButtonSend.setOnClickListener( this );

        // TODO: Vorbelegen zum Testen; remove later
        mEditTextMessage.setText("Lore ipsum se asdf asdf safd sadf sadfdsa");
        mEditTextTitle.setText("Testtitle");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i) {
            case R.id.post_button_send:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if ( user == null){
                    Log.d(TAG, "Severe Error : User is null in Posting");
                    finish();
                }

                Map<String,Object> postMap = new HashMap<>();
                postMap.put("uid", user.getUid());
                postMap.put("author", user.getEmail());
                postMap.put("title", mEditTextTitle.getText().toString());
                postMap.put("body", mEditTextMessage.getText().toString());
                postMap.put("timestamp", ServerValue.TIMESTAMP);

                DatabaseReference mDatabase = FirebaseDatabase
                        .getInstance()
                        .getReference("posts/");

                mDatabase.push().setValue( postMap );
                finish();

                return;
        }
    }
}
