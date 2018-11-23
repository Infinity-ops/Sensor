package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVWriter;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";
    private static final String DB_NAME = "Bitalino.db";
    private static final int DB_VERSION = 1;
    private static final String COL_ID = "ID";
    private static final String COL_DATE = "CREATED_AT";
    private static final String COLXAXIS = "XVALUE";
    private static final String TABLENAME = "RECORDINGS";
    private static final String COLYAXIS = "YVALUE";
    private static DBHelper sInstance;
    private SQLiteDatabase db;
    private CSVWriter csvWrite;
    static Handler messageHandler;
    Cursor curCSV;


    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLENAME + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                COL_DATE + " TEXT default CURRENT_TIMESTAMP, " +
                COLXAXIS + " INTEGER, " +
                COLYAXIS + " DOUBLE )" ;
        db.execSQL(createTable);

    }

    /**
     The static getInstance() method ensures that only one DatabaseHelper will ever exist at any given time.
     If the sInstance object has not been initialized, one will be created.
     If one has already been created then it will simply be returned.
     You should not initialize your helper object using with new DatabaseHelper(context)!
     Instead, always use DatabaseHelper.getInstance(context), as it guarantees that only
     one database helper will exist across the entire application’s lifecycle.
     */
    public static synchronized DBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
            Log.d(TAG, "New DBHelper created");
        }

        return sInstance;
    }

    public static synchronized DBHelper getInstance(Context context, Handler handler) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
            Log.d(TAG, "New DBHelper created");
        }

        messageHandler = handler;
        return sInstance;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
        onCreate(db);
    }
    public void insert(long x, double y, String str ) {
        db = this.getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("DATE", str);
        contentvalues.put("X-AXIS", x);
        contentvalues.put("Y-AXIS", y);
        db.insert("DATA_POINTS", null, contentvalues);
    }


    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public void exportDataSheet(File outputFile) throws SQLException, IOException {

        csvWrite = new CSVWriter(new FileWriter(outputFile));

        curCSV = db.rawQuery("SELECT * FROM " + TABLENAME, null);

        csvWrite.writeNext(curCSV.getColumnNames());

        while (curCSV.moveToNext()) {

            String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2),
                    curCSV.getString(3), curCSV.getString(4), curCSV.getString(5), curCSV.getString(6), curCSV.getString(7)};

            csvWrite.writeNext(arrStr);
        }

        csvWrite.close();
        curCSV.close();
    }
}
