package com.gabilheri.rosbridgecontroller.app.helperClasses;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * File that will hold hand static functions that might be needed to be called @ any moment
 * Created by marcus on 4/21/14.
 * @author Marcus Gabilheri
 * @version 1.0
 * @since April, 2014.
 */
public class HandyStaticFunctions {

    public static boolean getInternetState(Activity activity) {

        ConnectivityManager conMgr =  (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            DialogFragment noInternet = new NoInternetDialog();
            noInternet.show(activity.getFragmentManager(), "noInternet");
            return false;
        }
    }
}
