/**
 * 
 */
package it.magramtia.android.ariapalermo.xml;

import it.magramtia.android.ariapalermo.data.Posizione;
import it.magramtia.android.ariapalermo.status.ResponseStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * @author mattiadigan
 *
 * Effettua il parsing del file xml generato dal server per l'activity Passeggiata
 */
public class PosizioneParser {

	//Non uso namespaces
	private static final String ns = null;

	/**
	 * Riceve in input un file xml sotto forma di InputStream e restituisce una lista di punti
	 * con le loro coordinate
	 * @param in
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<Posizione> parse(InputStream in) throws XmlPullParserException, IOException {
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

	/*
	 * Struttura del file xml
	 */
	private List<Posizione> readResponse(XmlPullParser parser) throws XmlPullParserException, IOException{
		List<Posizione> points = new ArrayList<Posizione>();
		String name;
		ResponseStatus responseStatus = ResponseStatus.getInstance(null);
		parser.require(XmlPullParser.START_TAG, ns, "response");
		responseStatus.setStatusString(readStatus(parser));
		while(parser.getEventType() != XmlPullParser.END_TAG){
			name = parser.getName();		
			if(name.equals("punto")){
				parser.require(XmlPullParser.START_TAG, ns, "punto");
				parser.nextTag();
				Posizione point = new Posizione();
				point.setId(readId(parser));
				point.setLatitude(readLat(parser));
				point.setLongitude(readLon(parser));
				points.add(point);
				parser.require(XmlPullParser.END_TAG, ns, "punto");
				parser.nextTag();
			}
		}
		return points;
	}

	private String readStatus(XmlPullParser parser) throws IOException, XmlPullParserException{
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, ns, "status");
		String status = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "status");
		parser.nextTag();
		return status;
	}

	private int readId(XmlPullParser parser) throws IOException, XmlPullParserException{
		parser.require(XmlPullParser.START_TAG, ns, "id");
		int id = readInt(parser);
		parser.require(XmlPullParser.END_TAG, ns, "id");
		parser.nextTag();
		return id;
	}
	

	private double readLat(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "lat");
		double lat = readDouble(parser);
		parser.require(XmlPullParser.END_TAG, ns, "lat");
		parser.nextTag();
		return lat;
	}
	
	private double readLon(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "lng");
		double lon = readDouble(parser);
		parser.require(XmlPullParser.END_TAG, ns, "lng");
		parser.nextTag();
		return lon;
	}
	
	private int readInt(XmlPullParser parser) throws IOException, XmlPullParserException {
		int result = 0;
		if (parser.next() == XmlPullParser.TEXT) {
			result = Integer.parseInt(parser.getText());
			parser.nextTag();
		}
		return result;
	}
	
	private double readDouble(XmlPullParser parser) throws IOException, XmlPullParserException {
		double result = 0;
		if (parser.next() == XmlPullParser.TEXT) {
			result = Double.parseDouble(parser.getText());
			parser.nextTag();
		}
		return result;
	}
	
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String text = null;
		if (parser.next() == XmlPullParser.TEXT) {
			text = parser.getText();
			parser.nextTag();
		}
		return text;
	}
}
