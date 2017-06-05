package ch.fhnw.parking.parkyourcar;

import android.graphics.drawable.Drawable;

/**
 * Created by remo on 05.06.17.
 */

public class ParkingUtility {

    public static int getIcon(String dbId, String status) {
        if(status.equals("besetzt")) {
            int val = Integer.parseInt(dbId.substring(7)) % 3;
            if(val == 0) return R.drawable.parking_1;
            else if(val == 1) return R.drawable.parking_2;
            else return R.drawable.parking_3;
        }
        else {
            return  R.drawable.parking_free;
        }
    }
}
