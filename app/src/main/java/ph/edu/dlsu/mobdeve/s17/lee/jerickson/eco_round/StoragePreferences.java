package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Context;
import android.content.SharedPreferences;

public class StoragePreferences {

    private SharedPreferences appPreferences;
    private final String PREFS = "appPreferences";

    public StoragePreferences(Context context){
        appPreferences = context.getSharedPreferences(PREFS , Context.MODE_PRIVATE);
    }

    public void saveStringPreferences(String key, String value){
        SharedPreferences.Editor prefsEditor = appPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }
    public void saveBooleanPreferences(String key, boolean value){
        SharedPreferences.Editor prefsEditor = appPreferences.edit();
        prefsEditor.putBoolean(key , value);
        prefsEditor.apply();
    }

    public void saveIntPreferences(String key, int value){
        SharedPreferences.Editor prefsEditor = appPreferences.edit();
        prefsEditor.putInt(key , value);
        prefsEditor.apply();
    }

    public boolean getBooleanPreferences(String key){
        return(appPreferences.getBoolean(key, false));
    }

    public int getIntPreferences(String key){
        return(appPreferences.getInt(key, 0));
    }

    public String getStringPreferences(String key){
        return(appPreferences.getString(key, ""));
    }
}
