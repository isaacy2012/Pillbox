package com.innerCat.pillBox.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.innerCat.pillBox.BuildConfig;
import com.innerCat.pillBox.R;
import com.innerCat.pillBox.factories.SharedPreferencesFactory;

public class Updates {
    /**
     * Sets update.
     *
     * @param context the context
     * @param seen    the seen
     */
    private static void setUpdate(Context context, boolean seen) {
        SharedPreferences.Editor editor = SharedPreferencesFactory.getSP(context).edit();
        editor.putBoolean(BuildConfig.VERSION_NAME, seen);
        editor.apply();
    }

    /**
     * Set a particular update as seen
     *
     * @param context the context
     */
    public static void setUpdateSeen(Context context) {
        setUpdate(context, true);
    }

    /**
     * Set a particular update as unseen
     *
     * @param context the context
     */
    public static void setUpdateUnseen(Context context) {
        setUpdate(context, false);
    }

    public static boolean shouldShowUpdateDialog(Context context) {
        SharedPreferences sharedPreferences = SharedPreferencesFactory.getSP(context);
        boolean ret;
        // only if not first time
        ret = !sharedPreferences.getBoolean(context.getString(R.string.sp_should_show_onboarding), true);
        // and there has been an update
        ret &= !sharedPreferences.getBoolean(BuildConfig.VERSION_NAME, false);
        return ret;
    }

    public static String getUpdateBodyString() {
        return "" +
                "<b>Bug Fixes</b>" +
                "<br>" +
                "- Fixed a bug where merging refills wouldn't update properly" +
                "<br>" +
                "- Fixed a bug where refills of items weren't deleted with the items." +
                "<br>" +
                "- Fixed a bug where the edit icon was shown on the refill page" +
                "<br>" +
                "- Fixed a bug where this update dialog was shown when it was the users' first time ever using the app" +
                "<br>" +
                "<br>" +
                "If you have any bug reports or feature requests, feel free to leave a review or post an issue on our <a href=\"https://github.com/isaacy2012/Pillbox\">GitHub repository</a>." +
                "<br>" +
                "<br>" +
                "Thanks for using pillBox!";
    }
}
