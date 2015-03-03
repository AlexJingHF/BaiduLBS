package cn.lbs.alex.baidulbs.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by alex on 15/3/3.
 */
public class GPSSever extends Service implements LocationListener{

    private final static String TAG = "GPSServer";

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; //根据移动的最小距离更新

    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1;  //根据间隔的最小时间更新

    private Context mContext;

    private LocationManager mLocationManager;

    private boolean isGPSEnable = false;

    private boolean isNetworkEnable = false;

    private boolean canGetLocation = false;

    private Location mLocation;

    public GPSSever(Context context) {
        this.mContext = context;
        getLocation();
    }

    protected void getLocation(){
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        isGPSEnable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        isNetworkEnable = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable){
            //无法进行定位
            Log.d(TAG,"无法定位");
        }else {
            canGetLocation = true;
            //首先使用网络定位获取location
            if (isNetworkEnable){
                Log.d(TAG,"Network enable");
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this);
                mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (isGPSEnable){
                Log.d(TAG,"GPS enable");
                if (mLocation == null){
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this);
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        }

    }

    public void stopUsingGPS(){
        if (mLocationManager != null){
            mLocationManager.removeUpdates(this);
        }
    }

    public boolean isCanGetLocation() {
        return canGetLocation;
    }

    public Location getCurrentLocation() {
        if (mLocation != null){
            return mLocation;
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,"onLocationChanged\n 经度："+location.getLongitude()+"\t 纬度："+location.getLatitude());
        mLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG,"onStatusChanged\n provider:"+provider+"\t status:"+status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG,"onProviderEnabled\n provider:"+provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG,"onProviderDisabled\n provider:"+provider);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
