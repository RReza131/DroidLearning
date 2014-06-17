package org.intracode.o.stockquote.stockquote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;


public class MainActivity extends ActionBarActivity {

    public final static String STOCK_SYMBOL = "com.example.stockquote.STOCK";

    private SharedPreferences stockSymbolsEntered;

    private TableLayout stockTableScrollView;

    private EditText stockSymbolEditText;

    Button enterStockSymbolButton;
    Button deleteStocksButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Retrieve any stock symbols entered previously
        stockSymbolsEntered = getSharedPreferences("stockList", MODE_PRIVATE); //mode private means only accessible by your app Mode_World_Readable and Writable are your other options
        //what is the significance of stockList?? what does "stockList" hold that the preference can be gathered from??? -- StockList is the key for StockSymbolsEntered
        //initialize all components
        stockTableScrollView = (TableLayout) findViewById(R.id.stockTableScrollView);
        stockSymbolEditText = (EditText) findViewById(R.id.stockSymbolEditText);
        enterStockSymbolButton = (Button) findViewById(R.id.enterStockSymbolButton);
        deleteStocksButton = (Button) findViewById(R.id.deleteStocksButton);

        enterStockSymbolButton.setOnClickListener(enterStockButtonListener);
        deleteStocksButton.setOnClickListener(deleteStocksButtonlistener);

        updateSavedStockList(null); //going to update scroll view area if null is passed in otherwise add a new entery to the scroll list.

    }

    private void updateSavedStockList(String newStockSymbol){
        String[] stocks = stockSymbolsEntered.getAll().keySet().toArray(new String[0]); //note stockSymbolsEntered is the Shared Preferences
        Arrays.sort(stocks, String.CASE_INSENSITIVE_ORDER); //sort alphabetically and ignore casing

        //if a stock symbol was entered then add it to the array otherwise display all that are in the array
        if(newStockSymbol != null){
            insertStockInScrollView(newStockSymbol, Arrays.binarySearch(stocks, newStockSymbol));
        }else{
            for (int i = 0; i < stocks.length; i++){
                insertStockInScrollView(stocks[i], i);
            }
        }
    }

    private void insertStockInScrollView(String stock, int arrayIndex){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); //allows you to set or create a row from stock_quote_row.xml inside the scrollview

        View newStockRow = inflater.inflate(R.layout.stock_quote_row, null);

        TextView newStockTextView = (TextView) newStockRow.findViewById(R.id.stockSymbolTextView);

        newStockTextView.setText(stock);

        Button stockQuoteButton = (Button) newStockRow.findViewById(R.id.stockQuoteButton);
        stockQuoteButton.setOnClickListener(getStockActivityListener);

        Button quoteFromWebButton = (Button) newStockRow.findViewById(R.id.quoteFromWebButton);
        quoteFromWebButton.setOnClickListener(getStockFromWebSiteListener);

        stockTableScrollView.addView(newStockRow,arrayIndex);
    }

    private void saveStockSymbol(String newStock){
        String isTheStockNew = stockSymbolsEntered.getString(newStock,null);
        SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit(); //Editor stores the keyvalue pairs of the stock symbols

        preferencesEditor.putString(newStock, newStock); //Both newStock since the key and the value are going to be the same this time
        preferencesEditor.apply();

        if(isTheStockNew == null){
            updateSavedStockList(newStock); //if this is new then pass in new stock
        }
    }

    public View.OnClickListener enterStockButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //check if a stock symbol has been entered into edit text
            if(stockSymbolEditText.getText().length() > 0){
                saveStockSymbol(stockSymbolEditText.getText().toString()); //add the text then clear the text box
                stockSymbolEditText.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //force keyboard to close
                imm.hideSoftInputFromWindow(stockSymbolEditText.getWindowToken(),0);

        }else{ //create an alert box saying they did not enter anything
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.invalid_stock_symbol);
                builder.setPositiveButton(R.string.ok, null);
                builder.setMessage(R.string.missing_stock_symbol);

                AlertDialog theAlertDialog = builder.create();
                theAlertDialog.show();
            }
    }

    };

    //delete all the stocks in the scroll view
    private void deleteAllStocks(){
        stockTableScrollView.removeAllViews();
    }

    public View.OnClickListener deleteStocksButtonlistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            deleteAllStocks();

            SharedPreferences.Editor preferenceEditor = stockSymbolsEntered.edit();
            preferenceEditor.clear();
            preferenceEditor.apply();
        }
    };

    public View.OnClickListener getStockActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            TableRow tableRow = (TableRow) view.getParent();

            TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextView);

            String stockSymbol = stockTextView.getText().toString();
            //start a new activity
            Intent intent = new Intent(MainActivity.this, StockInfoActivity.class);

            intent.putExtra(STOCK_SYMBOL,stockSymbol); //pass into the new activity

            startActivity(intent);
        }
    };

    public View.OnClickListener getStockFromWebSiteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //in the future would probably do in a method since you are repeating code
            TableRow tableRow = (TableRow) view.getParent(); //getting the distinct row that contains the information you need

            TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextView);

            String stockSymbol = stockTextView.getText().toString();

            String stockURL = getString(R.string.yahoo_stock_url) + stockSymbol;

            //passing your infromation to the web Browser.
            Intent getStockWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(stockURL));
            startActivity(getStockWebPage);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
