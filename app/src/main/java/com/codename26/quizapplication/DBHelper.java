package com.codename26.quizapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.Random;


public class DBHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "databaseDB.db";
    private static final int DATABASE_VERSION = 2;
    private Context mContext;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
        mContext = context;
    }

    public QuizObject readDB() {
        Cursor c;
        QuizObject quizObject = new QuizObject();
        SQLiteDatabase db = getReadableDatabase();
        String selection = "wasShown < 1";
        try {
            c = db.query("quiztable", null, selection, null, null, null, null);

            //Take random entry from DB, that hasn't been shown
            try {
                int randMax = c.getCount();
                if (randMax > 0) {
                    if (c.moveToPosition(new Random().nextInt(randMax))) {
                        int idColIndex = c.getColumnIndex("id");
                        int titleColIndex = c.getColumnIndex("title");
                        int quantityColIndex = c.getColumnIndex("quantity");
                        int pictureColIndex = c.getColumnIndex("picture");

                        quizObject.setTitle(c.getString(titleColIndex));
                        quizObject.setNumberOfSearchQueries(c.getInt(quantityColIndex));
                        quizObject.setImageId(mContext.getResources().getIdentifier(
                                c.getString(pictureColIndex), "raw", mContext.getPackageName()));
                        ContentValues cv = new ContentValues();
                        cv.put("wasShown", 1);
                        String where = "title = '" + c.getString(titleColIndex) + "'";
                        db.update("quiztable", cv, where, null);
                    }
                } else {
                    return null;
                }
            }
            finally{
                c.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return quizObject;
    }

    public void resetWasShown() {
        String selection = "wasShown == 1";
        Cursor c;
        SQLiteDatabase db = getReadableDatabase();
        c = db.query("quiztable", null, selection, null, null, null, null);
        ContentValues cv = new ContentValues();
        cv.put("wasShown", 0);
        try {
            if (c.moveToFirst()) {
                do {
                    db.update("quiztable", cv, null, null);
                } while (c.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }finally {
            c.close();
        }
    }


}
