import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegexTester {

    public static void main(String[] args) throws IOException{
    	
    	String expr = "<div class=\"ObjectHeadline\"><a href=\""
                + "([^\"]+)"      // (Detta är grupp 1, dvs url) Allt från citationstecknet till
                + "\"[^>]*>"      // sista citationstecknet
                + "([^<]+)"       // Allt från > till </a>, dvs produkttitel
                + "</a>"
                + ".*?<div class=\"price\">"
                + ".*?[^>]*>([0-9]*\\s*[0-9]*)" 	// Ta sedan allt från taggen ovan, dvs pris
                + "([\\s]kr|KR|Kr|:-&nbsp;|:-|sek|SEK|Sek|$|€|£)" // Hitta den monetära enheten
    			+ ".*?<div class=\"shipping\">";	
    	Pattern patt = Pattern.compile(expr,Pattern.DOTALL | Pattern.UNIX_LINES);
		URL url = new URL("http://www.tradera.com/finding.mvc/itemlisting?search=zuiko&listtype=0&sort=128");
		Matcher m = patt.matcher(getURLContent(url));
		int i = 0;
		while (m.find()) {
			String stateURL = m.group(1);
			String stateName = m.group(2);
			String price = m.group(3);
			String monUnit = m.group(4);
//			System.out.println(stateURL + ": " +stateName);
//			System.out.println(stateURL + ": " +stateName + " kostar "+price);
			System.out.println(stateURL + ": " +stateName + " kostar "+price + " i "+monUnit);
			i++;
		}		
		System.out.println(i);
    }
    
    public static CharSequence getURLContent(URL url) throws IOException {
    	  URLConnection conn = url.openConnection();
    	  String encoding = conn.getContentEncoding();
    	  if (encoding == null) {
    	    encoding = "utf-8";
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