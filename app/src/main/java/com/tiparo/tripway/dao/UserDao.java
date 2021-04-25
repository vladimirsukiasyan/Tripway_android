package com.tiparo.tripway.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class UserDao implements daoFirestore{
    private String documentUserId;

    private String documentUserTripId;

    private String collectionCommentsId;

    private String collectionPointId;

    private HashMap<String, Object> daoMap = new HashMap<>();

    private Map<String, Object> userDataMap = new HashMap<>();

    private Map<String, Object> userTripsPointMap = new HashMap<>();

    private Map<String, Object> userTripsCommentMap = new HashMap<>();

    private Map<String, Object> userData = new HashMap<>();

    public HashMap<String, Object> getDaoMap() {
        return daoMap;
    }

    public void setDaoMap(HashMap<String, Object> daoMap) {
        this.daoMap = daoMap;
    }

    public Map<String, Object> getUserDataMap(FirebaseFirestore firebaseFirestore) {
        if (userDataMap.size() == 0)
            getDataUser(firebaseFirestore);

        return userDataMap;
    }

    public Map<String, Object> getUserTripsPointMap(FirebaseFirestore firebaseFirestore) {
        if (userTripsPointMap.size() == 0)
            getDataUser(firebaseFirestore);

        return userDataMap;
    }

    public Map<String, Object> getUserTripsCommentMap(FirebaseFirestore firebaseFirestore) {
        if (userTripsCommentMap.size() == 0)
            getDataUser(firebaseFirestore);

        return userDataMap;
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

    private void getDataUser(FirebaseFirestore firebaseFirestore) {
        firebaseFirestore.collection("Users").document(documentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            Timber.d("Cached document data: %s", task.getResult().getData());
                            userDataMap = task.getResult().getData();
                        } else {
                            Timber.d(task.getException(), "Cached get failed: ");
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                System.out.println("rr");
            }
        });
    }


    private void getDataUserTripsPoint(FirebaseFirestore firebaseFirestore) {
        firebaseFirestore.collection("Users")
                .document(documentUserId)
                .collection("UserTrip")
                .document(documentUserTripId)
                .collection("Points")
                .document()
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            userTripsPointMap = task.getResult().getData();
                            Timber.d("Cached document data: %s", task.getResult().getData());
                        } else {
                            Timber.d(task.getException(), "Cached get failed: ");
                        }
                    }
                });
    }

    private void getDataUserTripsComments(FirebaseFirestore firebaseFirestore) {
        firebaseFirestore.collection("Users")
                .document(documentUserId)
                .collection("UserTrip")
                .document(documentUserTripId)
                .collection("Comments")
                .document()
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            userTripsCommentMap = task.getResult().getData();
                            Timber.d("Cached document data: %s", task.getResult().getData());
                        } else {
                            Timber.d(task.getException(), "Cached get failed: ");
                        }
                    }
                });;
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