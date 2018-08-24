package ynca.nfs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;


public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "thumbnailsDB";
    private static final String TABLE_NAME = "thumbnails";
    private static final String ID = "id";
    private static final String IMAGE = "image";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "create table " + TABLE_NAME + " ( " +
                ID + " string , " + IMAGE + " blob )";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }


    public void saveImage(String id, Bitmap image)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        byte[] data = getBytes(image);
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID,id);
        contentValues.put(IMAGE,data);
        db.insert(TABLE_NAME,null,contentValues);

    }


    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }


    public Bitmap getImage(String key){

        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + ID + " = " + "'" + key + "'" ;
        Cursor cursor = db.rawQuery(Query,null  );


        if(cursor.moveToFirst()){
            byte[] blob = cursor.getBlob(1);
            cursor.close();
            Bitmap temp = BitmapFactory.decodeByteArray(blob,0,blob.length);
            return temp;
        }

        cursor.close();
        return null;
    }

    public boolean imageExists(String key) {
        SQLiteDatabase db = this.getReadableDatabase();

        String Query = "Select * from " + TABLE_NAME + " where " + ID + " = " + "'" + key + "'";
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.getCount() <= 0)
            return false;
        else
            return true;
    }



    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
        return stream.toByteArray();
    }




}
