package org.intracode.o.stockquote.stockquote;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by breza on 6/11/2014.
 */
public class StockInfoActivity extends Activity{

    private static final String TAG = "STOCKQUOTE"; //used for logcat to output messages for the debug

    TextView companyNameTextView;
    TextView yearLowTextView;
    TextView yearHighTextView;
    TextView daysLowTextView;
    TextView daysHighTextView;
    TextView lastTradePriceOnlyTextView;
    TextView changeTextView;
    TextView daysRangeTextView;

    //save all of the node keys from the XML data
    //parent node found in https://developer.yahoo.com/yql/console/?q=show%20tables&env=store://datatables.org/alltableswithkeys#h=select+*+from+yahoo.finance.quote+where+symbol+in+(%22MSFT%22)
    //save all the info if the application closes
    static final String KEY_ITEM = "quote";
    static final String KEY_NAME = "Name";
    static final String KEY_YEAR_LOW = "YearLow";
    static final String KEY_YEAR_HIGH = "YearHigh";
    static final String KEY_DAYS_LOW = "DaysLow";
    static final String KEY_DAYS_HIGH = "DaysHigh";
    static final String KEY_LAST_TRADE_PRICE = "LastTradePriceOnly";
    static final String KEY_CHANGE= "Change";
    static final String KEY_DAYS_RANGE = "DaysRange";

    //initial strings to represent the nodes as well
    String name = "";
    String yearLow = "";
    String yearHigh = "";
    String daysLow = "";
    String daysHigh = "";
    String lastTradePriceOnly= "";
    String change = "";
    String daysRange = "";

    String yahooURLFirst = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22";
    String yahooURLSecond = "%22)&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    //extended Activity and manually defined the onCreateMethod
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_stock_info);

        Intent Intent = getIntent();
        String stockSymbol = Intent.getStringExtra(MainActivity.STOCK_SYMBOL); //reciving what is passed from Main Activity line 156

        //initialize all TextViews
        companyNameTextView = (TextView) findViewById(R.id.companyNameTextView);
        yearLowTextView = (TextView) findViewById(R.id.yearLowTextView);
        yearHighTextView = (TextView) findViewById(R.id.yearHighTextView);
        daysLowTextView = (TextView) findViewById(R.id.daysLowStockView);
        daysHighTextView = (TextView) findViewById(R.id.daysHighStockView);
        lastTradePriceOnlyTextView = (TextView) findViewById(R.id.lastTradePriceOnlyTextView);
        changeTextView = (TextView) findViewById(R.id.changeTextView);
        daysRangeTextView = (TextView) findViewById(R.id.daysRangeTextView);

        //using TAG to send info to the logcat
        Log.d(TAG, "Before URL Creation" + stockSymbol);

        //build the URL for the yahoo webservice
        final String yqlURL = yahooURLFirst + stockSymbol + yahooURLSecond;

        //android tool kit IS NOT thread safe
        //To get web information you have to do it in its own thread
        new MyAsyncTask().execute(yqlURL);

    }

    private class MyAsyncTask extends AsyncTask<String, String, String>{

        protected String doInBackground(String... args) { //String... args same as String[] args
            try {
                URL url = new URL(args[0]);

                //create a connection between the application and URL we are trying to get
                URLConnection connection;
                connection = url.openConnection();

                //allow us to take advantage of HTTP features
                HttpURLConnection httpConnection = (HttpURLConnection) connection;

                //tells us if we were able to connect properly
                int responseCode = httpConnection.getResponseCode();

                if (responseCode == httpConnection.HTTP_OK) {
                    InputStream in = httpConnection.getInputStream();

                    //Pulling XML using the DOM Could also use XML pull parser
                    //dbf below allow us to parse DOM objects
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance().newInstance();

                    //provides DOM document from the XML page
                    DocumentBuilder db = dbf.newDocumentBuilder();

                    Document dom = db.parse(in);

                    Element docEle = dom.getDocumentElement();

                    NodeList nl = docEle.getElementsByTagName("quote");

                    if (nl != null && nl.getLength() > 0) {
                        //add the for loop to be able to get multiple quotes entered all at the same time
                        //not doing in this application but in real world you might
                        for (int i = 0; i < nl.getLength(); i++) {
                            StockInfo theStock = getStockInformation(docEle);

                            //static methods coming from StockInfo.Java
                            name = theStock.getName();
                            yearLow = theStock.getYearLow();
                            yearHigh = theStock.getYearHigh();
                            daysLow = theStock.getDaysLow();
                            daysHigh = theStock.getDaysHigh();
                            lastTradePriceOnly = theStock.getLastTradePriceOnly();
                            change = theStock.getChange();
                            daysRange = theStock.getDaysRange();
                        }

                    }


                }
            } catch (MalformedURLException e) {
                Log.d(TAG, "MalformedURLException", e);
            } catch (IOException e) {
                Log.d(TAG, "IOException", e);
            } catch (ParserConfigurationException e) {
                Log.d(TAG, "ParserConfigurationException", e);
            } catch (SAXException e) {
                Log.d(TAG, "Sax Exception", e);
            } finally {
            }
            return null;
        }

            protected void onPostExecute(String result){

            companyNameTextView.setText(name);
            yearLowTextView.setText("Year Low: " + yearLow);
            yearHighTextView.setText("Year High: " + yearHigh);
            daysLowTextView.setText("Days Low: " + daysLow);
            daysHighTextView.setText("Days High: " + daysHigh);
            lastTradePriceOnlyTextView.setText("Last Price: " + lastTradePriceOnly);
            changeTextView.setText("Change: " + change);
            daysRangeTextView.setText("Daily Price Range: " + daysRange);


        }

            }

        private StockInfo getStockInformation(Element entry){
            String stockName = getTextValue(entry, "Name");
            String stockYearLow = getTextValue(entry, "YearLow");
            String stockYearHigh = getTextValue(entry, "YearHigh");
            String stockDaysLow = getTextValue(entry, "DaysLow");
            String stockDaysHigh = getTextValue(entry, "DaysHigh");
            String stockLastTradePriceOnly = getTextValue(entry, "LastTradePriceOnly");
            String stockChange = getTextValue(entry, "Change");
            String stockDaysRange = getTextValue(entry, "DaysRange");

            StockInfo theStock = new StockInfo(stockDaysLow, stockDaysHigh, stockYearLow, stockYearHigh, stockName, stockLastTradePriceOnly, stockChange, stockDaysRange);

            return  theStock;

        }

        private String getTextValue(Element entry, String tagName){
            String tagValueToReturn = null;
            NodeList nl = entry.getElementsByTagName(tagName);
            if (nl!= null && nl.getLength() >0 ){
                Element element = (Element) nl.item(0);

                tagValueToReturn = element.getFirstChild().getNodeValue();
            }
            return tagValueToReturn;
        }

    }





