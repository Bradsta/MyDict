package com.gamicarts.mydict.definer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Created by Brad on 2/4/2015.
 */
public class Definer {

    public static String getDefinition(String word) {
        try {
            String nextWord = word.replace(" ", "+");
            URL url = new URL("https://www.google.com/search?q=define+" + nextWord);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setDoInput(true);
            //httpURLConnection.setDoOutput(true); Throws a FileNotFoundException in Android
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Host", "www.google.com");
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:12.0) Gecko/20100101 Firefox/12.0");
            httpURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            httpURLConnection.setRequestProperty("Connection", "close");

            BufferedReader read = new BufferedReader(new InputStreamReader(new GZIPInputStream(httpURLConnection.getInputStream())));
            String line = null;

            String definition = "Not found. You may have misspelled a word, or it could be our problem.";

            while ((line = read.readLine()) != null) {
                if (line.contains("\"display:inline\" data-dobid=\"dfn\"><span>")) {
                    definition = parse("data-dobid=\"dfn\"><span>", "</span>", line);
                    break;
                } else if (line.contains("class=\"kno-rdesc\"><span>")) {
                    definition = parse("class=\"kno-rdesc\"><span>", "</span>", line);
                    break;
                } else if (line.contains("<span class=\"_Tgc\">")) {
                    definition = parse("<span class=\"_Tgc\">", "</span>", line);
                    break;
                }
                //System.out.println(line);
            }

            read.close();

            return definition.replaceAll("</?b>", "").replaceAll("&#39;", "'");
        } catch (Exception e) { // generic
            e.printStackTrace();
        }

        return "Not found. You may have misspelled a word, or it could be our problem.";
    }

    private static String parse(String s, String e, String mixed) {
        try {
            return mixed.substring(mixed.indexOf(s) + s.length(), mixed.indexOf(e, mixed.indexOf(s) + s.length()));
        } catch(Exception ee) {
            return null;
        }
    }

}

