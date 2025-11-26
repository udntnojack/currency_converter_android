package com.example.montgomery_jack_s2429631;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class parserGetter implements Runnable
{
    private final static int MESSAGE_UPDATE_TEXT_CHILD_THREAD = 1;
    private String result;
    private TextView rawDataDisplay;
    private LinkedList<exchangeItem> exchangeItems;
    private String url;
    private final Handler handler;
    private boolean firstTime;
    public parserGetter(String aurl, Handler handler, boolean firstTime){
        url = aurl;
        this.handler = handler;
        this.firstTime = firstTime;
    }

    @Override
    public void run() {
        URL aurl;
        URLConnection yc;
        BufferedReader in = null;
        String inputLine = "";
        exchangeItems = new LinkedList<exchangeItem>();


        Log.d("MyTask", "in run");

        try {
            Log.d("MyTask", "in try");
            aurl = new URL(url);
            yc = aurl.openConnection();
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                result = result + inputLine;
            }
            in.close();
        } catch (IOException ae) {
            Message message = new Message();
            message.what = 6;
            handler.sendMessage(message);
            return;
        }

        //Clean up any leading garbage characters
        int i = result.indexOf("<?"); //initial tag
        result = result.substring(i);

        //Clean up any trailing garbage at the end of the file
        i = result.indexOf("</rss>"); //final tag
        result = result.substring(0, i + 6);

        // Now that you have the xml data into result, you can parse it
        try {
            XmlPullParserFactory factory =
                    XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

//stop at end of document
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    System.out.println("Start document");
                } else if (eventType == XmlPullParser.TEXT) {
                    System.out.println("Text " + xpp.getText());
                } else if (eventType == XmlPullParser.START_TAG) {
                    if(xpp.getName().equals("item")){
                        boolean getData = true;
                        String country = "";
                        String countryCode = "";
                        String date = "";
                        float ratio = 0f;
                        while(getData){
                            eventType = xpp.next();
                            String tagName = xpp.getName();
                            if (eventType == XmlPullParser.START_TAG && tagName.equals("title")){
                                String text = xpp.nextText();
                                System.out.println(text);
                                String[] tmp = text.split("/");
                                if (tmp.length == 2){
                                    tmp = tmp[1].split("\\(");
                                    country = tmp[0];
                                    countryCode = tmp[1].substring(0, tmp[1].length() -1);
                                }
                            }
                            if(eventType == XmlPullParser.START_TAG && tagName.equals("pubDate")){
                                date = xpp.nextText();
                            }
                            if(eventType == XmlPullParser.START_TAG && tagName.equals("description")){

                                String text = xpp.nextText();
                                String[] tmp = text.split("=");
                                if(tmp.length == 2){
                                    tmp = tmp[1].split(" ");
                                    ratio = Float.parseFloat(tmp[1]);
                                }
                                getData = false;
                            }
                        }
                        exchangeItem item = new exchangeItem(country, countryCode, ratio, date);
                        exchangeItems.add(item);
                    }
                } else if (eventType == XmlPullParser.END_TAG) {

                }
                eventType = xpp.next(); //advances to next event and returns type
            } //loop along the document
            if(firstTime){
                Message message = new Message();
                message.what = 1;
                message.obj = exchangeItems;
                handler.sendMessage(message);
            }
            else{
                Message message = new Message();
                message.what = 4;
                message.obj = exchangeItems;
                handler.sendMessage(message);
            }


        } catch (XmlPullParserException e) {
            Message message = new Message();
            message.what = 6;
            handler.sendMessage(message);
            //throw new RuntimeException(e);
        } catch (IOException e) {
            Message message = new Message();
            message.what = 6;
            handler.sendMessage(message);
            //throw new RuntimeException(e);
        }


        // Now update the TextView to display raw XML data
        // Probably not the best way to update TextView
        // but we are just getting started !
    }
}
