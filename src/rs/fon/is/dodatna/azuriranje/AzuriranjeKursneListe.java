package rs.fon.is.dodatna.azuriranje;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import rs.fon.is.dodatna.komunikacija.JsonRatesAPIKomunikacija;
import rs.fon.is.dodatna.valute.Valuta;

public class AzuriranjeKursneListe {
	private static final String putanjaDoFajlaKursnaLista = "data/kursnaLista.json";
	
	public LinkedList<Valuta> ucitajValute(){
		LinkedList<Valuta> kursnaLista = new LinkedList<Valuta>();
		try {
			FileReader in = new FileReader(putanjaDoFajlaKursnaLista);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonObject kurs = gson.fromJson(in, JsonObject.class);
			JsonArray kursevi = kurs.get("valute").getAsJsonArray();
			for (int i = 0; i <kursevi.size() ; i++) {
				Valuta v = new Valuta();
				v.setKurs(kursevi.get(i).getAsJsonObject().get("kurs").getAsDouble());
				v.setNaziv(kursevi.get(i).getAsJsonObject().get("naziv").getAsString());
				kursnaLista.add(v);	
			}
			in.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return kursnaLista;
	}
	
	public void upisiValute(LinkedList<Valuta> kursnaLista, GregorianCalendar datum){
		try {
			JsonArray nizKursevaJson = new JsonArray();
			JsonObject objekatJson = new JsonObject();
			objekatJson.addProperty("datum", datum.get(datum.DAY_OF_MONTH)+"."+(datum.get(datum.MONTH)+1)+"."+ datum.get(datum.YEAR));
				for (int i = 0; i < kursnaLista.size(); i++) {
					JsonObject valutaJson = new JsonObject();
					valutaJson.addProperty("naziv", kursnaLista.get(i).getNaziv());
					valutaJson.addProperty("kurs", kursnaLista.get(i).getKurs());
					nizKursevaJson.add(valutaJson);
				}
				objekatJson.add("valute", nizKursevaJson);
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(putanjaDoFajlaKursnaLista)));
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				out.println(gson.toJson(objekatJson));
				out.close();
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void azurirajValute() {
		try {
			
			LinkedList<Valuta> kursnaLista = ucitajValute();
			String[] naziviKurseva = new String[kursnaLista.size()];
			
			for (int i = 0; i < kursnaLista.size(); i++) {
				naziviKurseva[i] = kursnaLista.get(i).getNaziv();
			}
			kursnaLista = JsonRatesAPIKomunikacija.vratiIznosKurseva(naziviKurseva);
						
			upisiValute(kursnaLista, new GregorianCalendar());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
