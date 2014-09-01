/**
 * 
 */
package it.magramtia.android.ariapalermo.data;

/**
 * @author mattiadigan
 *
 */
public class Posizione {

	private int id;
	private double lat;
	private double lon;
	
	public Posizione(){
		this(0, 0.0, 0.0);
	}
	
	public Posizione(int _id, double latitude, double longitude){
		id = _id;
		lat = latitude;
		lon = longitude;
	}
	
	/*
	 * Metodi getter
	 */
	public int getId(){
		return id;
	}
	
	public double getLatitude(){
		return lat;
	}
	
	public double getLongitude(){
		return lon;
	}
	
	/*
	 * Metodi setter
	 */
	public void setId(int id){
		this.id = id;
	}
	
	public void setLatitude(double lat){
		this.lat = lat;
	}
	
	public void setLongitude(double lon){
		this.lon = lon;
	}
	
	public String toString(){
		return "id=" + id + ";lat=" + lat + ";lng=" + lon;
	}
}
