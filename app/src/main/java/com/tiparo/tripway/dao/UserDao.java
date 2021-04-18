package com.tiparo.tripway.dao;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class UserDao implements daoFirestore{
    public void addUser(FirebaseFirestore db) {
        Map<String, Object> user1 = new HashMap<>();
        user1.put("mail", "12345@gmail.com");
        user1.put("name", "Nikitos");

        db.collection("Users").document("LA")
                .set(user1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Timber.d("OK TEST");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.d("ERROR TEST");
                    }
                });

        db.collection("NewCollection")
                .add(user1)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Timber.tag("DAO").d("DocumentSnapshot added with ID: %s", documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.tag("DAO").d("Error adding document");
                    }
                });
    }

    public void deleteUser() {}

    public void readData(FirebaseFirestore db) {
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void addDataUser(FirebaseFirestore firebaseFirestore, String mail, String name) {
        Map<String, Object> user = new HashMap<>();
        user.put("mail", "12345@gmail.com");
        user.put("name", "Nikitos");

        Map<String, Object> trip = new HashMap<>();
        trip.put("trip1", "Trip");
        trip.put("trip2", "Trip");

        user.put("Trips", trip);

        DocumentReference documentReference = firebaseFirestore.collection("Users").document()
                .collection("UserTrips").document();
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        documentReference.collection("Comments").document().collection("Comment").document().set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });;
        documentReference.collection("Points").document().set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        /*.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Timber.d("OK TEST");
                        Timber.tag("DAO").d("DocumentSnapshot");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.d("ERROR TEST");
                    }
                });*/

        /*firebaseFirestore.collection("NewCollection")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Timber.tag("DAO").d("DocumentSnapshot added with ID: %s", documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.tag("DAO").d("Error adding document");
                    }
                });*/
    }

    @Override
    public void readDataUSer(FirebaseFirestore firebaseFirestore, String collections) {

    }
}