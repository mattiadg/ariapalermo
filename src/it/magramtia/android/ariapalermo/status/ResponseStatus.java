package it.magramtia.android.ariapalermo.status;

import android.content.Context;
import it.magramtia.android.ariapalermo.R;

/**
 * Classe singleton che memorizza lo status dell'ultima risposta xml
 */

/**
 * @author mattiadigan
 *
 */
public class ResponseStatus {
	
	private String status;
	private Context context;
	private static ResponseStatus instance;

	//Costruttore privato. La classe è un singleton
	private ResponseStatus(Context context){
		this.context = context;
		status = null;
	}

	//Metodo setter per lo status. Vi si accede tramite un'interfaccia pubblica
	private void setStatus(String s){
		status = s;
	}
	/**
	 * Metodo setter per lo status, accetta solo i valori possibili delle risposte xml.
	 * Ritorna true se la scrittura è stata effettuata, false altrimenti.
	 * @param s
	 * @return
	 */
	public synchronized boolean setStatusString(String s){
		if(s.equals(context.getResources().getString(R.string.status_ok)) ||
				s.equals(context.getResources().getString(R.string.status_out_of_border)) ||
				s.equals(context.getResources().getString(R.string.status_error))){
			setStatus(s);
			return true;
		}
		else{
			return false;
		}
	}

	//Metodo getter per lo status
	public String getStatus(){
		return status;
	}

	public static ResponseStatus getInstance(Context context){
		if(instance == null){
			instance = new ResponseStatus(context);
		}
		if(context != null){
			instance.context = context; 
		}
		return instance;
	}
}
