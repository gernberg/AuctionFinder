package GetWebsite;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * TODO: Ta bort h�rdkodning av s�kord
 * TODO: L�gg till datum n�r annonsen kom upp (f�r fotosidan och blocket)
 * TODO: tradera.com: http://www.tradera.com/finding.mvc/itemlisting?search=zuiko&listtype=0&sort=128 (alla annonser, kortast tid kvar)
 * TODO: ebay.co.uk: http://www.ebay.co.uk/sch/?_kw=zuiko&_ds=1&_fcid=192&_localstpos=&_sc=1&_sop=15&_stpos=&gbr=1 (all items, price+p&p, lowest first)
 * TODO: L�gg till tid kvar av auktion (f�r Ebay/Tradera)
 * TODO: L�gg till info om det �r "K�p nu" eller vanlig auktion (Ebay/Tradera)
 * TODO: L�gg till info om s�ljaren (Ebay/Tradera)
 * TODO: G�r det m�jligt att l�sa in fr�n flera sidor (Ebay/Tradera)
 * TODO: API f�r Tradera/Ebay??
 * TODO: Grafiskt gr�nssnitt m klickbara annonser, g�rna helt inh�mtade och renderade i programmet med en "L�s mer"-knapp f�r webbl�saren.
 * TODO: Snyggt vore om man h�mtade produktbilder, �ven fr�n fotosidan
 * TODO: Ut�ka med fler s�ljsajter
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
                + "([^\"]+)"      // (Detta �r grupp 1, dvs url) Allt fr�n citationstecknet till
                + "\"[^>]*>"      // sista citationstecknet
                + "([^<]+)"       // Allt fr�n > till </a>, dvs produkttitel
                + "</a><p class=\"list_price\">"
                + "([^<]+[0-9]+)" 	// Ta sedan allt d�refter som �r siffror, dvs pris
                + "(kr|KR|Kr|:-&nbsp;|:-|sek|SEK|Sek|$|�|�)" // Hitta den monet�ra enheten
    			+ ".*?</p>";		// Till slutet p� <p>-taggen med klass "list_price", scanna �ven allt d�rimellan.
		
		String fotosidanExpr = "<a class=\"viewdetail\" href=\""
                + "([^\"]+)"      // (Detta �r grupp 1, dvs url) Allt fr�n citationstecknet till
                + "\"[^>]*>"      // sista citationstecknet
                + "([^<]+)"       // Allt fr�n > till </a>, dvs produkttitel
                + "</a>"
                + ".*?<td align=\"right\" valign=\"top\" nowrap[>]*"
                + "([0-9]*\\s*[0-9]*)" 	// Ta sedan allt fr�n taggen ovan, dvs pris
                + "([\\s]kr|KR|Kr|:-&nbsp;|:-|sek|SEK|Sek|$|�|�)" // Hitta den monet�ra enheten
    			+ ".*?</td>";
		
		String traderaExpr = "<div class=\"ObjectHeadline\"><a href=\""
                + "([^\"]+)"      // (Detta �r grupp 1, dvs url) Allt fr�n citationstecknet till
                + "\"[^>]*>"      // sista citationstecknet
                + "([^<]+)"       // Allt fr�n > till </a>, dvs produkttitel
                + "</a>"
                + ".*?<div class=\"price\">"
                + ".*?[^>]*>([0-9]*\\s*[0-9]*)" 	// Ta sedan allt fr�n taggen ovan, dvs pris
                + "([\\s]kr|KR|Kr|:-&nbsp;|:-|sek|SEK|Sek|$|�|�)" // Hitta den monet�ra enheten
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
	 * En metod f�r att scanna en sida efter ett angivet regexp i syfte att hitta produktnamn, url och pris.
	 * OBS! Metoden klarar _inte_ av artiklar som inte har ett pris. Noll g�r bra, men utel�mnat pris resulterar i
	 * icke-funnen produkt. 
	 * 
	 * Skapad efter artikel p� http://www.javamex.com/tutorials/regular_expressions/example_scraping_html.shtml
	 * 
	 * @param Ett giltigt regexp som ska inneh�lla 4 grupper. Grupp 1 ska hitta url, grupp 2 ska hitta 
	 * 		  produktnamn/beskrivning, grupp 3 priset och grupp 4 den monet�ra enheten.
	 * @param En giltig URL som genererar �nskat s�k.
	 * @return En Lista med Lens-objekt skapade utifr�n vad som hittas
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
			
			// D� fotosidan saknar http://fotosidan... b�r det l�ggas till n�r lens-objektet skapas
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