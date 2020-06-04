package com.coolftc.buildinfo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView holdValue;
        holdValue = findViewById(R.id.bi_device);
        holdValue.setText(String.format(getResources().getString(R.string.lbl_device), Build.DEVICE));

        holdValue = findViewById(R.id.bi_display);
        holdValue.setText("DISPLAY: " + Build.DISPLAY + screenPixels() + "\n Screen Density: " + getScreenDensity());

        holdValue = findViewById(R.id.bi_hardware);
        holdValue.setText("HARDWARE: " + Build.HARDWARE);

        holdValue = findViewById(R.id.bi_modelid);
        holdValue.setText("ID: " + Build.ID);

        holdValue = findViewById(R.id.bi_manufacturer);
        holdValue.setText("MANUFACTURER: " + Build.MANUFACTURER);

        holdValue = findViewById(R.id.bi_model);
        holdValue.setText("MODEL: " + Build.MODEL);

        holdValue = findViewById(R.id.bi_product);
        holdValue.setText("PRODUCT: " + Build.PRODUCT);

        holdValue = findViewById(R.id.bi_osversion);
        holdValue.setText("OS VER: v" + Build.VERSION.RELEASE + " (api " + Build.VERSION.SDK_INT + ")");

        holdValue = findViewById(R.id.bi_radio);
        holdValue.setText("RADIO USED: " + radioInUse());

        holdValue = findViewById(R.id.bi_network);
        holdValue.setText("NETWORK: " + networkOperator() + "\n  status: " + networkConnection() + "\n  roaming: " + isRoamingNow() + "\n  signal: " + signalStrength());


        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            ServiceState ss = tm.getServiceState();
            int abc = ss.getState();
        }

        int sim = tm.getSimState();

        int carrierid;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // https://android.googlesource.com/platform/packages/providers/TelephonyProvider/+/master/assets/carrier_list.textpb
            carrierid = tm.getCarrierIdFromSimMccMnc();
            if(carrierid > 0) {
                int j = carrierid;
                String t = "";
            }
        }

    }

    private String radioInUse(){
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if(tm == null) return "Not Accessible";

            int phoneType = tm.getPhoneType();

            switch (phoneType) {
                case (TelephonyManager.PHONE_TYPE_CDMA):
                    return "CDMA";
                case (TelephonyManager.PHONE_TYPE_GSM):
                    return "GSM";
                case (TelephonyManager.PHONE_TYPE_NONE):
                    return "NONE";
                default:
                    return "Unknown";
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String networkConnection() {
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if(tm == null) return "Not Accessible";

            int dataStatus = tm.getDataState();

            switch (dataStatus) {
                case (TelephonyManager.DATA_DISCONNECTED):
                    return "Disconnected";
                case (TelephonyManager.DATA_CONNECTING):
                    return "Connecting";
                case (TelephonyManager.DATA_CONNECTED):
                    return "Connected";
                case (TelephonyManager.DATA_SUSPENDED):
                    return "Suspended";
                default:
                    return "Unknown";
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String isRoamingNow(){
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if(tm == null) return "False";
            return tm.isNetworkRoaming() ? "True" : "False";
        } catch (Exception ex) {
            return "False";
        }
    }

    private String signalStrength() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if(tm == null) return "False";
                SignalStrength tower = tm.getSignalStrength();
                int level = tower != null ? tower.getLevel() : 0;
                StringBuilder amt = new StringBuilder();
                for (int i = 0; i <= level; ++i) {
                    amt.append("*");
                }
                return amt.toString();
            } else {
                return "";
            }
        } catch (Exception ex) {
            return "Unknown";
        }
    }

    private String networkOperator(){
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if(tm == null) return "Unknown";
            String holdContry = tm.getNetworkCountryIso().length() > 0 ? tm.getNetworkCountryIso() : tm.getSimCountryIso();
            return tm.getNetworkOperatorName() + " (" + holdContry + ")";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String screenPixels(){
        try {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

            return String.format(Locale.US, "\n  height: %d, width: %d", displayMetrics.heightPixels, displayMetrics.widthPixels);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     *  The density definitions can be found here https://developer.android.com/reference/android/util/DisplayMetrics.html#summary
     *  Probably just need to look for higher densities over time.
     *  To use as a Function, make it static and require a Context be passed in.
     */
    public String getScreenDensity()
    {
        String density;
        // There are a bunch of screen specific metrics in getDisplayMetrics().
        switch (getResources().getDisplayMetrics().densityDpi)
        {
            case DisplayMetrics.DENSITY_LOW:
                density = "LDPI";
                break;
            case DisplayMetrics.DENSITY_140:
                density = "LDPI - MDPI";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                density = "MDPI";
                break;
            case DisplayMetrics.DENSITY_180:
            case DisplayMetrics.DENSITY_200:
            case DisplayMetrics.DENSITY_220:
                density = "MDPI - HDPI";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                density = "HDPI";
                break;
            case DisplayMetrics.DENSITY_260:
            case DisplayMetrics.DENSITY_280:
            case DisplayMetrics.DENSITY_300:
                density = "HDPI - XHDPI";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                density = "XHDPI";
                break;
            case DisplayMetrics.DENSITY_340:
            case DisplayMetrics.DENSITY_360:
            case DisplayMetrics.DENSITY_400:
            case DisplayMetrics.DENSITY_420:
            case DisplayMetrics.DENSITY_440:
                density = "XHDPI - XXHDPI";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                density = "XXHDPI";
                break;
            case DisplayMetrics.DENSITY_560:
            case DisplayMetrics.DENSITY_600:
                density = "XXHDPI - XXXHDPI";
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                density = "XXXHDPI";
                break;
            case DisplayMetrics.DENSITY_TV:
                density = "TVDPI";
                break;
            default:
                density = "UNKNOWN";
                break;
        }

        return density;
    }
}
