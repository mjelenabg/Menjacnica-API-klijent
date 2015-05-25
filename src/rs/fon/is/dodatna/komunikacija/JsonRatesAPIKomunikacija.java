package rs.fon.is.dodatna.komunikacija;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;




import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import rs.fon.is.dodatna.valute.Valuta;

public class JsonRatesAPIKomunikacija {
	private static String sendGet(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		con.setRequestMethod("GET");
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		
		boolean endReading = false;
		String response = "";
		
		while (!endReading) {
			String s = in.readLine();
			
			if (s != null) {
				response += s;
			} else {
				endReading = true;
			}
		}
		in.close();
 
		return response.toString();
	}
	private static String vratiURLKursa(String nazivKursa) {
		return "http://jsonrates.com/get/?from=" + nazivKursa + "&to=RSD&apiKey=jr-ba8999934fc5a7ab64a4872fb4ed9af7";
	}
public static LinkedList<Valuta> vratiIznosKurseva(String [] naziviValuta) {
		
		LinkedList<Valuta> kursnaLista = new LinkedList<Valuta>();
		for (int i = 0; i < naziviValuta.length; i++) {
				try {
					String result = sendGet(vratiURLKursa(naziviValuta[i]));
					Gson gson = new GsonBuilder().create();
					JsonObject jsonResult = (JsonObject) gson.fromJson(result, JsonObject.class);
					Valuta valuta = new Valuta();
					valuta.setNaziv(naziviValuta[i]);
					valuta.setKurs(Double.parseDouble(jsonResult.get("rate").getAsString()));
					kursnaLista.add(valuta);
					
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				}
		
		
		return kursnaLista;
		
	}
	
}
