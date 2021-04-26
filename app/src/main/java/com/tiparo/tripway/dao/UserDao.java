package com.tiparo.tripway.dao;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class UserDao implements daoFirestore{
    private HashMap<String, Object> daoMap = new HashMap<>();

    public HashMap<String, Object> getDaoMap() {
        return daoMap;
    }

    public void setDaoMap(HashMap<String, Object> daoMap) {
        this.daoMap = daoMap;
    }

    /*public void addUser(FirebaseFirestore db, Map<String, Object> userMap) {
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
    }*/

    public void deleteUser() {}

    public void addDataUser(FirebaseFirestore firebaseFirestore, HashMap<String, Object> userMap) {
        DocumentReference documentReference = firebaseFirestore.collection("Users")
                .document("test2")
                .collection("UserTrips").document();
        documentReference.set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        documentReference.collection("Comments")
                .document()
                .collection("Comment")
                .document()
                .set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        documentReference.collection("Points").document().set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void readDataUSer(FirebaseFirestore firebaseFirestore, String collections, String docPath) {
        DocumentReference docRef = firebaseFirestore.collection("Users").document(docPath);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Timber.tag("DAO").d( "DocumentSnapshot data: %s", document.getData());
                    } else {
                        Timber.tag("DAO").d("No such document");
                    }
                } else {
                    Timber.tag("DAO").d(task.getException());
                }
            }
        });
    }
}