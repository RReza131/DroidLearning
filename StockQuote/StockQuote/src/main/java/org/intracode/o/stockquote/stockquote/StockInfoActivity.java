package org.intracode.o.stockquote.stockquote;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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

    //name of nodes from the yahoo.com source we are pulling from
    String[][] xmlPullParserArray = {{"AverageDailyVolume", "0"},{"Change", "0"},{"DaysLow", "0"},
            {"DaysHigh", "0"},{"YearLow", "0"},{"YearHigh", "0"},{"MarketCapitalization", "0"},
            {"LastTradePriceOnly", "0"},{"DaysRange", "0"},{"Name", "0"},{"Symbol", "0"},
            {"Volume", "0"}, {"StockExchange", "0"}};

    int parseArrayIncrement = 0;

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
            try{
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(true);

                XmlPullParser parser = factory.newPullParser();

                parser.setInput(new InputStreamReader(getURlData(args[0])));

                beginDocument(parser, "query"); //why are we passing in query?
                //get the targeted event type that starts at Start_Document
                int eventType = parser.getEventType();

                do{
                    //cycle through elements of XML just as long as it is not a start or endtag
                    //dont understand - so this is going ot cycle through all the elements  then do parser.next() and everything below for each?
                    //wouldn't you want parser.next() and everything below in your nextElement while loop as its going through each element?
                    nextElement(parser);

                    parser.next();

                    eventType = parser.getEventType();

                    //check to see if the value was found between two tags
                    if(eventType == XmlPullParser.TEXT){

                        String valueFromXML = parser.getText();

                        xmlPullParserArray[parseArrayIncrement++][1] = valueFromXML;

                    }

                } while(eventType != XmlPullParser.END_DOCUMENT);
            }
            catch (ClientProtocolException e){
                e.printStackTrace();
            }
            catch (XmlPullParserException e){
                e.printStackTrace();
            }
            catch (URISyntaxException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            finally {

            }
            return null;
        }

            public  InputStream getURlData(String url) throws URISyntaxException, ClientProtocolException, IOException{ //do the throws otherwise we would need a try block
                //get access to URL resources
                DefaultHttpClient client = new DefaultHttpClient();
                //a way to get infromation from URl
                HttpGet method = new HttpGet(new URI(url));
                //get a response from client whether connection was stable or not
                HttpResponse res = client.execute(method);
                //tells system where the content is coming from
                return  res.getEntity().getContent();
            }

            public  final void beginDocument(XmlPullParser parser, String firstElementName)throws XmlPullParserException, IOException {

                int type; //type of tag we are working with
                //define nothing in the while block below because all we want to do is skip what does not fulfill the conditions below
                //dont understand why we are doing what is below??
                while((type = parser.next()) != parser.START_TAG && type != parser.END_DOCUMENT){
                    ;
                }
                //throw error if start tag not found at all
                if(type != parser.START_TAG){
                    throw new XmlPullParserException("No Start Tag Found");
                }
                //verify that the tag passed in is really the first element passed in from our yahoo XML document
                if(!parser.getName().equals(firstElementName)){
                    throw new XmlPullParserException("Unexpected Start Tag Found " + parser.getName() + ", expected " + firstElementName);
                }
            }

            public  final void nextElement(XmlPullParser parser)throws XmlPullParserException, IOException {
                int type;
                //cycle through all that data.
                while((type = parser.next()) != parser.START_TAG && type != parser.END_DOCUMENT){
                    ;
                }
            }
            protected void onPostExecute(String result){ //writes information to our GUI

            companyNameTextView.setText( xmlPullParserArray[9][1]); //this is the 9th element in the stockquote XML that is returned
            yearLowTextView.setText("Year Low: " + xmlPullParserArray[4][1]);
            yearHighTextView.setText("Year High: " + xmlPullParserArray[5][1]);
            daysLowTextView.setText("Days Low: " + xmlPullParserArray[2][1]);
            daysHighTextView.setText("Days High: " + xmlPullParserArray[3][1]);
            lastTradePriceOnlyTextView.setText("Last Price: " + xmlPullParserArray[7][1]);
            changeTextView.setText("Change: " + xmlPullParserArray[1][1]);
            daysRangeTextView.setText("Daily Price Range: " + xmlPullParserArray[8][1]);


        }

        }


    }





