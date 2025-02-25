package com.example.newtracking;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private final DatabaseReference userDatabase;

    public FirebaseHelper() {
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void saveUser(String userId, String name, String crn,String email, String role,String phNumber) {
        userDatabase.child(userId).setValue(new User(name,crn,email, role, phNumber));
    }
}
