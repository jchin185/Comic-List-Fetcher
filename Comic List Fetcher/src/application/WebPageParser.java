package application;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * This class parses a url given a date using Jsoup.
 * 
 * @author Jesse
 *
 */
public class WebPageParser {

	public WebPageParser() {

	}

	public static List<String> parsePage(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		String urlp1 = "http://www.comiclist.com/index.php/lists/comiclist-new-comic-book-releases-list-for-";
		String urlp2 = "-csv";
		StringBuilder url = new StringBuilder(urlp1);
		url.append(date.format(formatter));
		url.append(urlp2);
		try {
			Document document = Jsoup.connect(url.toString()).get();
			Elements e = document.select("p:contains(PUBLISHER,TITLE,PRICE)");
			List<String> issues = new ArrayList<String>(
					Arrays.asList(e.html().split("<br> ")));
			issues.remove(0);
			return issues;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
