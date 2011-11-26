package GetWebsite;

public class Lens implements Comparable<Lens>{
	private String lens;
	private String url;
	private String urlOrigin;
	private int price;
	private String monetaryUnit;


	public Lens(String lens, String url, String urlOrigin, int price, String monetaryUnit){
		setLens(lens);
		setUrl(url);
		setUrlOrigin(urlOrigin);
		setPrice(price);
		setMonetaryUnit(monetaryUnit);
	}

	public void printLens(){
		System.out.println("Objektiv: " + getLens());
		System.out.println("Pris: " + getPrice()+getMonetaryUnit());
	}
	
	public String getLens() {
		return lens;
	}

	private void setLens(String lens) {
		// Om produkten innehåller radbrytning, ta bort det.
		if(lens.contains("\n")){
			lens = lens.replace("\n", "");
			lens = lens.replaceFirst("^[\\s{1,3}]", "");
		}
		this.lens = lens;
	}

	public String getUrl() {
		return url;
	}

	private void setUrl(String url) {
		// Då fotosidan saknar http://fotosidan... bör det läggas till när lens-objektet skapas
		if(url.contains("fotosidan")){
			String tempUrl = "http://www.fotosidan.se";
			tempUrl = tempUrl.concat(url);
			url = tempUrl;
		}
		else if(url.contains("tradera")){
			String tempUrl = "http://www.tradera.com";
			tempUrl = tempUrl.concat(url);
			url = tempUrl;
		}
		this.url = url;
	}

	public int getPrice() {
		return price;
	}

	private void setPrice(int price) {
		this.price = price;
	}
	
	public String getMonetaryUnit() {
		return monetaryUnit;
	}

	private void setMonetaryUnit(String monetaryUnit) {
		this.monetaryUnit = monetaryUnit;
	}

	@Override
	public int compareTo(Lens anotherLens) {
		int anotherLensPrice = ((Lens) anotherLens).getPrice();
		return this.price - anotherLensPrice;
	}

	public String getUrlOrigin() {
		return urlOrigin;
	}

	public void setUrlOrigin(String urlOrigin) {
		this.urlOrigin = urlOrigin;
	}
	
}
