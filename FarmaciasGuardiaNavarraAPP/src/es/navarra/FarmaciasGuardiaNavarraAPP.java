package es.navarra;




	import android.app.Activity;
	import android.app.AlertDialog;
	import android.app.ProgressDialog;
	import android.content.Context;
	import android.content.DialogInterface;
	import android.content.Intent;
	import android.content.SharedPreferences;
	import android.location.Criteria;
	import android.location.Location;
	import android.location.LocationListener;
	import android.location.LocationManager;
	import android.os.Bundle;
	import android.os.Handler;
	import android.os.Message;
	import android.preference.PreferenceManager;
	import android.util.Log;
	import android.view.Gravity;
	import android.view.KeyEvent;
	import android.view.View;
	import android.view.Window;
	import android.view.WindowManager;
	import android.widget.Toast;

	
	import com.google.android.maps.GeoPoint;


	public class FarmaciasGuardiaNavarraAPP extends Activity implements LocationListener, FarmaciaGuardiaNavarraDataListener {

	        public static final String TAG = "FarmaciaGuardiaNavarraActivity";
	        
	        public static final int MESSAGE_LOADING_START = 0;
	        public static final int MESSAGE_LOADING_OK = 1;
	        public static final int MESSAGE_LOADING_ERROR = 2;
	                
	        LocationManager manager;
	        String providerFine, providerCoarse, lastLocationProvider;
	        ProgressDialog loadingDialog;
	        FarmaciaGuardiaNavarraData Data;
	        boolean fullscreen;
	        boolean updateWhenLocationAvaiable = false;
	    GeoPoint geoPoint;
	    
	        static long whenLoadedAutomatically = 0;

	        public void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	                

	                // No Titlebar
	                requestWindowFeature(Window.FEATURE_NO_TITLE);
	                requestWindowFeature(Window.FEATURE_PROGRESS);

	                setContentView(R.layout.dashboard);
	                
	                Log.d(TAG, "Empieza la creacion");        
	                
	        Data = FarmaciaGuardiaNavarraData.getInstance(this);
	    }
	        
	        @Override
	        protected void onResume() {
	                super.onResume();

	                Data.addListener(this);
	                Data.setLang(getResources().getString(R.string.geonames_lang));

	                geoPoint = null;
	                
	                Log.d(TAG, "Loading preferences");
	                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	                fullscreen = sharedPref.getBoolean("fullscreen", false);

	                // No Statusbar
	                if (fullscreen) {
	                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	                } else {
	                        getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	                }
	                
	                manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

	                Criteria criteria = new Criteria();
	                criteria.setAltitudeRequired(false);
	                criteria.setBearingRequired(false);
	                criteria.setCostAllowed(false);
	                criteria.setPowerRequirement(Criteria.POWER_LOW);
	        
	                criteria.setAccuracy(Criteria.ACCURACY_FINE);
	                providerFine = manager.getBestProvider(criteria, true);
	                Log.d(TAG, "USING location provider fine = " + providerFine);

	                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	                providerCoarse = manager.getBestProvider(criteria, true);
	                Log.d(TAG, "USING location provider coarse = " + providerCoarse);
	                        
	                if (providerFine == null && providerCoarse == null) {
	                        Toast toast = Toast.makeText(getApplicationContext(), R.string.error_location, Toast.LENGTH_LONG);
	                        toast.setGravity(Gravity.CENTER, 0, 0);
	                        toast.show();
	                } else {
	                        lastLocationProvider = null;
	                        if (providerFine != null && providerCoarse != null && providerFine.equals(providerCoarse)) providerCoarse = null;
	                        
	                        if (providerCoarse != null) manager.requestLocationUpdates(providerCoarse, 5*60000, 100, this); // update each 5 minutes
	                        if (providerFine != null) manager.requestLocationUpdates(providerFine, 5*60000, 100, this); // update each 5 minutes
	                }
	                
	                // Actualiza al iniciar y cada media hora si no se cierra la aplicación
	                if ((System.currentTimeMillis() - whenLoadedAutomatically > 30*60*1000)) {
	                        updateData();
	                        whenLoadedAutomatically = System.currentTimeMillis();
	                }
	                
	                Log.d(TAG, "Creada"); 
	        }

	        @Override
	        protected void onPause() {
	                super.onPause();
	        Data.removeListener(this);
	                
	                if (loadingDialog != null) {
	                        if (loadingDialog.isShowing()) loadingDialog.dismiss();
	                        loadingDialog = null;
	                }
	                
	                // To save battery
	                if (manager != null) manager.removeUpdates(this);
	        }

	        private void updateData() {
	                Log.d(TAG, "updateData()");
	                if (geoPoint != null) {
	                        Data.getDataFromServer(this);
	                } else {
	                        Log.d(TAG, "Delaying data update till location avaiable");
	                        updateWhenLocationAvaiable = true;
	                }
	        }

	        private GeoPoint location2GeoPoint(Location location) {
	                if (location == null) return null;
	                int latitude = (int) (location.getLatitude()*1E6);
	                int longitude = (int) (location.getLongitude()*1E6);
	                return new GeoPoint(latitude, longitude);
	        }
	        
	        public void onLocationChanged(Location location) {
	                if (lastLocationProvider == null || lastLocationProvider.equals(providerCoarse)) {
	                        lastLocationProvider = location.getProvider();
	                        Log.d(TAG, "Received location from provider " + lastLocationProvider);
	                                
	                        geoPoint = location2GeoPoint(location);
	                        Data.setLocationPoint(geoPoint);
	                        Log.d(TAG, "Location Changed GeoPoint="+geoPoint);

	                        if (updateWhenLocationAvaiable) {
	                                updateData();
	                        }
	                        updateWhenLocationAvaiable = false;
	                }
	        }

	        public void onProviderDisabled(String provider) {
	        }

	        public void onProviderEnabled(String provider) {
	        }

	        public void onStatusChanged(String provider, int status, Bundle extras) {
	        }

	        public void endQueryError() {
	                handler.sendMessage(Message.obtain(handler, MESSAGE_LOADING_ERROR));
	        }

	        public void endQueryOk() {
	                handler.sendMessage(Message.obtain(handler, MESSAGE_LOADING_OK));
	        }

	        public void startQuery() {
	                handler.sendMessage(Message.obtain(handler, MESSAGE_LOADING_START));
	        }

	        /**
	         * Called to update
	         */
	        Handler handler = new Handler() {
	                @Override
	                public void handleMessage(Message msg) {
	                        update(msg);
	                }
	        };
	        
	        void update(Message msg) {
	                switch(msg.what) {
	                case MESSAGE_LOADING_START:
	                        showLoading();
	                        break;
	                case MESSAGE_LOADING_OK:
	                        if (loadingDialog == null) return;
	                        loadingDialog.dismiss();
	                        break;
	                case MESSAGE_LOADING_ERROR:
	                        if (loadingDialog == null) return;
	                        loadingDialog.dismiss();
	                        
	                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	                        builder.setCancelable(false);
	                        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int which) {
	                                }
	                        });             
	                        builder.setTitle(R.string.error);
	                        builder.setMessage(R.string.error_data);
	                        AlertDialog errorDialog = builder.create();
	                        errorDialog.show();
	                        break;
	                }
	        }
	        
	        private void showLoading() {
	                if (loadingDialog != null && loadingDialog.isShowing()) return;
	                loadingDialog = ProgressDialog.show(this, "", getResources().getText(R.string.loading), true, true);
	                loadingDialog.show();
	        }
	        
	        @Override
	        public boolean onKeyDown(int keyCode, KeyEvent event)  {
	            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	                whenLoadedAutomatically = 0;
	            }

	            return super.onKeyDown(keyCode, event);
	        }

	        public void onMapAction(View v) {
	                Intent intent = new Intent(getApplicationContext(), FarmaciaGuardiaNavarraMapActivity.class);
	                startActivity(intent);
	        }

	        public void onListAction(View v) {
	                Intent intent = new Intent(getApplicationContext(), FarmaciaGuardiaNavarraListActivity.class);
	                startActivity(intent);
	        }
	        
	        public void onRefreshAction(View v) {
	                updateData();
	        }
	        
	        public void onPreferencesAction(View v) {
	                Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
	                startActivity(intent);
	        }
	}