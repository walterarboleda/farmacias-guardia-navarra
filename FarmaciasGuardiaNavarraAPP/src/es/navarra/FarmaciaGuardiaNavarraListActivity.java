package es.navarra;

	import android.app.ListActivity;
	import android.content.Intent;
	import android.content.SharedPreferences;
	import android.os.Bundle;
	import android.preference.PreferenceManager;
	import android.view.View;
	import android.view.Window;
	import android.view.WindowManager;
	import android.widget.ListView;

	


	public class FarmaciaGuardiaNavarraListActivity extends ListActivity {
	                
			FarmaciaGuardiaNavarraData Data;
			FarmaciaGuardiaNavarraAdapter adapter;
	        boolean byDistance = false;
	        
	        public void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	    

	                // No Titlebar
	                requestWindowFeature(Window.FEATURE_NO_TITLE);
	                requestWindowFeature(Window.FEATURE_PROGRESS);
	            
	            setContentView(R.layout.farmaciasguardianavarra_list);
	        
	        adapter = new FarmaciaGuardiaNavarraAdapter(getApplicationContext());
	                setListAdapter(adapter);
	                
	        Data = FarmaciaGuardiaNavarraData.getInstance(this);
	    }
	        
	        @Override
	        protected void onResume() {
	                super.onResume();
	                
	                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	                Boolean fullscreen = sharedPref.getBoolean("fullscreen", false);
	                adapter.notifyDataSetChanged();
	                
	                // No Statusbar
	                if (fullscreen) {
	                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	                } else {
	                        getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	                }
	        }

	        @Override
	        protected void onPause() {
	                super.onPause();

	        }

	        @Override
	        protected void onListItemClick(ListView l, View v, int position, long id) {
	                super.onListItemClick(l, v, position, id);

	                Intent intent = new Intent(this, FarmaciaGuardiaNavarraDetailActivity.class);
	                Bundle bundle = new Bundle();
	                bundle.putInt("selectedPlace", position);
	                bundle.putBoolean("calledFromMap", false);
	                intent.putExtras(bundle);
	                startActivity(intent);
	        }
	        
	        public void onBackAction(View v) {
	                finish();
	        }
	        
	        public void onPreferencesAction(View v) {
	                Intent intent = new Intent(this, PreferencesActivity.class);
	                startActivity(intent);
	        }
	}
