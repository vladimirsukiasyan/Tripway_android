package com.tiparo.tripway.dao;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

interface daoFirestore {
    public void addDataUser(FirebaseFirestore firebaseFirestore, HashMap<String, Object> userMap);
    
    public void readDataUSer(FirebaseFirestore firebaseFirestore, String collections, String docPath);
}
