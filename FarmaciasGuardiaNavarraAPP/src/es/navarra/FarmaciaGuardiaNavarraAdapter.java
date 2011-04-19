package es.navarra;

	import java.text.DecimalFormat;
	import java.text.NumberFormat;

	import android.content.Context;
	import android.view.LayoutInflater;
	import android.view.View;
	import android.view.ViewGroup;
	import android.widget.BaseAdapter;
	import android.widget.LinearLayout;
	import android.widget.TextView;

	public class FarmaciaGuardiaNavarraAdapter extends BaseAdapter {

	        private Context mContext;
	        FarmaciaGuardiaNavarraData Data;

	        public FarmaciaGuardiaNavarraAdapter(Context c) {
	                mContext = c;
	                Data = FarmaciaGuardiaNavarraData.getInstance(c);
	        }

	        public int getCount() {
	                return Data.getSize();
	        }

	        public FarmaciaGuardiaNavarra getObject(int position) throws Exception {
	                return (FarmaciaGuardiaNavarra) Data.getFarmaciaGuardiaNavarra(position);
	        }
	        
	        public Object getItem(int position) {
	                return position;
	        }

	        public long getItemId(int position) {
	                return position;
	        }

	        public View getView(int position, View convertView, ViewGroup parent) {
	                LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(
	                                R.layout.farmaciasguardianavarra_adapter, parent, false);
	                
	                FarmaciaGuardiaNavarra farmacia = Data.getFarmaciaGuardiaNavarra(position);

	                TextView title = (TextView) ll.findViewById(R.id.Title);
	                title.setText(farmacia.getLocalidad()+" , "+farmacia.getDireccion());
	                
	                
	                return ll;
	        }
	}