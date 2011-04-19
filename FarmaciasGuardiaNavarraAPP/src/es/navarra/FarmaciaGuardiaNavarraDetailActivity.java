package es.navarra;


	import java.text.DecimalFormat;
	import java.text.NumberFormat;

	import android.app.Activity;
	import android.content.Intent;
	import android.content.SharedPreferences;
	import android.net.Uri;
	import android.os.Bundle;
	import android.preference.PreferenceManager;
	import android.util.Log;
	import android.view.View;
	import android.view.Window;
	import android.view.WindowManager;
	import android.widget.TextView;


	import com.google.android.maps.GeoPoint;


	public class FarmaciaGuardiaNavarraDetailActivity extends Activity {

	        public static final String TAG = "FarmaciasGuardiaNavarraDetailActivity";
	                
	        
	        FarmaciaGuardiaNavarraData Data;
	        int selectedPlace = -1;
	        boolean calledFromMap = false; 
	        
	        public void onCreate(Bundle bundle) {
	            super.onCreate(bundle);
	                
	                // No Titlebar
	                requestWindowFeature(Window.FEATURE_NO_TITLE);
	                requestWindowFeature(Window.FEATURE_PROGRESS);

	            setContentView(R.layout.farmaciasguardianavarra_detail);

	        Bundle extras = getIntent().getExtras();
	        selectedPlace = extras.getInt("selectedPlace");
	        calledFromMap = extras.getBoolean("calledFromMap");
	        
	        Data = FarmaciaGuardiaNavarraData.getInstance(this);
	        }
	        
	        @Override
	        protected void onResume() {
	                super.onResume();
	                
	                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	                Boolean fullscreen = sharedPref.getBoolean("fullscreen", false);

	                // No Statusbar
	                if (fullscreen) {
	                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	                } else {
	                        getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	                }
	                
	                Data.setSelectedPointIndex(selectedPlace);
	                FarmaciaGuardiaNavarra farmacia = Data.getFarmaciaGuardiaNavarra(selectedPlace);
	        if (farmacia == null) {
	                finish();
	                return;
	        }
	                Log.d(TAG, "ENTRA: " + farmacia.farmacia);
	                
	                TextView title = ((TextView) findViewById(R.id.Farmacia));
	                title.setText(" ");
	                
	                TextView telefono = ((TextView) findViewById(R.id.Telefono));
	                telefono.setText(farmacia.getTelefono());
	                
	                TextView direccion = ((TextView) findViewById(R.id.Direccion));
	                direccion.setText(farmacia.getDireccion());
	                
	                TextView localidad = ((TextView) findViewById(R.id.Localidad));
	                localidad.setText(farmacia.getLocalidad());

	                TextView horario = ((TextView) findViewById(R.id.Horario));
	                horario.setText(farmacia.getHorario());
	        }

	        @Override
	        protected void onPause() {
	                super.onPause();
	        }

	        public void onMapAction(View v)  {
	                if (calledFromMap) {
	                        finish();
	                } else {
	                        Intent intent = new Intent(getApplicationContext(), FarmaciaGuardiaNavarraMapActivity.class);
	                        startActivity(intent);
	                        finish();
	                }
	        }

	        public void onWebAction(View v)  {

	        }
	        
	        public void onNavigateAction(View v)  {
	                GeoPoint point = Data.getFarmaciaGuardiaNavarra(selectedPlace).getGeoPoint();

	                String uri = "google.navigation:q="+Double.valueOf(point.getLatitudeE6())/1000000+","+Double.valueOf(point.getLongitudeE6())/1000000;
	                Log.i(TAG, "URI = " + uri + "  " + point.getLatitudeE6() +" "+ point.getLongitudeE6());
	                                
	                Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
	                startActivity(myIntent);
	        }
	        
	        public void onBackAction(View v) {
	                finish();
	        }
	        
	        public void onPreferencesAction(View v) {
	                Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
	                startActivity(intent);
	        }
	}
	