/**
 * 
 */
package it.magramtia.android.ariapalermo.xml;

import it.magramtia.android.ariapalermo.data.RequestData;
import it.magramtia.android.ariapalermo.status.ResponseStatus;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.util.Xml;

/**
 * @author mattiadigan
 *
 * Effettua il parsing del file xml ricevuto dal server
 */
public class AriaPalermoParser {

	// We don't use namespaces
	private static final String ns = null;

	public RequestData parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readResponse(parser);
		} finally {
			in.close();
		}
	}

	private RequestData readResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
		ResponseStatus responseStatus = ResponseStatus.getInstance(null);
		String status;
		double azoto;
		double zolfo;
		double benzene;
		double particolato;
		double verde;
		double finale;

		String name;

		parser.require(XmlPullParser.START_TAG, ns, "response");
		name = parser.getName();
		if(name.equals("response")){
			parser.nextTag();
			status = readStatus(parser);
			if(status.equals("ok")){
				azoto = readAzoto(parser);
				zolfo = readZolfo(parser);
				benzene = readBenzene(parser);
				particolato = readParticolato(parser);
				verde = readVerde(parser);
				finale = readFinale(parser);
			}
			else{
				azoto = 0;
				zolfo = 0;
				benzene = 0;
				particolato = 0;
				verde = 0;
				finale = 0;
			}
			responseStatus.setStatusString(status);
			return new RequestData(status, azoto, zolfo, benzene, particolato, verde, finale);
		}
		return null;
	}

	//Legge il tag status nella risposta
	private String readStatus(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "status");
		String status = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "status");
		parser.nextTag();
		return status;
	}

	//Legge il tag azoto
	private double readAzoto(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "azoto");
		double azoto = readDouble(parser);
		parser.require(XmlPullParser.END_TAG, ns, "azoto");
		parser.nextTag();
		return azoto;
	}

	//Legge il tag zolfo
	private double readZolfo(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "zolfo");
		double zolfo = readDouble(parser);
		parser.require(XmlPullParser.END_TAG, ns, "zolfo");
		parser.nextTag();
		return zolfo;
	}

	//Legge il tag benzene
	private double readBenzene(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "benzene");
		double benzene = readDouble(parser);
		parser.require(XmlPullParser.END_TAG, ns, "benzene");
		parser.nextTag();
		return benzene;
	}

	//Legge il tag materiale
	private double readParticolato(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "materiale");
		double particolato = readDouble(parser);
		parser.require(XmlPullParser.END_TAG, ns, "materiale");
		parser.nextTag();
		return particolato;
	}

	//Legge il tag verde
	private double readVerde(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "verde");
		double verde = readDouble(parser);
		parser.require(XmlPullParser.END_TAG, ns, "verde");
		parser.nextTag();
		return verde;
	}

	//Legge il tag finale
	private double readFinale(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "finale");
		double finale = readDouble(parser);
		parser.require(XmlPullParser.END_TAG, ns, "finale");
		parser.nextTag();
		return finale;
	}

	//Estrae i valori String
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	//Estrae i valori double
	private double readDouble(XmlPullParser parser) throws IOException, XmlPullParserException {
		double result = 0;
		if (parser.next() == XmlPullParser.TEXT) {
			result = Double.parseDouble(parser.getText());
			parser.nextTag();
		}
		return result;
	}

}
