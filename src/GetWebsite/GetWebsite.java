package GetWebsite;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * TODO: Ta bort hårdkodning av sökord
 * TODO: Lägg till datum när annonsen kom upp (för fotosidan och blocket)
 * TODO: tradera.com: http://www.tradera.com/finding.mvc/itemlisting?search=zuiko&listtype=0&sort=128 (alla annonser, kortast tid kvar)
 * TODO: ebay.co.uk: http://www.ebay.co.uk/sch/?_kw=zuiko&_ds=1&_fcid=192&_localstpos=&_sc=1&_sop=15&_stpos=&gbr=1 (all items, price+p&p, lowest first)
 * TODO: Lägg till tid kvar av auktion (för Ebay/Tradera)
 * TODO: Lägg till info om det är "Köp nu" eller vanlig auktion (Ebay/Tradera)
 * TODO: Lägg till info om säljaren (Ebay/Tradera)
 * TODO: Gör det möjligt att läsa in från flera sidor (Ebay/Tradera)
 * TODO: API för Tradera/Ebay??
 * TODO: Grafiskt gränssnitt m klickbara annonser, gärna helt inhämtade och renderade i programmet med en "Läs mer"-knapp för webbläsaren.
 * TODO: Snyggt vore om man hämtade produktbilder, även från fotosidan
 * TODO: Utöka med fler säljsajter
 * 
 * @author wiktorkettel
 *
 */
public class GetWebsite {

	private List<Lens> allPages = new ArrayList<Lens>();
	
	public List<Lens> getAllPages() {
		return allPages;
	}

	public GetWebsite() throws Exception{
		String blocketExpr = "<a tabindex=\"50\" class=\"item_link\" href=\""
                + "([^\"]+)"      // (Detta är grupp 1, dvs url) Allt från citationstecknet till
                + "\"[^>]*>"      // sista citationstecknet
                + "([^<]+)"       // Allt från > till </a>, dvs produkttitel
                + "</a><p class=\"list_price\">"
                + "([^<]+[0-9]+)" 	// Ta sedan allt därefter som är siffror, dvs pris
                + "(kr|KR|Kr|:-&nbsp;|:-|sek|SEK|Sek|$|€|£)" // Hitta den monetära enheten
    			+ ".*?</p>";		// Till slutet på <p>-taggen med klass "list_price", scanna även allt därimellan.
		
		String fotosidanExpr = "<a class=\"viewdetail\" href=\""
                + "([^\"]+)"      // (Detta är grupp 1, dvs url) Allt från citationstecknet till
                + "\"[^>]*>"      // sista citationstecknet
                + "([^<]+)"       // Allt från > till </a>, dvs produkttitel
                + "</a>"
                + ".*?<td align=\"right\" valign=\"top\" nowrap[>]*"
                + "([0-9]*\\s*[0-9]*)" 	// Ta sedan allt från taggen ovan, dvs pris
                + "([\\s]kr|KR|Kr|:-&nbsp;|:-|sek|SEK|Sek|$|€|£)" // Hitta den monetära enheten
    			+ ".*?</td>";
		
		String traderaExpr = "<div class=\"ObjectHeadline\"><a href=\""
                + "([^\"]+)"      // (Detta är grupp 1, dvs url) Allt från citationstecknet till
                + "\"[^>]*>"      // sista citationstecknet
                + "([^<]+)"       // Allt från > till </a>, dvs produkttitel
                + "</a>"
                + ".*?<div class=\"price\">"
                + ".*?[^>]*>([0-9]*\\s*[0-9]*)" 	// Ta sedan allt från taggen ovan, dvs pris
                + "([\\s]kr|KR|Kr|:-&nbsp;|:-|sek|SEK|Sek|$|€|£)" // Hitta den monetära enheten
    			+ ".*?<div class=\"shipping\">";
		
		String blocketURL = "http://www.blocket.se/hela_sverige?ca=14&q=zuiko&st=s&st=u&st=b&l=0&f=p&w=3";
		String fotosidanURL = "http://www.fotosidan.se/classifieds/list.htm?_text=zuiko";
		String traderaURL = "http://www.tradera.com/finding.mvc/itemlisting?search=zuiko&listtype=0&sort=128";
		String ebayURL = "http://www.ebay.co.uk/sch/?_kw=zuiko&_ds=1&_fcid=192&_localstpos=&_sc=1&_sop=15&_stpos=&gbr=1";
		
		List<Lens> blocket = scanPage(blocketExpr, blocketURL);
		List<Lens> fotosidan = scanPage(fotosidanExpr,fotosidanURL);
		List<Lens> tradera = scanPage(traderaExpr,traderaURL);
		
		
		
		for(Lens lens : fotosidan){
			allPages.add(lens);
		}
		for(Lens lens:blocket){
			allPages.add(lens);
		}
		for(Lens lens:tradera){
			allPages.add(lens);
		}
		
//		Collections.sort(allPages);
		
	}
	
	
	/**
	 * En metod för att scanna en sida efter ett angivet regexp i syfte att hitta produktnamn, url och pris.
	 * OBS! Metoden klarar _inte_ av artiklar som inte har ett pris. Noll går bra, men utelämnat pris resulterar i
	 * icke-funnen produkt. 
	 * 
	 * Skapad efter artikel på http://www.javamex.com/tutorials/regular_expressions/example_scraping_html.shtml
	 * 
	 * @param Ett giltigt regexp som ska innehålla 4 grupper. Grupp 1 ska hitta url, grupp 2 ska hitta 
	 * 		  produktnamn/beskrivning, grupp 3 priset och grupp 4 den monetära enheten.
	 * @param En giltig URL som genererar önskat sök.
	 * @return En Lista med Lens-objekt skapade utifrån vad som hittas
	 * @throws Exception
	 */
	public static List<Lens> scanPage(String expr, String url) throws Exception{
		List<Lens> returnList = new ArrayList<Lens>();
    	Pattern patt = Pattern.compile(expr,Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher m = patt.matcher(getURLContent(new URL(url)));
		while (m.find()) {
			String urlFound = m.group(1);
			String productFound = m.group(2);
			String priceFound = m.group(3).replaceAll("\\s", "");
			int price = Integer.valueOf(priceFound);
			String monUnitFound = m.group(4);
			
			// Då fotosidan saknar http://fotosidan... bör det läggas till när lens-objektet skapas
			if(url.contains("fotosidan")){
				String tempUrl = "http://www.fotosidan.se";
				tempUrl = tempUrl.concat(urlFound);
				urlFound = tempUrl;
			}
			else if(url.contains("tradera")){
				String tempUrl = "http://www.tradera.com";
				tempUrl = tempUrl.concat(urlFound);
				urlFound = tempUrl;
			}
			returnList.add(new Lens(productFound, urlFound, price, monUnitFound));
		}		
		return returnList;
	}
	
	public static CharSequence getURLContent(URL url) throws IOException {
		URLConnection conn = url.openConnection();
		String encoding = conn.getContentEncoding();
		if (encoding == null) {
			encoding = "ISO-8859-1";
		}
		BufferedReader br = new BufferedReader(new
				InputStreamReader(conn.getInputStream(), encoding));
		StringBuilder sb = new StringBuilder(16384);
		try {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
		} finally {
			br.close();
		}
		return sb;
	}
}