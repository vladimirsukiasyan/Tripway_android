package com.tiparo.tripway.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UserDao implements daoFirestore{
    private String documentUserId;

    private String documentUserTripId;

    private String collectionCommentsId;

    private String collectionPointId;

    private HashMap<String, Object> daoMap = new HashMap<>();

    public HashMap<String, Object> getDaoMap() {
        return daoMap;
    }

    public void setDaoMap(HashMap<String, Object> daoMap) {
        this.daoMap = daoMap;
    }

    public String getCollectionCommentsId() {
        return collectionCommentsId;
    }

    public String getCollectionPointId() {
        return collectionPointId;
    }

    public String getDocumentUserId() {
        return documentUserId;
    }

    public String getDocumentUserTripId() {
        return documentUserTripId;
    }

    public void deleteUser() {}

    public void setDataUserTripsPoint(FirebaseFirestore firebaseFirestore, HashMap<String, Object> pointMap) {
        firebaseFirestore.collection("Users")
                .document(documentUserId)
                .collection("UserTrip")
                .document(documentUserTripId)
                .collection("Points")
                .document()
                .set(pointMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
        });
    }

    public void setDataUserTripsComment(FirebaseFirestore firebaseFirestore, HashMap<String, Object> commentMap) {
        firebaseFirestore.collection("Users")
                .document(documentUserId)
                .collection("UserTrip")
                .document(documentUserTripId)
                .collection("Comments")
                .document()
                .collection("Comment")
                .document()
                .set(commentMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public Task<DocumentSnapshot> getDataUser(FirebaseFirestore firebaseFirestore) {
        return firebaseFirestore.collection("Users")
                .document(documentUserId)
                .get();
    }

    public Task<DocumentSnapshot> getDataUserTripsPoint(FirebaseFirestore firebaseFirestore) {
        return firebaseFirestore.collection("Users")
                .document(documentUserId)
                .collection("UserTrip")
                .document(documentUserTripId)
                .collection("Points")
                .document()
                .get();
    }

    public Task<DocumentSnapshot> getDataUserTripsComments(FirebaseFirestore firebaseFirestore) {
        return firebaseFirestore.collection("Users")
                .document(documentUserId)
                .collection("UserTrip")
                .document(documentUserTripId)
                .collection("Comments")
                .document()
                .get();
    }

    public void addDataUser(FirebaseFirestore firebaseFirestore, HashMap<String, Object> userMap) {
        DocumentReference documentReference = firebaseFirestore.collection("Users")
                .document();

        documentUserId = documentReference.getId();

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

        documentUserTripId = documentReference.collection("UserTrips").document().getId();

        documentReference.collection("UserTrips").document(documentUserTripId)
                .collection("Points")
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

        collectionPointId = documentReference
                .collection("UserTrips")
                .document(documentUserTripId)
                .collection("Point")
                .getId();

        documentReference.collection("UserTrips").document(documentUserTripId)
                .collection("Comments")
                .document()
                .collection("Comment")
                .document()
                .set(userMap)//TODO добавить map
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        collectionCommentsId = documentReference
                .collection("UserTrips")
                .document(documentUserTripId)
                .collection("Comments")
                .getId();
    }
}