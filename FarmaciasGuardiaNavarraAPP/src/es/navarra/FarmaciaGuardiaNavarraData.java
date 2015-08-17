package es.navarra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.util.Log;

import com.google.android.maps.GeoPoint;


public class FarmaciaGuardiaNavarraData implements Runnable {

	// Clase que permite la lectura del recurso del catalogo de datos abiertos
	//API para el acceso al recurso guardias.xml en el harvester de datos abiertos http://192.168.10.81/8080
	//String url = "http://192.168.10.81:8080/dataset/9c8d1690-3b3f-43f3-b264-1c9d193b283c/resource/edd6ffeb-37eb-442c-9efa-34b89072c398/download/guardias.xml"

	private static String TAG = "FarmaciasGuardiaNavarraData";
	private static FarmaciaGuardiaNavarraData instance;

	GeoPoint locationGeoPoint;
	int distance;
	String lang = "en";
	Thread thread;
	int selectedPointIndex = -1;
	ArrayList<FarmaciaGuardiaNavarra> farmaciaGuardiaNavarraList;
	FarmaciaGuardiaNavarraDataListener listener;
	SharedPreferences sharedPrefs;

	public static FarmaciaGuardiaNavarraData getInstance(Context c) {
		if (FarmaciaGuardiaNavarraData.instance == null) {
			FarmaciaGuardiaNavarraData.instance = new FarmaciaGuardiaNavarraData();
		}
		return FarmaciaGuardiaNavarraData.instance;
	}

	private FarmaciaGuardiaNavarraData() {
		farmaciaGuardiaNavarraList = new ArrayList<FarmaciaGuardiaNavarra>();
	}

	public synchronized void setLocationPoint(GeoPoint locationPoint) {
		this.locationGeoPoint = locationPoint;
	}

	public synchronized GeoPoint getLocationPoint() {
		return locationGeoPoint;
	}

	public void addListener(FarmaciaGuardiaNavarraDataListener listener) {
		this.listener = listener;
	}

	public void removeListener(FarmaciaGuardiaNavarraDataListener listener) {
		if (this.listener == listener)
			this.listener = null;
	}

	public GeoPoint getPointToDraw(int index) {
		return farmaciaGuardiaNavarraList.get(index).getGeoPoint();
	}

	public void setSelectedPointIndex(int selectedPointIndex) {
		this.selectedPointIndex = selectedPointIndex;
	}

	public int getSelectedPointIndex() {
		return selectedPointIndex;
	}

	public GeoPoint getSelectedPoint() {
		if (selectedPointIndex == -1)
			return null;
		return farmaciaGuardiaNavarraList.get(selectedPointIndex).getGeoPoint();
	}

	public int getSize() {
		return farmaciaGuardiaNavarraList.size();
	}

	public int getNearestIndex(GeoPoint point) {
		double minDist = 999999999;
		int selectedIndex = -1;
		for (int i = 0; i < farmaciaGuardiaNavarraList.size(); i++) {
			GeoPoint geoPoint = farmaciaGuardiaNavarraList.get(i).getGeoPoint();
			double dist = DistanceCalculator.distance(point, geoPoint);

			if (dist < minDist) {
				selectedIndex = i;
				minDist = dist;
			}
		}
		return selectedIndex;
	}

	public void getDataFromServer(Context c) {
		if (locationGeoPoint == null) {
			Log.d(TAG, "Not updating data because location is not set");
			return;
		}
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
		distance = Integer.valueOf(sharedPrefs.getString("distance", "20"));

		thread = new Thread(this);
		thread.start();
	}

	/**
	 * Gets data from server in JSON format
	 * 
	 * @param locationPoint
	 */
	public void run() {
		boolean error = false;
		if (listener != null)
			listener.startQuery();

		try {
			
			//API para el acceso al recurso guardias.xml en el harvester de datos abiertos http://192.168.10.81/8080
			//String url = "http://192.168.10.81:8080/dataset/9c8d1690-3b3f-43f3-b264-1c9d193b283c/resource/edd6ffeb-37eb-442c-9efa-34b89072c398/download/guardias.xml"

    		String url = "http://www.navarra.es/appsext/DescargarFichero/default.aspx?codigoAcceso=OpenData&fichero=GuardiasFarmacias/Guardias.xml";

			
			Log.d(TAG, url);
			InputStream is = openHttpConnection(url);

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
			.newInstance();

			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document doc = docBuilder.parse(is);

			Log.d(TAG, doc.toString());

			processResponseString(doc);

		} catch (Exception e) {
			Log.e("JSON", "Error parseando Fichero JSON", e);
			error = true;
		}
		if (listener != null) {
			if (error) {
				listener.endQueryError();
			} else {
				listener.endQueryOk();
			}
		}
	}

	private boolean processResponseString(Document doc) {
		try {
			if (doc == null)
				return false;

			doc.getDocumentElement().normalize();

			NodeList listaFarmacias = doc
					.getElementsByTagName("FARMACIAGUARDIA");

			int totalFarmacias = listaFarmacias.getLength();

			farmaciaGuardiaNavarraList.clear();
			selectedPointIndex = -1;

			GeoLocation geoLocalizador = new GeoLocation();

			Log.d(TAG, "procesando respuestas: "+ totalFarmacias);
			
			Date hoy = new Date();
			
			hoy.setHours(10);
			hoy.setMinutes(10);
			hoy.setSeconds(10);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(hoy);
			cal.add(Calendar.DATE, -1);
			Date ayer = cal.getTime();
			
			ayer.setHours(10);
			ayer.setMinutes(10);
			ayer.setSeconds(10);
			
		

			for (int i = 0; i < totalFarmacias; i++) {

				Node persona = listaFarmacias.item(i);

				if (persona.getNodeType() == Node.ELEMENT_NODE) {
					Element elemento = (Element) persona;

					
					
					//ACELERADOR
					
					FarmaciaGuardiaNavarra farmaciaGuardiaNavarra = new FarmaciaGuardiaNavarra();

					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					
					Date fechaFarmacia = new Date();
					
					fechaFarmacia = df.parse(getTagValue("FECHA", elemento));
					
			
					farmaciaGuardiaNavarra.setFecha(fechaFarmacia);
					

					Date fechafarmacia = new Date();
					fechafarmacia = farmaciaGuardiaNavarra.getFecha();
					fechafarmacia.setHours(10);
					fechafarmacia.setMinutes(10);
					fechafarmacia.setSeconds(10);	
					
					if (hoy.before(fechafarmacia)){
						i = totalFarmacias;
					}else if (ayer.after(fechafarmacia))
					{}
					else{
					farmaciaGuardiaNavarra.setDesde(Integer.valueOf(getTagValue("DESDE", elemento)));
					farmaciaGuardiaNavarra.setHasta(Integer.valueOf(getTagValue("HASTA", elemento)));
					farmaciaGuardiaNavarra.setLocalidad(getTagValue("LOCALIDAD", elemento));
					farmaciaGuardiaNavarra.setGrupo(getTagValue("GRUPO",elemento));
					farmaciaGuardiaNavarra.setDireccion(getTagValue("DIRECCION", elemento));
					farmaciaGuardiaNavarra.setFarmacia(getTagValue("FARMACIA",elemento));

					farmaciaGuardiaNavarra.setHorario("De: "+ farmaciaGuardiaNavarra.getDesde() + ", Hasta: "+ farmaciaGuardiaNavarra.getHasta());

					// Localizador
									
					String direccion = "Spain, Navarra, "+ farmaciaGuardiaNavarra.getLocalidad()+ " , "+ farmaciaGuardiaNavarra.getDireccion();
					
					direccion = direccion.replaceAll("/", " ");
					
					direccion = direccion.replaceAll(" ", "%20");
					
					
					Log.d(TAG, "farmacia actual: "+ i);
					Log.d(TAG, direccion);
					
					GeoPoint geoPoint = geoLocalizador.getGeoPoint(direccion);
					
					farmaciaGuardiaNavarra.setGeoPoint(geoPoint);
					farmaciaGuardiaNavarra.setLat((double) geoPoint.getLatitudeE6());
					farmaciaGuardiaNavarra.setLng((double) geoPoint.getLongitudeE6());

					try {
						farmaciaGuardiaNavarra.setTelefono(getTagValue(
								"TELEFONO", elemento));
					} catch (Exception e) {
					}
					;					
					Date fechaActual = new Date();
					
					Log.d(TAG, "fecha actual "+fechaActual.toString());
					
					Date fechaDesde = new Date();
					fechaDesde = farmaciaGuardiaNavarra.fecha;
					fechaDesde.setHours(farmaciaGuardiaNavarra.desde);
					fechaDesde.setMinutes(00);
					
					Log.d(TAG, "fecha desde "+fechaDesde.toString());
					
					Date fechaHasta = new Date();
					fechaHasta = farmaciaGuardiaNavarra.fecha;
					fechaHasta.setHours(farmaciaGuardiaNavarra.hasta);
					fechaHasta.setMinutes(00);
					
					Calendar cal2 = Calendar.getInstance();
					cal2.setTime(fechaHasta);
					cal2.add(Calendar.DATE, 1);
					fechaHasta = cal2.getTime();
					
					Log.d(TAG, "fecha hasta "+fechaHasta.toString());
					
					if ((fechaDesde.before(fechaActual) && fechaHasta.after(fechaActual))){
					farmaciaGuardiaNavarraList.add(farmaciaGuardiaNavarra);
					Log.d(TAG, "añadida:  "+farmaciaGuardiaNavarra.farmacia);
					}
				}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}

	public String convertStreamToString(InputStream is) throws IOException {
		Log.e(TAG, "convertStreamToString()");
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			Log.e(TAG, "Error obtaining data!!!");
			return "";
		}
	}

	private InputStream openHttpConnection(String urlString) throws IOException {
		InputStream in = null;
		int response = -1;

		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();

		if (!(conn instanceof HttpURLConnection))
			throw new IOException("Not an HTTP connection");

		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();

			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
			}
		} catch (Exception ex) {
			throw new IOException("Error connecting");
		}
		return in;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public FarmaciaGuardiaNavarra getFarmaciaGuardiaNavarra(int i) {
		if (i < 0 || i >= farmaciaGuardiaNavarraList.size())
			return null;
		return farmaciaGuardiaNavarraList.get(i);
	}

	public String getTagValue(String tag, Element elemento) {

		NodeList lista = elemento.getElementsByTagName(tag).item(0)
				.getChildNodes();

		Node valor = (Node) lista.item(0);

		return valor.getNodeValue();

	}

}
