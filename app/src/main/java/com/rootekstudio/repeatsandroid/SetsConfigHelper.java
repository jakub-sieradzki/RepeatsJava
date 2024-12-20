package com.rootekstudio.repeatsandroid;

import android.annotation.SuppressLint;
import android.content.Context;

import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SingleSetInfo;
import com.rootekstudio.repeatsandroid.database.Values;
import com.rootekstudio.repeatsandroid.notifications.NotificationsScheduler;
import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SetsConfigHelper {
    private Context context;
    private RepeatsDatabase DB;
    private SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private SimpleDateFormat creationDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SetsConfigHelper(Context context) {
        this.context = context;
        DB = RepeatsDatabase.getInstance(context);
    }

    public RepeatsDatabase getRepeatsDatabase() {
        return DB;
    }

    @SuppressLint("SimpleDateFormat")
    public String createNewSet(boolean addEmptyItem, String setName) {
        String id = "R" + idFormat.format(new Date());
        String creationDate = creationDateFormat.format(new Date());

        SingleSetInfo list;
        if (Locale.getDefault().toString().equals("pl_PL")) {
            list = new SingleSetInfo(id, setName, creationDate, 0, "pl_PL", "en_GB");
        } else {
            list = new SingleSetInfo(id, setName, creationDate, 0, "en_US", "es_ES");
        }

        //Registering set in database
        DB.createSet(id);
        DB.addSetToSetsInfoAndCalendar(list);

        if(addEmptyItem) {
            //Adding first empty item to set
            DB.addEmptyItemToSet(id);
        }

        JsonFile.putSetToJSON(context, id);

        return id;
    }

    public String createTempSet() {
        String date = idFormat.format(new Date());
        String setID = "temp" + date;
        DB.createSet(setID);
        DB.addEmptyItemToSet(setID);

        return setID;
    }

    public void deleteSet(String setID) {
        ArrayList<String> allImages = DB.getAllImages(setID);
        for (int i = 0; i < allImages.size(); i++) {
            String imgName = allImages.get(i);
            File file = new File(context.getFilesDir(), imgName);
            file.delete();
        }

        DB.deleteSetFromDatabase(setID);
        DB.deleteSet(setID);
        JsonFile.removeSetFromJSON(context, setID);

        //if there is no set left in database, turn off notifications
        if (DB.itemsInSetCount(Values.sets_info) == 0) {
            NotificationsScheduler.stopNotifications(context);
            SharedPreferencesManager.getInstance(context).setListNotifi("0");
        }
    }
}
