package com.android.aaroo.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPrefrence {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private static final String PREF_NAME = "Aaroo_APP_PREF";

    public static final String DEVICE_ID = "device_id";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String SMS_STATUS = "smsStatus";
    public static final String FINGER_LOCK_SET = "finger_lock";
    public static final String PIN_SET = "pinSet";
    public static final String PIN_SET_STATUS = "pinset_status";


    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    public static final String KEY_MOBILE = "mobile";
    private static final String KEY_USER_IMAGE = "user_image";
    private static final String KEY_CONNECTION_REQ = "connection_request";
    private static final String KEY_PROFILE_ID = "profile_id";
    private static final String KEY_BIZ_YEAR = "__BIZ_YEAR";
    private static final String KEY_PINCODE = "__PINCODE";
    private static final String KEY_VERIFICATION = "__verification";
    private static final String KEY_BIZ_NAMES = "__BIZ_NAMES";
    private static final String KEY_BIZ_IS_SELLER = "__BIZ_IS_SELLER";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_CAT_CLASSID = "cat_classID";
    public static final String KEY_CAT_CLASSID2 = "cat_classID2";
    public static String USER_DETAILS = "user_details";
    public static String CART_COUNT = "cart_count";

    ///////////////General Setting ///////////////////////
    public static String GENERAL_SETTING_ID = "general_settings_id";
    public static String GENERAL_SETTING_ADDRESS = "general_settings_address";
    public static String GENERAL_SETTING_PHONE_NO = "general_settings_phoneNo";
    public static String GENERAL_SETTING_APP_LINK = "general_settings_app_link";
    public static String GENERAL_SETTING_DELIV_DAYS_COUNT = "general_settings_app_delivery_days_count";
    public static String GENERAL_SETTING_APPVERSION = "general_settings_appversion";
    ///////////////General Setting ///////////////////////

    public AppPrefrence(Context context){
       this._context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(_context);
         pref = context.getSharedPreferences(PREF_NAME, Context.MODE_MULTI_PROCESS);
        editor = pref.edit();
    }

    public void setSmsStatus(String value) {
        editor.putString(SMS_STATUS, value);
        editor.commit();
    }

    public String getSmsStatus() {
        if (pref != null) {
            return pref.getString(SMS_STATUS, "");
        }
        return "";
    }

    public void setFingerLock(String value) {
        editor.putString(FINGER_LOCK_SET, value);
        editor.commit();
    }

    public String getFingerLock() {
        if (pref != null) {
            return pref.getString(FINGER_LOCK_SET, "");
        }
        return "";
    }

 public void setPinset(String value) {
        editor.putString(PIN_SET, value);
        editor.commit();
    }

    public String getPinset() {
        if (pref != null) {
            return pref.getString(PIN_SET, "");
        }
        return "";
    }

    public void setPinsetStatus(String value) {
        editor.putString(PIN_SET_STATUS, value);
        editor.commit();
    }

    public String getPinsetStatus() {
        if (pref != null) {
            return pref.getString(PIN_SET_STATUS, "");
        }
        return "";
    }


    public void setDeviceId(String value) {
        editor.putString(DEVICE_ID, value);
        editor.commit();
    }

    public String getDeviceId() {
        if (pref != null) {
            return pref.getString(DEVICE_ID, "");
        }
        return "";
    }

    public void setFCM_TOKEN(String value) {
        editor.putString(FCM_TOKEN, value);
        editor.commit();
    }

    public String getFCM_TOKEN() {
        if (pref != null) {
            return pref.getString(FCM_TOKEN, "");
        }
        return "";
    }

    public void setIsLogin(String value) {
        editor.putString(IS_LOGIN, value);
        editor.commit();
    }

    public String getIsLogin() {
        if (pref != null) {
            return pref.getString(IS_LOGIN, "");
        }
        return "";
    }

    public void setUserID(String value) {
        editor.putString(KEY_USER_ID, value);
        editor.commit();
    }

    public String getUserID() {
        if (pref != null) {
            return pref.getString(KEY_USER_ID, "");
        }
        return "";
    }

    public void setEmail(String value) {
        editor.putString(KEY_EMAIL, value);
        editor.commit();
    }

    public String getEmail() {
        if (pref != null) {
            return pref.getString(KEY_EMAIL, "");
        }
        return "";
    }

    public void setUsername(String value) {
        editor.putString(KEY_USER_NAME, value);
        editor.commit();
    }

    public String getUsername() {
        if (pref != null) {
            return pref.getString(KEY_USER_NAME, "");
        }
        return "";
    }


    public void setMobile(String value) {
        editor.putString(KEY_MOBILE, value);
        editor.commit();
    }

    public String getMobile() {
        if (pref != null) {
            return pref.getString(KEY_MOBILE, "");
        }
        return "";
    }


    public void setPincode(String value) {
        editor.putString(KEY_PINCODE, value);
        editor.commit();
    }

    public String getPincode() {
        if (pref != null) {
            return pref.getString(KEY_PINCODE, "");
        }
        return "";
    }


    public void setUserProfile(String value) {
        editor.putString(KEY_USER_IMAGE, value);
        editor.commit();
    }

    public String getUserProfile() {
        if (pref != null) {
            return pref.getString(KEY_USER_IMAGE, "");
        }
        return "";
    }
    public void setRoll(String value) {
        editor.putString(KEY_BIZ_IS_SELLER, value);
        editor.commit();
    }

    public String getRoll() {
        if (pref != null) {
            return pref.getString(KEY_BIZ_IS_SELLER, "");
        }
        return "";
    }

    public void setBizName(String value) {
        editor.putString(KEY_BIZ_NAMES, value);
        editor.commit();
    }

    public String getBizName() {
        if (pref != null) {
            return pref.getString(KEY_BIZ_NAMES, "");
        }
        return "";
    }

    public void setVerification(String value) {
        editor.putString(KEY_VERIFICATION, value);
        editor.commit();
    }

    public String getVerification() {
        if (pref != null) {
            return pref.getString(KEY_VERIFICATION, "");
        }
        return "";
    }

    public void setConnectionReq(String value) {
        editor.putString(KEY_CONNECTION_REQ, value);
        editor.commit();
    }

    public String getConnectionReq() {
        if (pref != null) {
            return pref.getString(KEY_CONNECTION_REQ, "");
        }
        return "";
    }

    public void setProfileID(String value) {
        editor.putString(KEY_PROFILE_ID, value);
        editor.commit();
    }

    public String getProfileID() {
        if (pref != null) {
            return pref.getString(KEY_PROFILE_ID, "");
        }
        return "";
    }

    public void setCat_Class_ID(String value) {
        editor.putString(KEY_CAT_CLASSID, value);
        editor.commit();
    }

    public String getCat_Class_ID() {
        if (pref != null) {
            return pref.getString(KEY_CAT_CLASSID, "");
        }
        return "";
    }

    public void setCat_Class_ID2(String value) {
        editor.putString(KEY_CAT_CLASSID2, value);
        editor.commit();
    }

    public String getCat_Class_ID2() {
        if (pref != null) {
            return pref.getString(KEY_CAT_CLASSID2, "");
        }
        return "";
    }


    public void setGENERAL_SETTING_ID(String value) {
        editor.putString(GENERAL_SETTING_ID, value);
        editor.commit();
    }

    public String getGENERAL_SETTING_ID() {
        if (pref != null) {
            return pref.getString(GENERAL_SETTING_ID, "");
        }
        return "";
    }

 public void setGENERAL_SETTING_ADDRESS(String value) {
        editor.putString(GENERAL_SETTING_ADDRESS, value);
        editor.commit();
    }

    public String getGENERAL_SETTING_ADDRESS() {
        if (pref != null) {
            return pref.getString(GENERAL_SETTING_ADDRESS, "");
        }
        return "";
    }



    public void setGENERAL_SETTING_PHONE_NO(String value) {
        editor.putString(GENERAL_SETTING_PHONE_NO, value);
        editor.commit();
    }

    public String getGENERAL_SETTING_PHONE_NO() {
        if (pref != null) {
            return pref.getString(GENERAL_SETTING_PHONE_NO, "");
        }
        return "";
    }




    public void setGENERAL_SETTING_APP_LINK(String value) {
        editor.putString(GENERAL_SETTING_APP_LINK, value);
        editor.commit();
    }

    public String getGENERAL_SETTING_APP_LINK() {
        if (pref != null) {
            return pref.getString(GENERAL_SETTING_APP_LINK, "");
        }
        return "";
    }



    public void setGENERAL_SETTING_DELIV_DAYS_COUNT(String value) {
        editor.putString(GENERAL_SETTING_DELIV_DAYS_COUNT, value);
        editor.commit();
    }

    public String getGENERAL_SETTING_DELIV_DAYS_COUNT() {
        if (pref != null) {
            return pref.getString(GENERAL_SETTING_DELIV_DAYS_COUNT, "");
        }
        return "";
    }



    public void setGENERAL_SETTING_APPVERSION(String value) {
        editor.putString(GENERAL_SETTING_APPVERSION, value);
        editor.commit();
    }

    public String getGENERAL_SETTING_APPVERSION() {
        if (pref != null) {
            return pref.getString(GENERAL_SETTING_APPVERSION, "");
        }
        return "";
    }


    public void setCART_COUNT(String value) {
        editor.putString(CART_COUNT, value);
        editor.commit();
    }

    public String getCART_COUNT() {
        if (pref != null) {
            return pref.getString(CART_COUNT,"");
        }
        return "";
    }

}

