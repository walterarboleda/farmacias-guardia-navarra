package es.navarra;


	import android.content.Context;
	import android.content.Intent;
	import android.graphics.Canvas;
	import android.graphics.Color;
	import android.graphics.Paint;
	import android.graphics.Point;
	import android.graphics.Rect;
	import android.graphics.Typeface;
	import android.graphics.drawable.Drawable;
	import android.os.Bundle;
	import android.view.Gravity;
	import android.widget.Toast;

	import com.google.android.maps.GeoPoint;
	import com.google.android.maps.MapController;
	import com.google.android.maps.MapView;
	import com.google.android.maps.Overlay;
	import com.google.android.maps.Projection;

	public class FarmaciaGuardiaNavarraOverLay extends Overlay {

		FarmaciaGuardiaNavarraData Data;
		FarmaciaGuardiaNavarraMapActivity activity;
	                
	        public FarmaciaGuardiaNavarraOverLay(FarmaciaGuardiaNavarraMapActivity activity) {
	                super();
	                Data = FarmaciaGuardiaNavarraData.getInstance(activity);
	                this.activity = activity;
	        }

	        @Override
	        public void draw(Canvas canvas, MapView mapView, boolean shadow) {

	                Projection projection = mapView.getProjection();

	                if (shadow == false && Data.getLocationPoint() != null) {

	                        for (int i = 0; i < Data.getSize(); i++) {
	                                if (i != Data.getSelectedPointIndex()) {
	                                        GeoPoint geoPoint = Data.getPointToDraw(i);
	                                        Point point = new Point();

	                                        projection.toPixels(geoPoint, point);
	                                        if (point.x > -100 && point.x < canvas.getWidth() + 100
	                                                        && point.y > -100
	                                                        && point.y < canvas.getHeight() + 100)
	                                                drawMarker(i, point, canvas);
	                                }
	                        }
	                        for (int i = 0; i < Data.getSize(); i++) {
	                                if (i != Data.getSelectedPointIndex()) {
	                                        GeoPoint geoPoint = Data.getPointToDraw(i);
	                                        Point point = new Point();
	                                        projection.toPixels(geoPoint, point);
	                                        if (point.x > -100 && point.x < canvas.getWidth() + 100
	                                                        && point.y > -100
	                                                        && point.y < canvas.getHeight() + 100)
	                                                drawText(i, point, canvas, false);
	                                }
	                        }
	                }

	                if (Data.getSelectedPointIndex() != -1) {
	                        GeoPoint geoPoint = Data.getSelectedPoint();
	                        if (geoPoint == null)
	                                return;
	                        Point point = new Point();
	                        projection.toPixels(geoPoint, point);
	                        if (point.x > -50 && point.x < canvas.getWidth() + 50
	                                        && point.y > -50 && point.y < canvas.getHeight() + 50)
	                                drawMarker(Data.getSelectedPointIndex(), point, canvas);
	                        drawText(Data.getSelectedPointIndex(), point, canvas, true);
	                }

	                super.draw(canvas, mapView, shadow);
	        }

	        private void drawMarker(int index, Point point, Canvas canvas) {
	                Drawable marker = activity.getResources().getDrawable(R.drawable.wikiplace);
	                Paint paint = new Paint();
	                paint.setColor(Color.rgb(0, 146, 66));
	                canvas.drawLine(point.x, point.y - dipToPixels(activity, 46), point.x, point.y, paint);
	                paint.setColor(Color.BLACK);
	                marker.setBounds(point.x, point.y - dipToPixels(activity, 48), point.x + dipToPixels(activity, 32), point.y - dipToPixels(activity, 16));
	                marker.draw(canvas);
	        }

	        private void drawText(int index, Point point, Canvas canvas, boolean isSelected) {

	        }

	        // Busca el lugar más cercana clickada
	        @Override
	        public boolean onTap(GeoPoint point, MapView mapView) {
	                
	                // Al doble tap abre el detalle
	                int newSelectedPointIndex = Data.getNearestIndex(point);
	                if (newSelectedPointIndex != Data.getSelectedPointIndex()) {                  
	                        Toast toast = Toast.makeText(activity, R.string.tap_again_details, Toast.LENGTH_SHORT);
	                        toast.setGravity(Gravity.TOP, 0, 0);
	                        toast.show();
	                } else {
	                        // Lanza intent de detalles
	                        Intent intent = new Intent(activity, FarmaciaGuardiaNavarraDetailActivity.class);
	                        Bundle bundle = new Bundle();
	                        bundle.putInt("selectedPlace", newSelectedPointIndex);
	                        bundle.putBoolean("calledFromMap", true);
	                        intent.putExtras(bundle);
	                        activity.startActivity(intent);
	                        return true;
	                }
	                
	                Data.setSelectedPointIndex(newSelectedPointIndex);
	                if (Data.getSelectedPointIndex() != -1) {
	                        GeoPoint geoPoint = Data.getSelectedPoint();
	                        MapController mapController = mapView.getController();
	                        mapController.animateTo(geoPoint);
	                }
	                
	                int i = Data.getSelectedPointIndex();
	                if (i == -1) return false;
	                return false;
	        }
	        
	        private int dipToPixels(Context c, float dipValue) {  
	                return (int) (dipValue * c.getResources().getDisplayMetrics().density + 0.5f);  
	        }  
	}
	