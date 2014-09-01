/**
 * 
 */
package it.magramtia.android.ariapalermo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import it.magramtia.android.ariapalermo.data.RequestData;
import it.magramtia.android.ariapalermo.status.ResponseStatus;
import it.magramtia.android.ariapalermo.xml.AriaPalermoParser;


/**
 * @author mattiadigan
 *
 */
public class QualityResultActivity extends android.support.v7.app.ActionBarActivity {

	private GoogleMap mMap;
	private String indirizzo;
	private String tipo;
	private double latitude;
	private double longitude;

	public static final String WIFI = "Wi-Fi";
	public static final String ANY = "Any";
	private String Url = "";
	// Whether there is a Wi-Fi connection.
	private static boolean wifiConnected = false; 
	// Whether there is a mobile connection.
	private static boolean mobileConnected = false;
	// Whether the display should be refreshed.
	public static boolean refreshDisplay = false; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quality_result_activity);

		//Legge i dati passati dall'activity chiamante
		Bundle extras = getIntent().getExtras();
		tipo = extras.getString("tipo");
		if(tipo.equals(getResources().getString(R.string.gps))){
			latitude = extras.getDouble("Lat");
			longitude = extras.getDouble("Lon");
		}
		else if(tipo.equals(getResources().getString(R.string.indirizzo))){
			indirizzo = extras.getString(getResources().getString(R.string.indirizzo));
		}
		checkConnection();
		setUpMapIfNeeded();
		loadPage();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	private void checkConnection() {
		NetworkInfo mobileInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		mobileConnected = mobileInfo.isConnected();
		wifiConnected = wifiInfo.isConnected();
	}

	// Uses AsyncTask to download the XML feed from stackoverflow.com.
	public void loadPage() {  

		if(wifiConnected || mobileConnected) {
			new DownloadXmlTask().execute(Url);
		}
		else {
			Toast errToast = Toast.makeText(this, "Connettività non presente", Toast.LENGTH_LONG);
			errToast.show();
		}  
	}

	// Implementation of AsyncTask used to download XML feed from stackoverflow.com.
	private class DownloadXmlTask extends AsyncTask<String, Void, RequestData> {

		@Override
		protected RequestData doInBackground(String... urls) {
			RequestData values;

			try {
				values = loadXmlFromNetwork(urls[0]);
			} catch (IOException e) {
				return new RequestData(getResources().getString(R.string.connection_error), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
			} catch (XmlPullParserException e) {
				return new RequestData(getResources().getString(R.string.xml_error), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
			}
			return values;
		}

		@Override
		protected void onPostExecute(RequestData result) {  
			String status = ResponseStatus.getInstance(null).getStatus();
			//Carica le TextView dell'activity
			TextView azotoText = (TextView) findViewById(R.id.no2m);
			TextView zolfoText = (TextView) findViewById(R.id.so2mis);
			TextView benzeneText = (TextView) findViewById(R.id.benzenemis);
			TextView particolatoText = (TextView) findViewById(R.id.particolatomis);
			TextView verdeText = (TextView) findViewById(R.id.verdemis);
			TextView finalText = (TextView) findViewById(R.id.finalemis);

			TextView azotoLimText = (TextView) findViewById(R.id.no2lim);
			TextView zolfoLimText = (TextView) findViewById(R.id.so2lim);
			TextView benzeneLimText = (TextView) findViewById(R.id.benzenelim);
			TextView particolatoLimText = (TextView) findViewById(R.id.particolatolim);
			//Assegna i valori limite delle sostanze alle TextView
			azotoLimText.setText("/" + String.valueOf(RequestData.BIOSSIDO_AZOTO_LIMIT));
			zolfoLimText.setText("/" + String.valueOf(RequestData.BIOSSIDO_ZOLFO_LIMIT));
			benzeneLimText.setText("/" + String.valueOf(RequestData.BENZENE_LIMIT));
			particolatoLimText.setText("/" + String.valueOf(RequestData.MATERIALE_PARTICOLATO_LIMIT));
			
			if(status.equals(getResources().getString(R.string.status_ok))){
				//Inserisce i dati nelle TextView
				azotoText.setText(String.valueOf(result.getBiossidoAzoto()));
				zolfoText.setText(String.valueOf(result.getBiossidoZolfo()));
				benzeneText.setText(String.valueOf(result.getBenzene()));
				particolatoText.setText(String.valueOf(result.getMaterialeParticolato()));
				verdeText.setText(String.valueOf(result.getVerde()) + "%" );
				finalText.setText(String.valueOf(result.getFinale()) + "%");
				coloraTextView(azotoText, result.getBiossidoAzoto(), RequestData.BIOSSIDO_AZOTO_LIMIT);
				coloraTextView(zolfoText, result.getBiossidoZolfo(), RequestData.BIOSSIDO_ZOLFO_LIMIT);
				coloraTextView(benzeneText, result.getBenzene(), RequestData.BENZENE_LIMIT);
				coloraTextView(particolatoText, result.getMaterialeParticolato(), RequestData.MATERIALE_PARTICOLATO_LIMIT);
				coloraVerdeTextView(verdeText, result.getVerde());
				coloraFinalTextView(finalText, result.getFinale());
			}
			else if(status.equals(getResources().getString(R.string.status_out_of_border))){
				Toast.makeText(QualityResultActivity.this, "Non ti trovi dentro la città di Palermo!", Toast.LENGTH_LONG).show();
				startActivity(new Intent(getApplication(), AriaMainActivity.class));
			}
			else if(status.equals(getResources().getString(R.string.status_error))){
				Toast.makeText(QualityResultActivity.this, getResources().getString(R.string.connection_error), Toast.LENGTH_LONG).show();
				startActivity(new Intent(getApplication(), AriaMainActivity.class));
			}
		}

		// Uploads XML from stackoverflow.com, parses it, and combines it with
		// HTML markup. Returns HTML string.
		private RequestData loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
			InputStream stream = null;
			// Instantiate the parser
			AriaPalermoParser ariaPalermoParser = new AriaPalermoParser();
			RequestData entry;

			try {
				stream = downloadUrl(urlString);        
				entry = ariaPalermoParser.parse(stream);
				// Makes sure that the InputStream is closed after the app is
				// finished using it.
			} finally {
				if (stream != null) {
					stream.close();
				} 
			}
			return entry;
		}

		// Given a string representation of a URL, sets up a connection and gets
		// an input stream.
		private InputStream downloadUrl(String urlString) throws IOException {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			return conn.getInputStream();
		}

		private void coloraTextView(TextView tv, double val, double lim){
			if(val > lim){
				tv.setTextColor(Color.RED);
			}
			else if(val < 0.75 * lim){
				tv.setTextColor(Color.GREEN);
			}
		}

		private void coloraVerdeTextView(TextView tv, double val){
			if(val < 5){
				tv.setTextColor(Color.RED);
			}
			else if(val > 25){
				tv.setTextColor(Color.GREEN);
			}
		}

		private void coloraFinalTextView(TextView tv, double val){
			if(val < 50){
				tv.setTextColor(getResources().getColor(R.color.verde));
			}
			else if(val < 80){
				tv.setTextColor(getResources().getColor(R.color.arancione));
			}
			else {
				tv.setTextColor(getResources().getColor(R.color.rosso));
			}
		}
	}


	/*
	 * Mappa Google
	 */

	//Carica la mappa
	private void setUpMapIfNeeded() {
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				aggiungiIndirizzo();
			}
		}
	}

	//Aggiunge indirizzo alla mappa
	private void aggiungiIndirizzo(){
		LatLng POSIZIONE = null;
		Geocoder dec = new Geocoder(getApplicationContext()); //da un indirizzo alle coordinate sulla mappa
		Address indirizzo_addr = null;
		List<Address> list;

		if(tipo.equals(getResources().getString(R.string.indirizzo))){
			String str_ind = indirizzo.trim().toLowerCase() + " Palermo";
			try{
				list=(dec.getFromLocationName(str_ind,1));//primo elemento della lista e lo assegna ad indirizzo
				if(list.size() > 0){
					indirizzo_addr = list.get(0);
					POSIZIONE = new LatLng(indirizzo_addr.getLatitude(), indirizzo_addr.getLongitude());
					Url = "http://ariapalermo.altervista.org/myair.php?lat="+indirizzo_addr.getLatitude()+"&lng="+indirizzo_addr.getLongitude();
					mMap.addMarker(new MarkerOptions().position(POSIZIONE)
							.title("Indirizzo cercato")
							.snippet(indirizzo)
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(POSIZIONE, 15));//sposta la camera sul nuovo punto	
				}
				else{
					Toast.makeText(getApplicationContext(), "Errore! Impossibile trovare l'indirizzo specificato!", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(this, AriaMainActivity.class));
				}
			}
			catch(IOException e){
				//Toast toast = new Toast(getApplicationContext());
				Toast.makeText(getApplicationContext(), "Impossibile caricare la mappa.\nVerifica la tua connessione ad internet", Toast.LENGTH_SHORT).show();
			} 
		}
		else if(tipo.equals(getResources().getString(R.string.gps))){
			POSIZIONE = new LatLng(latitude, longitude);
			try{
				indirizzo_addr = dec.getFromLocation(latitude, longitude, 1).get(0);
				indirizzo = indirizzo_addr.getThoroughfare();
				Url = "http://ariapalermo.altervista.org/myair.php?lat="+indirizzo_addr.getLatitude()+"&lng="+indirizzo_addr.getLongitude();
				mMap.addMarker(new MarkerOptions().position(POSIZIONE)
						.title("La tua ultima posizione")
						.snippet(indirizzo)
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(POSIZIONE, 15));//sposta la camera sul nuovo punto
			} catch(IOException e){
				Toast.makeText(getApplicationContext(), "Impossibile caricare la mappa.\nVerifica la tua connessione ad internet", Toast.LENGTH_SHORT).show();
			}
		}
		else{
			return;
		}
	}
}
