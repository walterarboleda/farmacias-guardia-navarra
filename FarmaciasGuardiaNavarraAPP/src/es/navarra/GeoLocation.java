package es.navarra;


	import com.google.android.maps.GeoPoint;  
	import java.io.IOException;  
	import java.io.InputStream;  
	import org.apache.http.HttpEntity;  
	import org.apache.http.HttpResponse;  
	import org.apache.http.client.ClientProtocolException;  
	import org.apache.http.client.HttpClient;  
	import org.apache.http.client.methods.HttpGet;  
	import org.apache.http.impl.client.DefaultHttpClient;  
	import org.json.JSONArray;  
	import org.json.JSONException;  
	import org.json.JSONObject;  
	 
	/**  
	*  
	* @author Manu  
	*/  
	public class GeoLocation {  
	 
	   private static JSONObject getLocationInfo(String address) {  
	       HttpGet httpGet = new HttpGet("http://maps.google.com"  
	               + "/maps/api/geocode/json?address=" + address  
	               + "&sensor=false");  
	       return getLocation(httpGet);  
	   }  
	 
	   public static GeoPoint getGeoPoint(String address) {  
	       JSONObject jsonObject = getLocationInfo(address);  
	       Double lon = new Double(0);  
	       Double lat = new Double(0);  
	       try {  
	           lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");  
	           lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");  
	       } catch (JSONException e) {  
	           e.printStackTrace();  
	       }  
	       return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));  
	   }  
	 
	   private static JSONObject getLocationInfo(double lat, double lng) {  
	       HttpGet httpGet = new HttpGet("http://maps.google.com"  
	               + "/maps/api/geocode/json?latlng=" + lat + "," + lng  
	               + "&sensor=false");  
	       return getLocation(httpGet);  
	   }  
	 
	   public static String getAddress(double lat, double lng) {  
	       JSONObject jsonObject = getLocationInfo(lat, lng);  
	       String address = "Lugar desconocido";  
	       try {  
	           address = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getString("formatted_address");  
	       } catch (JSONException e) {  
	           e.printStackTrace();  
	       }  
	       return address;  
	   }  
	 
	   private static JSONObject getLocation(HttpGet httpGet) {  
	       HttpClient client = new DefaultHttpClient();  
	       HttpResponse response;  
	       StringBuilder stringBuilder = new StringBuilder();  
	       try {  
	           response = client.execute(httpGet);  
	           HttpEntity entity = response.getEntity();  
	           InputStream stream = entity.getContent();  
	           int b;  
	           while ((b = stream.read()) != -1) {  
	               stringBuilder.append((char) b);  
	           }  
	       } catch (ClientProtocolException e) {  
	       } catch (IOException e) {  
	       }  
	       JSONObject jsonObject = new JSONObject();  
	       try {  
	           jsonObject = new JSONObject(stringBuilder.toString());  
	       } catch (JSONException e) {  
	           e.printStackTrace();  
	       }  
	       return jsonObject;  
	   }  
	} 

