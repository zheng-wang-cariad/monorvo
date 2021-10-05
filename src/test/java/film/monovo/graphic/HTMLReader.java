package film.monovo.graphic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;

public class HTMLReader {

    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.parse(new File("/Users/kolinsky.hexad/Documents/shop/tmp/55d57cd6-4a1e-4d7b-8d6b-3a9be1534af1.html"), "utf-8");
        var elements = doc.select("div:contains(Bestellnummer:)").select("a[href]");
        for (Element e: elements) {
     //       for(Element e2: e.getAllElements())
           // System.out.println(e.toString());
            //
            System.out.println(e.childNodes().get(0).toString());
        }

//        elements = doc.select("a:contains(Sende dem Käufer eine E-Mail)");
//        for (Element e: elements) {
//            //       for(Element e2: e.getAllElements())
//            System.out.println(e.attr("href").toString().replace("mailto:", ""));
//        }
//
//        var field = getAddressLine(doc, "name");
//        System.out.println(field);
//
//        field = getAddressLine(doc, "first-line");
//        System.out.println(field);
//
//        field = getAddressLine(doc, "city");
//        System.out.println(field);
//
//        field = getAddressLine(doc, "zip");
//        System.out.println(field);
//
//        field = getAddressLine(doc, "country-name");
//        System.out.println(field);
    }

    private static String getAddressLine(Document doc, String name) {
        var elements = doc.getElementsByClass(name);
        for (Element e: elements) {
            return e.childNodes().get(0).toString();
        }
        return "";
    }
}
