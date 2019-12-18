package de.hawlandshut.pluto20_ukw.model;

import com.google.firebase.database.DataSnapshot;

public class Post {
    public String uid;
    public String author;
    public String title;
    public String body;
    public long timestamp;
    public String firebaseKey;



    public Post(String uid, String author, String title, String body, long timestamp, String firebaseKey) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.firebaseKey = firebaseKey;
    }

    public static Post fromSnapShot(DataSnapshot dataSnapshot) {
        String uid   = (String) dataSnapshot.child("uid").getValue();
        String title = (String) dataSnapshot.child("title").getValue();
        String body  = (String) dataSnapshot.child("body").getValue();
        String author = (String) dataSnapshot.child("author").getValue();

        Post p = new Post(uid, author, title, body, 0, dataSnapshot.getKey());

        return p;
    }
}
