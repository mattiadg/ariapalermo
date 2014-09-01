package it.magramtia.android.ariapalermo;

import it.magramtia.android.ariapalermo.RefreshDialogFragment.RefreshDialogFragmentListener;
import it.magramtia.android.ariapalermo.status.ResponseStatus;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;

public class SplashScreenActivity extends ActionBarActivity implements RefreshDialogFragmentListener{

	private static final long DELAY = 3000;
	private boolean scheduled = false;
	private Timer splashTimer;

	// Whether there is a Wi-Fi connection.
	private static boolean wifiConnected = false; 
	// Whether there is a mobile connection.
	private static boolean mobileConnected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen_layout);

		checkConnection();
		splashTimer = new Timer();
		if(wifiConnected || mobileConnected){
			splashTimer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					SplashScreenActivity.this.finish();
					startActivity(new Intent(SplashScreenActivity.this, AriaMainActivity.class));
				}
			}, DELAY);
			scheduled = true;
		}
		else{
			splashTimer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					launchDialog();
				}
			}, DELAY);
			scheduled = true;
		}
		//Inizializza il ResponseStatus
		ResponseStatus.getInstance(getApplicationContext());
	}

	@Override
	protected void onResume(){
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (scheduled)
			splashTimer.cancel();
		splashTimer.purge();
	}

	//Controlla lo stato della connessione
	private void checkConnection() {
		NetworkInfo mobileInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		mobileConnected = mobileInfo.isConnected();
		wifiConnected = wifiInfo.isConnected();
	}
	
	private void launchDialog(){
		DialogFragment fragment = new RefreshDialogFragment();
		fragment.show(getSupportFragmentManager(), "Errore di rete");
	}

	@Override
	public void onPositiveClick() {
		this.finish();
		startActivity(getIntent());
	}
}
