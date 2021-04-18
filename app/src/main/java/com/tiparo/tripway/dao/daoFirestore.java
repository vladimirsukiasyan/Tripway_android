package com.tiparo.tripway.dao;

import com.google.firebase.firestore.FirebaseFirestore;

interface daoFirestore {
    public void addDataUser(FirebaseFirestore firebaseFirestore, String mail, String name);
    
    public void readDataUSer(FirebaseFirestore firebaseFirestore, String collections);
}
