package de.hawlandshut.pluto20_ukw;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.hawlandshut.pluto20_ukw.model.Post;
import de.hawlandshut.pluto20_ukw.test.TestData;
import de.hawlandshut.pluto20_ukw.web.WebAppInterface;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "xx Main Activity";

    // Initialisieren der ListView
    ListView mListView;

    // Liste der von Firebase empfangenen Posts
    ArrayList<Post> mPostList;
    ArrayAdapter<Post> mAdapter;

    // TODO: Just for testing. Remove
    String TEST_MAIL = "dieter.greipl@gmail.com";
    String TEST_PASSWORD = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init ListView
        mListView = findViewById( R.id.mainListViewMessages);

        // Initialisieren der Post-Liste mit Testdaten
        mPostList = (ArrayList<Post>) TestData.createTestdata();
        mAdapter = new ArrayAdapter<Post>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                mPostList
        ){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView line1, line2;

                line1 = view.findViewById( android.R.id.text1 );
                line2 = view.findViewById( android.R.id.text2 );

                Post post = getItem(position);

                line1.setText(post.author+ " (" +post.title + " )");
                line2.setText(post.body );

                return view;
            }
        };

        // Adapter der Listview zuordnen...
        mListView.setAdapter( mAdapter );
    }


    // Menue aus der XML Datei "aufblasen"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.menu_main, menu);
        return true;
    }

    // Listener für Menu Events erzeugen
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch( item.getItemId() ){
            case R.id.menu1TestAuth:
               doTestAuth();
               return true;

            case R.id.menu2CreateUser:
                doCreateUser();
                return true;

            case R.id.menu3SignIn:
                doSignIn();
                return true;

            case R.id.menu4SignOut:
                doTestSignOut();
                return true;

            case R.id.menu5DeleteTestUser:
                doDeleteTestUser();
                return true;

            case R.id.menu6SendResetPasswordMail:
                doSendResetPasswordMail();
                return true;

            case R.id.menu7SendActivationMail:
                doSendActivationMail();
                return true;

            case R.id.menu8SetDisplayName:
                doSetDisplayName();
                return true;

            case R.id.menu9WebView:
                intent = new Intent( getApplication(), WebViewActivity.class);
                startActivity( intent );
                return true;

            default:
                return true;
        }
    }

    private void doSetDisplayName() {

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

    private void doSendResetPasswordMail() {
          FirebaseAuth.getInstance().sendPasswordResetEmail( TEST_MAIL).addOnCompleteListener(
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

    private void doDeleteTestUser() {
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

    private void doTestSignOut() {
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

    private void doSignIn() {
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Toast.makeText( getApplicationContext(), "Your are signed in! Sign out first!", Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword( TEST_MAIL, TEST_PASSWORD).addOnCompleteListener(
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

    private void doCreateUser() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword( TEST_MAIL, TEST_PASSWORD).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText( getApplicationContext(), "User created.", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText( getApplicationContext(), "User creation failed.", Toast.LENGTH_LONG).show();
                            Log.d(TAG,task.getException().getLocalizedMessage());
                        }
                    }
                }
        );
    }

    private void doTestAuth() {
        FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        String msg =  (user == null) ?
                "Not Authenticated" :
                ("Authenticated : " +user.getEmail() +"(Verified:" + user.isEmailVerified()+ ")" );
        Toast.makeText( getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
        // Check, if we have a user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if( user == null){
            // We have no user. Reset the app and goto SignInActivity
            // TODO Reset app

            // Goto SignInAct
            Intent intent;
            intent = new Intent( getApplication(), SignInActivity.class);
            startActivity( intent );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart called");
    }


}
