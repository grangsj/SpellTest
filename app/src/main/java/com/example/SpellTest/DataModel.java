package com.example.SpellTest;

import android.content.Context;

public class DataModel {

    private static DataModel sDataModel;


    private DataModel (Context context) {

    }

    public static DataModel getDataModel(Context context){
        if (sDataModel == null){
            sDataModel = new DataModel(context);
        }

        return sDataModel;
    }
}
