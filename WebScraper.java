package com.marmanis.lukas.sports;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class WebScraper {
    final static String baseURL = "https://www.basketball-reference.com/leagues/NBA_";  // This does not have the year or month, which we will go get later.
    final static String[] years = new String[]{"2020","2021","2022"};
    public static void main(String[] args) {
        List <String> yearlyScheduleURLList = new ArrayList<>();
        List<String> monthList;
        List<String> boxScoreURLList = new ArrayList<>();


        try {
            yearlyScheduleURLList = getYearlyScheduleURLList(yearlyScheduleURLList);
            for (String year : yearlyScheduleURLList){
                monthList = getMonthList(year);
                for (String yearAndMonthURL: monthList){
                    boxScoreURLList = getBoxScoreURLList(boxScoreURLList,yearAndMonthURL);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }


        
        
    }

    private static List<String> getYearlyScheduleURLList(List<String> emptyYearlyScheduleList) {
        for(String year : years){
            emptyYearlyScheduleList.add(baseURL+year+"_games.html");
        }
        return  emptyYearlyScheduleList; //It is no longer empty
    }

    private static List<String> getMonthList(String yearUrl) throws IOException {
        List<String> monthList = new ArrayList<>();
        try {

            Document webPage = Jsoup.connect(yearUrl).get();

            Elements monthsTab = webPage.select("div.filter");
            Elements months = monthsTab.select("a");
            for (Element month : months) {
                String monthURL = month.attr("href");
                monthList.add(monthURL);
            }

        }catch (IOException exception){
            System.err.println("An error occurred while trying to connect to the url: " + yearUrl);
            System.err.println("Error message: " + exception.getMessage());
            throw exception;
        }
        return monthList;
    }
    private static List<String> getBoxScoreURLList(List<String> boxScoreURLList, String url) throws IOException {
        try {
            Document doc = Jsoup.connect(url).get();

            Elements table = doc.select("table#schedule");
            Elements rows = table.select("tr:not(:first-child)");

            

            for (Element row : rows) {
                Element centerCell = row.select(".center").first();
                if (centerCell != null) {
                    String link = centerCell.select("a").attr("href");
                    boxScoreURLList.add(link);
                }
            }
        }catch (IOException exception){
            System.err.println("An error occurred while trying to connect to the url: " + url);
            System.err.println("Error message: " + exception.getMessage());
            throw exception;

        }

        return boxScoreURLList;
    }
}