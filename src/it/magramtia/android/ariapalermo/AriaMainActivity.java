package it.magramtia.android.ariapalermo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import it.magramtia.android.ariapalermo.SearchAddressDialogFragment.SearchAddressDialogListener;
import it.magramtia.android.ariapalermo.data.Posizione;
import it.magramtia.android.ariapalermo.status.ResponseStatus;
import it.magramtia.android.ariapalermo.xml.PosizioneParser;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AriaMainActivity extends ActionBarActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
SearchAddressDialogListener{

	private ImageView areeVerdi;
	private ImageView miaAria;
	private ImageView faiPasseggiata;
	private ImageView cercaIndirizzo;
	private Location mCurrentLocation;
	private LocationClient mLocationClient;
	private Posizione dest;

	private final double longmax=13.461686;
	private final double latmax=38.231152;
	private final double longmin=13.229690;
	private final double latmin=38.049681;

	// Whether there is a Wi-Fi connection.
	private static boolean wifiConnected = false; 
	// Whether there is a mobile connection.
	private static boolean mobileConnected = false;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aria_main);

		mLocationClient = new LocationClient(this, this, this);

		PulsantiCallback myCallback = new PulsantiCallback();

		areeVerdi = (ImageView) findViewById(R.id.imageView1);
		miaAria = (ImageView) findViewById(R.id.imageView2);
		faiPasseggiata = (ImageView) findViewById(R.id.imageView3);
		cercaIndirizzo = (ImageView) findViewById(R.id.imageView4);

		areeVerdi.setOnClickListener(myCallback);
		miaAria.setOnClickListener(myCallback);
		cercaIndirizzo.setOnClickListener(myCallback);
		faiPasseggiata.setOnClickListener(myCallback);
		areeVerdi.setOnClickListener(myCallback);
	}

	//Controlla lo stato della connessione
	private void checkConnection() {
		NetworkInfo mobileInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		mobileConnected = mobileInfo.isConnected();
		wifiConnected = wifiInfo.isConnected();
	}

	/**
	 * Classe di callback per i pulsanti
	 */
	private class PulsantiCallback implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			cancellaSelezione();
			checkConnection();
			v.setBackgroundColor(Color.WHITE);
			if(!mobileConnected && !wifiConnected){
				Toast.makeText(getApplication(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
			}
			else{
				switch(v.getId()){
				case R.id.imageView1:
					//Chiamata all'activity aree verdi
					startActivity(new Intent(getApplication(), AreeVerdiActivity.class));
					break;
				case R.id.imageView2:
					mCurrentLocation = mLocationClient.getLastLocation();
					if(mCurrentLocation == null){
						Toast.makeText(getApplication(), getResources().getString(R.string.no_location_err), Toast.LENGTH_LONG).show();
					}
					else{
						if(mCurrentLocation != null){
							double lat = mCurrentLocation.getLatitude();
							double lng = mCurrentLocation.getLongitude();
							if(lat >= latmin && lat <= latmax && lng >= longmin && lng <= longmax){
								Intent intent = new Intent(getApplication(), QualityResultActivity.class);
								intent.putExtra("tipo", getResources().getString(R.string.gps));
								intent.putExtra("Lat", lat);
								intent.putExtra("Lon", lng);
								startActivity(intent);
							}
							else{
								Toast.makeText(getApplication(), "Non sei dentro la città di Palermo!", Toast.LENGTH_SHORT).show();
							}
						}
						else{
							Toast.makeText(getApplication(), "Attiva il servizio di localizzazione dalle impostazioni!", Toast.LENGTH_SHORT).show();
							cancellaSelezione();
						}
					}
					break;
				case R.id.imageView3:
					//Chiamata all'activity fai passeggiata
					mCurrentLocation = mLocationClient.getLastLocation();
					if(mCurrentLocation == null){
						Toast.makeText(getApplication(), getResources().getString(R.string.no_location_err), Toast.LENGTH_LONG).show();
					}
					else{
						double lat = mCurrentLocation.getLatitude();
						double lng = mCurrentLocation.getLongitude();
						String urlPasseggiata = "http://ariapalermo.altervista.org/passeggiata.php?lat="+lat+"&lng="+lng;
						//String urlPasseggiata = "http://ariapalermo.altervista.org/passeggiata.php?lat=120.4907111&lng=15.0772719";
						if(lat >= latmin && lat <= latmax && lng >= longmin && lng <= longmax){
							new PasseggiataPointGetter().execute(urlPasseggiata);
						}
						else{
							Toast.makeText(getBaseContext(),  "Non ti trovi dentro il comune di Palermo!", Toast.LENGTH_SHORT).show();
						}
					}
					break;
				case R.id.imageView4:
					searchAddress();
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	protected void onResume(){
		super.onResume();
		cancellaSelezione();
	}

	//Elimina i quadrati bianchi
	private void cancellaSelezione(){
		areeVerdi.setBackgroundColor(getResources().getColor(R.color.verde_app));
		miaAria.setBackgroundColor(getResources().getColor(R.color.verde_app));
		faiPasseggiata.setBackgroundColor(getResources().getColor(R.color.verde_app));
		cercaIndirizzo.setBackgroundColor(getResources().getColor(R.color.verde_app));
	}

	/*
	 * Creazione e gestione del menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.aria_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_help:
			startActivity(new Intent(this, HelpActivity.class));
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	/*
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
	}
	/*
	 * Called by Location Services if the connection to the
	 * location client drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}
	/*
	 * Called by Location Services if the attempt to
	 * Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the
			 * user with the error.
			 */
			Toast.makeText(this, "Errore di connessione!", Toast.LENGTH_SHORT).show();
			//showErrorDialog(connectionResult.getErrorCode());
		}
	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}
	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}

	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;
		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}
		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}
		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	/*
	 * Handle results returned to the FragmentActivity
	 * by Google Play services
	 */
	@Override
	protected void onActivityResult(
			int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST :
			/*
			 * If the result code is Activity.RESULT_OK, try
			 * to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK :
				/*
				 * Try the request again
				 */
				break;
			}
		}
	}

	/*
	 * Gestione della finestra di dialogo
	 */
	//Mostra finestra di dialogo
	public void searchAddress(){
		DialogFragment newFragment = new SearchAddressDialogFragment();
		newFragment.show(getSupportFragmentManager(), "search");
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		EditText edit = (EditText) dialog.getDialog().findViewById(R.id.editText1);
		String indirizzo = edit.getText().toString();
		Intent intent = new Intent(this, QualityResultActivity.class);
		intent.putExtra("indirizzo", indirizzo);
		intent.putExtra("tipo", getResources().getString(R.string.indirizzo));
		startActivity(intent);
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		return;
	}

	/*
	 * Handle di passeggiata
	 */
	private class PasseggiataPointGetter extends
	AsyncTask<String, Void, Posizione> {

		@Override
		protected Posizione doInBackground(String... urls) {
			Posizione points;
			try {
				points = loadXmlFromNetwork(urls[0]).get(0);
			} catch (IOException e) {
				return null;
			} catch (XmlPullParserException e) {
				return null;
			} catch (NullPointerException e) {
				return null;
			}
			return points;
		}

		protected void onPostExecute(Posizione pos){
			if(pos != null){
				dest = pos;
				String urlGoogle = "http://maps.google.com/maps?saddr=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();
				urlGoogle = urlGoogle + "&daddr=" + 
						dest.getLatitude() + "," + 
						dest.getLongitude();
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlGoogle));
				startActivity(intent);
			}
			else{
				String status = ResponseStatus.getInstance(null).getStatus();
				if(status.equals(getResources().getString(R.string.status_out_of_border))){
					Toast.makeText(getApplicationContext(), "Non ti trovi dentro Palermo!", Toast.LENGTH_SHORT).show();
				}
				else if(status.equals(getResources().getString(R.string.status_error))){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
				}
			}
		}

		// Uploads XML from stackoverflow.com, parses it, and combines it with
		// HTML markup. Returns HTML string.
		private List<Posizione> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
			InputStream stream = null;
			// Instantiate the parser
			PosizioneParser posizioneParser = new PosizioneParser();
			List<Posizione> entry;
			String status;
			try {
				stream = downloadUrl(urlString);        
				entry = posizioneParser.parse(stream);
				// Makes sure that the InputStream is closed after the app is
				// finished using it.
			} finally {
				if (stream != null) {
					stream.close();
				} 
			}
			status  = ResponseStatus.getInstance(null).getStatus();
			if(status != null && status.equals(getResources().getString(R.string.status_ok))){
				return entry;
			}
			else{
				return null;
			}
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
	}
}
