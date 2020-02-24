package com.blisscom.gourava.jaiho.infra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blisscom.gourava.jaiho.model.UserPoojaBookingHistoryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gourava on 1/9/17.
 */

public class PojoDbHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PojoDb";

    // Contacts table name
    private static final String TABLE_USER_POOJA_BOOKING_HISTORY = "userPoojaBookingHistory";

    // userPoojaBookingHistory Table Columns names
    private static final String KEY_id = "id";
    private static final String KEY_poojaBookingRequestId = "poojaBookingRequestId";
    private static final String KEY_userId = "userId";
    private static final String KEY_poojaAddress = "poojaAddress";
    private static final String KEY_poojaId = "poojaId";
    private static final String KEY_bookingRequestTime = "bookingRequestTime";
    private static final String KEY_poojaStartTime = "poojaStartTime";
    private static final String KEY_poojaLanguage = "poojaLanguage";
    private static final String KEY_prepBy = "prepBy";
    private static final String KEY_poojaAddressLongitude = "poojaAddressLongitude";
    private static final String KEY_poojaAddressLatitude = "poojaAddressLatitude";
    private static final String KEY_poojaBookingStatus = "poojaBookingStatus";
    private static final String KEY_priestName = "priestName";
    private static final String KEY_priestPhoneNumber = "priestPhoneNumber";
    private static final String KEY_offeredPrice = "offeredPrice";


    public PojoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_POOJA_BOOKING_HISTORY_TABLE = "CREATE TABLE " + TABLE_USER_POOJA_BOOKING_HISTORY + "("
                + KEY_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_poojaBookingRequestId + " TEXT,"
                + KEY_userId + " TEXT,"
                + KEY_poojaAddress + " TEXT,"
                + KEY_poojaId + " INTEGER,"
                + KEY_bookingRequestTime + " TEXT,"
                + KEY_poojaStartTime + " TEXT,"
                + KEY_poojaLanguage + " TEXT,"
                + KEY_prepBy + " TEXT,"
                + KEY_poojaAddressLongitude + " FLOAT,"
                + KEY_poojaAddressLatitude + " FLOAT,"
                + KEY_poojaBookingStatus + " TEXT,"
                + KEY_priestName + " TEXT,"
                + KEY_priestPhoneNumber + " TEXT,"
                + KEY_offeredPrice+ " INTEGER "
                + ")";
        db.execSQL(CREATE_USER_POOJA_BOOKING_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_POOJA_BOOKING_HISTORY);

        // Create tables again
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Adding new item
    public void addUserPoojaBookingHistoryItem(UserPoojaBookingHistoryItem successfulBooking) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_poojaBookingRequestId, successfulBooking.getPoojaBookingRequestId());
        values.put(KEY_userId, successfulBooking.getUserId());
        values.put(KEY_poojaAddress, successfulBooking.getPoojaAddress());
        values.put(KEY_poojaId, successfulBooking.getPoojaId());
        values.put(KEY_bookingRequestTime, successfulBooking.getBookingRequestTime());
        values.put(KEY_poojaStartTime, successfulBooking.getPoojaStartTime());
        values.put(KEY_poojaLanguage, successfulBooking.getPoojaLanguage());
        values.put(KEY_prepBy, successfulBooking.getPrepBy());
        values.put(KEY_poojaAddressLongitude, successfulBooking.getPoojaAddressLongitude());
        values.put(KEY_poojaAddressLatitude, successfulBooking.getPoojaAddressLatitude());
        values.put(KEY_poojaBookingStatus, successfulBooking.getPoojaBookingStatus());
        values.put(KEY_priestName, successfulBooking.getPriestName());
        values.put(KEY_priestPhoneNumber, successfulBooking.getPriestPhoneNumber());
        values.put(KEY_offeredPrice, successfulBooking.getOfferedPrice());

        // Inserting Row
        db.insert(TABLE_USER_POOJA_BOOKING_HISTORY, null, values);
        db.close(); // Closing database connection
    }

    public void addUserPoojaBookingHistoryItems(List<UserPoojaBookingHistoryItem> successfulBookings) {
        SQLiteDatabase db = this.getWritableDatabase();
        for(UserPoojaBookingHistoryItem successfulBooking : successfulBookings) {
            ContentValues values = new ContentValues();
            values.put(KEY_poojaBookingRequestId, successfulBooking.getPoojaBookingRequestId());
            values.put(KEY_userId, successfulBooking.getUserId());
            values.put(KEY_poojaAddress, successfulBooking.getPoojaAddress());
            values.put(KEY_poojaId, successfulBooking.getPoojaId());
            values.put(KEY_bookingRequestTime, successfulBooking.getBookingRequestTime());
            values.put(KEY_poojaStartTime, successfulBooking.getPoojaStartTime());
            values.put(KEY_poojaLanguage, successfulBooking.getPoojaLanguage());
            values.put(KEY_prepBy, successfulBooking.getPrepBy());
            values.put(KEY_poojaAddressLongitude, successfulBooking.getPoojaAddressLongitude());
            values.put(KEY_poojaAddressLatitude, successfulBooking.getPoojaAddressLatitude());
            values.put(KEY_poojaBookingStatus, successfulBooking.getPoojaBookingStatus());
            values.put(KEY_priestName, successfulBooking.getPriestName());
            values.put(KEY_priestPhoneNumber, successfulBooking.getPriestPhoneNumber());
            values.put(KEY_offeredPrice, successfulBooking.getOfferedPrice());

            // Inserting Row
            db.insert(TABLE_USER_POOJA_BOOKING_HISTORY, null, values);
        }
        db.close(); // Closing database connection
    }

    // Getting single contact
    public UserPoojaBookingHistoryItem getUserPoojaBookingItem(String poojaBookingRequestId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER_POOJA_BOOKING_HISTORY,
                new String[] {KEY_poojaBookingRequestId,
                        KEY_userId,KEY_poojaAddress, KEY_poojaId, KEY_bookingRequestTime, KEY_poojaStartTime , KEY_poojaLanguage,
                        KEY_prepBy,KEY_poojaAddressLongitude, KEY_poojaAddressLatitude,  KEY_poojaBookingStatus, KEY_priestName,
                        KEY_priestPhoneNumber, KEY_offeredPrice },
                KEY_poojaBookingRequestId + "=?",
                new String[] { poojaBookingRequestId },
                null,
                null,
                null,
                KEY_poojaBookingRequestId + " DESC ");
        if (cursor != null)
            cursor.moveToFirst();

        UserPoojaBookingHistoryItem bookingHistory = new UserPoojaBookingHistoryItem(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getFloat(8),
                cursor.getFloat(9),
                cursor.getString(10),
                cursor.getString(11),
                cursor.getString(12),
                cursor.getInt(13));
        // return contact
        return bookingHistory;
    }

    // Getting All userPoojaBookingHistoryItems
    public List<UserPoojaBookingHistoryItem> getAllUserPoojaBookingHistoryItem() {
        List<UserPoojaBookingHistoryItem> bookingHistoryList = new ArrayList<UserPoojaBookingHistoryItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER_POOJA_BOOKING_HISTORY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserPoojaBookingHistoryItem userPoojaBookingHistoryItem = new UserPoojaBookingHistoryItem();
                userPoojaBookingHistoryItem.setId(cursor.getInt(0));
                userPoojaBookingHistoryItem.setPoojaBookingRequestId(cursor.getString(1));
                userPoojaBookingHistoryItem.setUserId(cursor.getString(2));
                userPoojaBookingHistoryItem.setPoojaAddress(cursor.getString(3));
                userPoojaBookingHistoryItem.setPoojaId(cursor.getInt(4));
                userPoojaBookingHistoryItem.setBookingRequestTime(cursor.getString(5));
                userPoojaBookingHistoryItem.setPoojaStartTime(cursor.getString(6));
                userPoojaBookingHistoryItem.setPoojaLanguage(cursor.getString(7));
                userPoojaBookingHistoryItem.setPrepBy(cursor.getString(8));
                userPoojaBookingHistoryItem.setPoojaAddressLongitude(cursor.getFloat(9));
                userPoojaBookingHistoryItem.setPoojaAddressLatitude(cursor.getFloat(10));
                userPoojaBookingHistoryItem.setPoojaBookingStatus(cursor.getString(11));
                userPoojaBookingHistoryItem.setPriestName(cursor.getString(12));
                userPoojaBookingHistoryItem.setPriestPhoneNumber(cursor.getString(13));
                userPoojaBookingHistoryItem.setOfferedPrice(cursor.getInt(14));
                bookingHistoryList.add(userPoojaBookingHistoryItem);
            } while (cursor.moveToNext());
        }

        // return userPoojaBookingHistoryItem list
        return bookingHistoryList;
    }

    public int getUserPoojaBookingHistoryItemCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USER_POOJA_BOOKING_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating single userPoojaBookingHistoryItem

    public int updateUserPoojaBookingHistoryItem(UserPoojaBookingHistoryItem successfulBooking) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_poojaBookingRequestId, successfulBooking.getPoojaBookingRequestId());
        values.put(KEY_userId, successfulBooking.getUserId());
        values.put(KEY_poojaAddress, successfulBooking.getPoojaAddress());
        values.put(KEY_poojaId, successfulBooking.getPoojaId());
        values.put(KEY_bookingRequestTime, successfulBooking.getBookingRequestTime());
        values.put(KEY_poojaStartTime, successfulBooking.getPoojaStartTime());
        values.put(KEY_poojaLanguage, successfulBooking.getPoojaLanguage());
        values.put(KEY_prepBy, successfulBooking.getPrepBy());
        values.put(KEY_poojaAddressLongitude, successfulBooking.getPoojaAddressLongitude());
        values.put(KEY_poojaAddressLatitude, successfulBooking.getPoojaAddressLatitude());
        values.put(KEY_poojaBookingStatus, successfulBooking.getPoojaBookingStatus());
        values.put(KEY_priestName, successfulBooking.getPriestName());
        values.put(KEY_priestPhoneNumber, successfulBooking.getPriestPhoneNumber());
        values.put(KEY_offeredPrice, successfulBooking.getOfferedPrice());

        // updating row
        return db.update(TABLE_USER_POOJA_BOOKING_HISTORY, values, KEY_poojaBookingRequestId + " = ?",
                new String[] { String.valueOf(successfulBooking.getPoojaBookingRequestId()) });
    }


    // Deleting single contact
    public void deleteUserPoojaBookingHistoryItem(UserPoojaBookingHistoryItem successfulBooking) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_POOJA_BOOKING_HISTORY, KEY_poojaBookingRequestId + " = ?",
                new String[] { String.valueOf(successfulBooking.getPoojaBookingRequestId()) });
        db.close();
    }
    public void deleteAllUserPoojaBookingHistoryItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_POOJA_BOOKING_HISTORY, "1",null );
        db.close();
    }
}
