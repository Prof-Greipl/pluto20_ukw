package de.hawlandshut.pluto20_ukw;


import android.content.Intent;
import android.net.Uri;
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

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    // Verbindiung zur DB
    boolean mListenerIsRunning;
    ChildEventListener mCEL;
    Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init ListView
        mListView = findViewById( R.id.mainListViewMessages);

        // Initialisieren der Post-Liste
        mPostList = new ArrayList<Post>();
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

                Post post = getItem( getCount() - 1 - position);

                line1.setText(post.author+ " (" +post.title + " )");
                line2.setText(post.body );

                return view;
            }
        };

        // Adapter der Listview zuordnen...
        mListView.setAdapter( mAdapter );

        // Query und CEL initialisieren
        mCEL = getChildEventListener();
        mQuery = FirebaseDatabase.getInstance().getReference().child("posts/").limitToLast(5);

        mListenerIsRunning = false;

    }

    private ChildEventListener getChildEventListener() {
        ChildEventListener cel = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "Child added : " + dataSnapshot.getKey());
                // Verarbeitetn des empfangenen Posts
                Post p = Post.fromSnapShot( dataSnapshot );
                mPostList.add( p );
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
             //   Log.d(TAG, "Child changed : " + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for (int i = 0; i < mPostList.size(); i++){
                    if (key.equals( mPostList.get(i).firebaseKey)){
                        mPostList.remove(i);
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "Child deleted : " + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "Child moved : " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mListenerIsRunning = false;
                Log.d(TAG, "Listener cancelled.");
            }
        };

        return cel;
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
            case R.id.menu_simulate_crash:
                Crashlytics.getInstance().crash();

            case R.id.menu_manage_account:
                // Goto to ManageAccount
                intent = new Intent( getApplication(), ManageAccountActivity.class);
                startActivity( intent );
                return true;

            case R.id.menu_post:
                intent = new Intent( getApplication(), PostActivity.class);
                startActivity( intent );
                return true;

            default:
                return true;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
        // Check, if we have a user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if( user == null){
            resetApp();
            Intent intent;
            intent = new Intent( getApplication(), SignInActivity.class);
            startActivity( intent );
        }
        else {
            // Start listener if we have a user
            if (!mListenerIsRunning) {

                mPostList.clear();
                mAdapter.notifyDataSetChanged();

                mQuery.addChildEventListener(mCEL);
                mListenerIsRunning = true;
            }

        }
    }

    void resetApp(){

        if (mListenerIsRunning){
            mQuery.removeEventListener( mCEL );
            mListenerIsRunning = false;
        }

        mPostList.clear();
        mAdapter.notifyDataSetChanged();
    }

}
