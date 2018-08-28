package org.dimigo.whatchamajig;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

public class DBManager {

    private static DBManager manager;
    private Context context;
    private int openCounter;

    public static final String DATABASE_NAME = "memo.db";
    public static final String TABLE_NAME = "MEMO";
    public static final int DATABASE_VERSION = 4;
    SQLiteDatabase database;

    private DBManager(Context context){
        this.context = context;
    }

    public static DBManager getInstance(Context context){
        if(manager == null) {
            manager = new DBManager(context);
        }

        return manager;
    }

    public void open(){
        if(openCounter == 0 ) {
            MemoDBHelper helper = new MemoDBHelper(context);
            database = helper.getWritableDatabase();
        }
        openCounter++;
    }

    public void close(){
        openCounter--;
        if(openCounter == 0) {
            if (database != null) {
                database.close();
            }
        }
    }

    public Cursor rawQuery(String sql){
        return database.rawQuery(sql, null);
    }

    public boolean execSql(String sql){
        try{
            database.execSQL(sql);
            return true;
        }catch(Exception e){
            Log.d("TAG", e.toString());
        }
        return false;
    }

    private class MemoDBHelper extends SQLiteOpenHelper {
        public MemoDBHelper(Context context){
            super(context,  DATABASE_NAME,
                    null, DATABASE_VERSION );
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // 2. 테이블 생성
            Log.d("TAG", "onCreate 호출");
            String sql = "create table " + TABLE_NAME + " ( "
                    + " id integer PRIMARY KEY autoincrement,"
                    + " title text,"
                    + " content text, "
                    + " recent inteager DEFAULT 1, "  // 1 : 최근
                    + " date  TIMESTAMP DEFAULT (datetime('now', 'localtime')) )";
            /* 데이터베이스 객체로 sql 실행 */

            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // 테이블 삭제 후 재생성
            Log.d("TAG", "onUpgrade() 호출");
            String sql = "drop table if exists " + TABLE_NAME;
            db.execSQL(sql);
            onCreate(db);
        }
    }
}
