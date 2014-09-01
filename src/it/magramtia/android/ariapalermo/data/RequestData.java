/**
 * 
 */
package it.magramtia.android.ariapalermo.data;

/**
 * @author mattiadigan
 *
 */
public class RequestData {
	
	/**
	 * Limiti per la salute delle sostanze riportate. Le misure sono da intendersi in 
	 * microgrammi / metricubi
	 */
	public static final double BIOSSIDO_AZOTO_LIMIT = 40.0;
	public static final double BIOSSIDO_ZOLFO_LIMIT = 20.0;
	public static final double BENZENE_LIMIT = 5.0;
	public static final double MATERIALE_PARTICOLATO_LIMIT = 40.0;
	// Valori letti per ogni sostanza
	private double bioAzoto;
	private double bioZolfo;
	private double benzene;
	private double materialeParticolato;
	
	//Percentuale di verde nella zona
	private double verde;
	//Risultato
	private double finale;
	
	//Costruttore privato. L'oggetto si istanzia solo attraverso la lettura di un xml
	public RequestData(String s, double bA, double bZ, double ben, double mP, double v, double f){
		bioAzoto = bA;
		bioZolfo = bZ;
		benzene = ben;
		materialeParticolato = mP;
		verde = v;
		finale = f;
	}
	
	/**
	 * Metodo getter del biossido d'azoto
	 * @return Il valore del biossido di azoto
	 */
	public double getBiossidoAzoto(){
		return bioAzoto;
	}
	
	/**
	 * Metodo getter del biossido di zolfo
	 * @return Il valore del biossido di zolfo
	 */
	public double getBiossidoZolfo(){
		return bioZolfo;
	}
	
	/**
	 * Metodo getter del benzene
	 * @return Il valore del benzene
	 */
	public double getBenzene(){
		return benzene;
	}
	
	/**
	 * Metodo getter del materiale particolato
	 * @return Il valore del materiale particolato
	 */
	public double getMaterialeParticolato(){
		return materialeParticolato;
	}
	
	/**
	 * Metodo getter del verde
	 * @return La percentuale di verde nella zona
	 */
	public double getVerde(){
		return verde;
	}
	
	public double getFinale(){
		return finale;
	}
}
