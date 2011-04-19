package es.navarra;

	import java.util.List;

	import android.R.drawable;
	import android.content.Intent;
	import android.content.SharedPreferences;
	import android.os.Bundle;
	import android.preference.PreferenceManager;
	import android.view.Menu;
	import android.view.MenuItem;
	import android.view.View;
	import android.view.Window;
	import android.view.WindowManager;


	import com.google.android.maps.GeoPoint;
	import com.google.android.maps.MapActivity;
	import com.google.android.maps.MapView;
	import com.google.android.maps.MyLocationOverlay;
	import com.google.android.maps.Overlay;


	public class FarmaciaGuardiaNavarraMapActivity extends MapActivity {
	        
	        public static final int MENU_MY_LOCATION = 1;
	        
	        MapView mapView;
	        FarmaciaGuardiaNavarraOverLay OverLay;
	        FarmaciaGuardiaNavarraData Data;
	        MyLocationOverlay myLocationOverlay;
	        boolean detectLocation;
	        
	        public void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	               
	                // No Titlebar
	                requestWindowFeature(Window.FEATURE_NO_TITLE);
	                requestWindowFeature(Window.FEATURE_PROGRESS);
	            
	            setContentView(R.layout.farmaciasguardianavarra_map);

	        mapView = (MapView)findViewById(R.id.mapview);
	        mapView.setBuiltInZoomControls(true);
	        mapView.getController().setZoom(14);
	        
	        List<Overlay> overlays = mapView.getOverlays();
	        myLocationOverlay = new MyLocationOverlay(this, mapView);
	        overlays.add(myLocationOverlay);

	        OverLay = new FarmaciaGuardiaNavarraOverLay(this);
	        overlays.add(OverLay);
	        
	        Data = FarmaciaGuardiaNavarraData.getInstance(this);
	    }
	        
	        @Override
	        protected void onResume() {
	                super.onResume();
	                
	                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	                mapView.setSatellite(sharedPref.getBoolean("satellite", false));
	                boolean fullscreen = sharedPref.getBoolean("fullscreen", false);

	                // No statusbar
	                if (fullscreen) {
	                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	                } else {
	                        getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	                }

	        myLocationOverlay.enableMyLocation();
	        if (Data.getSelectedPoint() != null) {                
	                mapView.getController().setCenter(Data.getSelectedPoint());
	        } else if (Data.getLocationPoint() != null) {
	                mapView.getController().setCenter(Data.getLocationPoint());
	        }
	        }

	        @Override
	        protected void onPause() {
	                super.onPause();
	                // To save battery
	                myLocationOverlay.disableMyLocation(); // VEry important, if not, countinues to use the GPS
	        }
	        
	        @Override
	        protected boolean isRouteDisplayed() {
	                return false;
	        }
	        
	        @Override
	        public boolean onCreateOptionsMenu(Menu menu) {
	                super.onCreateOptionsMenu(menu);

	                menu.add(0, MENU_MY_LOCATION, 0, R.string.menu_my_location).setIcon(drawable.ic_menu_mylocation);
	                
	                return true;
	        }

	        @Override
	        public boolean onOptionsItemSelected(MenuItem item) {
	                GeoPoint geoPoint = null;
	                switch (item.getItemId()) {
	                case MENU_MY_LOCATION:
	                        geoPoint = myLocationOverlay.getMyLocation();
	                        // Fallback
	                        if (geoPoint == null) geoPoint = Data.getLocationPoint();
	                if (geoPoint != null) mapView.getController().animateTo(geoPoint);
	                        return true;
	                }
	                return false;
	        }

	        public void onBackAction(View v) {
	                finish();
	        }
	        
	        public void onPreferencesAction(View v) {
	                Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
	                startActivity(intent);
	        }
	}