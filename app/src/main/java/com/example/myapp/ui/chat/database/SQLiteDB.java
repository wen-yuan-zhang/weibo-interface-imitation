package com.example.myapp.ui.chat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SQLiteDB {
    private static SQLiteDB sqliteDB;

    private SQLiteDatabase db;

    private SQLiteDB(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static SQLiteDB getInstance(Context context) {
        if (sqliteDB == null) {
            sqliteDB = new SQLiteDB(context);
        }
        return sqliteDB;
    }

    public synchronized void closeDB() {
        if (db != null && db.isOpen()) db.close();
    }


    // isRcv, fromId, createTime, message
    public void addChat(Map<String, Object> value) {
        if (db != null && db.isOpen()) {
            ContentValues cv = new ContentValues();
            cv.put("isRcv", (Integer) value.get("isRcv"));
            cv.put("targetId", (Integer) value.get("fromId"));
            if (value.get("createTime") != null)
                cv.put("createTime", (Long) value.get("createTime"));
            cv.put("message", (String) value.get("message"));
            db.insert("Chat", null, cv);
        }
    }

    // 添加聊天记录
    public void addChat(int isRcv, int targetId, Long createTime, String message) {
        if (db != null && db.isOpen()) {
            ContentValues cv = new ContentValues();
            cv.put("isRcv", isRcv);
            cv.put("targetId", targetId);
            if (createTime != null) cv.put("createTime", createTime);
            cv.put("message", message);
            db.insert("Chat", null, cv);
        }
    }

    // 删除和某人的聊天记录
    public void deleteChat(int targetId) {
        if (db != null && db.isOpen()) {
            db.delete("Chat", "targetId=?", new String[]{String.valueOf(targetId)});
        }
    }

    // 查询聊天记录
    public List<Map<String, Object>> fetchChat(int targetId, Long startTime, int start, int count) {
        if (db != null && db.isOpen()) {
            if (startTime == null) startTime = System.currentTimeMillis();
            Cursor cursor = db.query("Chat", new String[]{"isRcv", "createTime", "message"},
                    "targetId = ? and createTime < ?", new String[]{String.valueOf(targetId), String.valueOf(startTime)},
                    null, null, "id desc", start + "," + count);
            List<Map<String, Object>> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<>();
                int isRcv = cursor.getInt(cursor.getColumnIndex("isRcv"));
                long createTime = cursor.getLong(cursor.getColumnIndex("createTime"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                map.put("isRcv", isRcv);
                map.put("createTime", createTime);
                map.put("message", message);
                result.add(map);
            }
            cursor.close();
//            for(Map<String, Object> r: result){
//                System.out.println(r.toString());
//            }
            return result;
        }
        return new ArrayList<>();
    }

    // 返回一个 List 按照最后交流的时间顺序，越靠近现在的排名越前
    public List<Map<String, Object>> fetchUser() {
        if (db != null && db.isOpen()) {
            Cursor cursor = db.rawQuery("SELECT targetId, MAX(createTime) as maxTime FROM Chat GROUP BY targetId ORDER BY maxTime DESC", new String[]{});
            List<Map<String, Object>> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<>();
                int targetId = cursor.getInt(cursor.getColumnIndex("targetId"));
                long maxTime = cursor.getLong(cursor.getColumnIndex("maxTime"));
                map.put("targetId", targetId);
                map.put("maxTime", maxTime);
                result.add(map);
            }
            cursor.close();
//            for(Map<String, Object> r : result){
//                System.out.println(r.toString());
//            }
            return result;
        }
        return new ArrayList<>();
    }


}
