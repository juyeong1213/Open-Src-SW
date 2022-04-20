package Sim;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class sim {


    public static void main(String[] args) throws IOException {
        String URL = "https://kupis.konkuk.ac.kr/sugang/acd/cour/time/SeoulTimetableInfo.jsp";
        Connection conn = Jsoup.connect(URL);

        Document document = conn.get();

        Elements thinkings = document.select(".thinking");



    }
}
