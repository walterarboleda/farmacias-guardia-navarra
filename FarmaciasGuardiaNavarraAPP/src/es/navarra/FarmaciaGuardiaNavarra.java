package es.navarra;

import java.util.Date;

import com.google.android.maps.GeoPoint;

public class FarmaciaGuardiaNavarra {
	 
	   Double lat;
       Double lng;
       
       GeoPoint geoPoint;
       
       Double distance;
             
       Date fecha;
       int desde;
       int hasta;
       String localidad;
       String grupo;
       String direccion;
       String farmacia;
       String telefono;
       
       String horario;
       
       
       public String getHorario() {
		return horario;
	}
	public void setHorario(String horario) {
		this.horario = horario;
	}
	public Double getDistance() {
		return distance;
       }
       public void setDistance(Double distance) {
		this.distance = distance;
       }
       public Double getLat() {
               return lat;
       }
       public void setLat(Double lat) {
               this.lat = lat;
       }
       public Double getLng() {
               return lng;
       }
       public void setLng(Double lng) {
               this.lng = lng;
       }
       public GeoPoint getGeoPoint() {
               return geoPoint;
       }
       public void setGeoPoint(GeoPoint geoPoint) {
               this.geoPoint = geoPoint;
       }
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public int getDesde() {
		return desde;
	}
	public void setDesde(int desde) {
		this.desde = desde;
	}
	public int getHasta() {
		return hasta;
	}
	public void setHasta(int hasta) {
		this.hasta = hasta;
	}
	public String getLocalidad() {
		return localidad;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getFarmacia() {
		return farmacia;
	}
	public void setFarmacia(String farmacia) {
		this.farmacia = farmacia;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
       
}



