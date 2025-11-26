/*  Starter project for Mobile Platform Development - 1st diet 25/26
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 jack montgomery
// Student ID           S2429631
// Programme of Study   software development
//

// UPDATE THE PACKAGE NAME to include your Student Identifier
package com.example.montgomery_jack_s2429631;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;
    private Button reloadButton;
    private static String result;
    private LinkedList<exchangeItem> exchangeItems;
    private LinkedList<exchangeItem> matchingItems;
    private TextView errorMessage;
    private TextView errorAboutPage;
    private ViewFlipper flip;
    private exchangeItem selected;
    private boolean onGPB = true;
    private Button ConvertSetting;
    private AutoCompleteTextView autoComplete;
    private AutoCompleteTextView autoCompleteName;
    private timer timer;

    private TextView lastpulledTime;
    private boolean isUserTyping = true;
    private ImageView flagImage;
    private float currencyAmount;
    private Handler mainHandler;
    private final static int MESSAGE_UPDATE_TEXT_CHILD_THREAD = 1;
    public static void SetResult(String input)
    {
        result = input;
    }
    private String url1="";
    private String urlSource="https://www.fx-exchange.com/gbp/rss.xml";

    @Override protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flip = (ViewFlipper) findViewById(R.id.myVFlip);
        createUpdateUiHandler(); // Set up the raw links to the graphical components
        spinner = (Spinner)findViewById(R.id.spinnerCountries);
        reloadButton = (Button)findViewById(R.id.reloadButton);
        ConvertSetting = (Button)findViewById(R.id.ConvertSetting);
        exchangeItems = new LinkedList<exchangeItem>();
        autoComplete = findViewById(R.id.autoCompleteTextView);
        autoCompleteName = findViewById(R.id.autoCompleteName);
        flagImage = findViewById(R.id.flag);
        lastpulledTime = findViewById(R.id.lastPull);
        errorMessage = findViewById(R.id.errorMessage);
        errorAboutPage = findViewById(R.id.errorAboutPage);
        updateToolbarTitle("Loading please wait");
        runParser(true);
        setListeners();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //create menu when task bar loads
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    private void updateToolbarTitle(String newTitle){
        // update title of toolbar whenever page loads
        TextView tvTitle = findViewById(R.id.pageTitle);
        tvTitle.setText(newTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //change screen when user interacts with the menu
        if (id == R.id.Convert) {
            flip.setDisplayedChild(flip.indexOfChild(findViewById(R.id.ConvertPage)));
            updateSpinner();
            updateToolbarTitle("Convert currency");
            return true;
        } else if (id == R.id.search) {
            flip.setDisplayedChild(flip.indexOfChild(findViewById(R.id.searchPage)));
            updateToolbarTitle("Country Information");
            return true;

        } else if (id == R.id.about) {
            flip.setDisplayedChild(flip.indexOfChild(findViewById(R.id.aboutPage)));
            updateToolbarTitle("about us");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public Handler getHandler(){
        return mainHandler;
    }
    private void createUpdateUiHandler(){
        if(mainHandler == null){
            mainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    // This runs on the main thread
                    if (msg.what == 1) {
                        //when parser returns the first time, get parser data and load first screen
                        exchangeItems = (LinkedList<exchangeItem>)msg.obj;
                        exchangeItems.sort(Comparator.comparing(exchangeItem::getCountry));
                        Toolbar myToolbar = (Toolbar)findViewById(R.id.toolbar);
                        setSupportActionBar(myToolbar);
                        updateSpinner();
                        flip.setDisplayedChild(flip.indexOfChild(findViewById(R.id.ConvertPage)));
                        updateToolbarTitle("Convert currency");
                        SetTime();
                        runTimer();

                    }
                    if (msg.what == 2) {
                        //get information for dropdown
                        matchingItems = (LinkedList<exchangeItem>)msg.obj;
                        matchingItems.sort(Comparator.comparing(exchangeItem::getCountryCode));



                        ArrayAdapter<exchangeItem> adapter =
                                new searchCodeAdapter(autoComplete.getContext(), matchingItems);

                        autoComplete.setAdapter(adapter);
                        autoComplete.post(autoComplete::showDropDown);
                    }
                    if (msg.what == 3) {
                        //get information for name drop down
                        matchingItems = (LinkedList<exchangeItem>)msg.obj;
                        matchingItems.sort(Comparator.comparing(exchangeItem::getCountry));

                        ArrayAdapter<exchangeItem> adapter = new ArrayAdapter<>(autoCompleteName.getContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                matchingItems);

                        autoCompleteName.setAdapter(adapter);
                        autoCompleteName.post(autoCompleteName::showDropDown);
                    }
                    if (msg.what == 4) {
                        //get information from parser when running again
                        exchangeItems = (LinkedList<exchangeItem>)msg.obj;
                        exchangeItems.sort(Comparator.comparing(exchangeItem::getCountry));
                        updateSpinner();
                        SetTime();
                        errorAboutPage.setText("");
                    }
                    if (msg.what == 5) {
                        //update parser
                        runParser(false);
                        SetTime();
                    }
                    if (msg.what == 6) {
                        //display error messages
                        errorMessage.setText("an error occured please check your connection");
                        errorAboutPage.setText("an error occured please check your connection");
                    }
                    return true;
                }
            });
        }
    }
    private void updateSpinner(){
        ArrayAdapter<exchangeItem> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                exchangeItems
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    public void updateFlag(){

        //update flag whenever user selects a new country
        String flag = selected.getflagCode().toLowerCase();
        int resId = getResources().getIdentifier(flag, "drawable", getPackageName());
        View current = flip.getCurrentView();

        //check page
        if (current.getId() == R.id.ConvertPage) {
            if (resId != 0) {
                flagImage.setImageResource(resId);
            } else {
                flagImage.setImageResource(R.drawable.gb);
            }
        }
        else{
            ImageView IV = findViewById(R.id.flagInfo);
            if (resId != 0) {
                IV.setImageResource(resId);
            } else {
                IV.setImageResource(R.drawable.gb);
            }
        }
    }

    public void updateConverter(){

        if(selected != null){
            //set is user typing to false so menus are not triggered
            isUserTyping = false;
            autoCompleteName.setText(selected.getCountry(), false);
            autoComplete.setText(selected.getCountryCode(), false);
            isUserTyping = true;
            updateFlag();

            //change currency calculation based on what the user has selected
            if(onGPB){
                if(currencyAmount > 0 && selected != null){
                    TextView rate = findViewById(R.id.exchangeRate);
                    rate.setText("Exchange Rate: "+ selected.getAmount());
                    TextView amount = findViewById(R.id.Amount);
                    setAmountColour(rate);
                    float tmp = selected.getAmount() * currencyAmount;
                    amount.setText("Amount: £"+ tmp);
                }
            }
            else {
                if (currencyAmount > 0 && selected != null) {
                    TextView rate = findViewById(R.id.exchangeRate);
                    rate.setText("Exchange Rate: " + selected.getAmount());
                    TextView amount = findViewById(R.id.Amount);
                    setAmountColour(rate);
                    float tmp = currencyAmount/ selected.getAmount();

                    String countryCode = selected.getflagCode();
                    Locale locale = new Locale("", countryCode);
                    //change currency symbol
                    String currencySymbol = "?";
                    try {
                        Currency currency = Currency.getInstance(locale);
                        currencySymbol = currency.getSymbol();
                    } catch (IllegalArgumentException e) {

                        e.printStackTrace();
                    }

                    amount.setText("Amount: " + currencySymbol + tmp);
                }
            }
        }
    }
    public void SetTime(){
        String formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        lastpulledTime.setText("Last updated: " + formatted);
    }
    public void setAmountColour(TextView amount){
        //change colour of text based on amount value
        if(selected.getAmount() >= 0 && selected.getAmount() < 1 ){
            amount.setTextColor(getResources().getColor(R.color. YellowGreen,null));;
        }
        else if(selected.getAmount() > 1 && selected.getAmount() < 5){
            amount.setTextColor(getResources().getColor(R.color. DarkGreen,null));;
        }
        else if(selected.getAmount() > 5 && selected.getAmount() < 10){
            amount.setTextColor(getResources().getColor(R.color. Orange,null));;
        }
        else{
            amount.setTextColor(getResources().getColor(R.color. Red,null));;
        }


    }

    public void setListeners(){
        //set all listeners for user ineraction
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = (exchangeItem) parent.getItemAtPosition(position);
                TextView rateInfo = findViewById(R.id.RateInfo);
                setAmountColour(rateInfo);
                rateInfo.setText("rate: " + selected.getAmount());

                TextView countryCodeinfo = findViewById(R.id.countryCodeinfo);
                countryCodeinfo.setText("Code: "+ selected.getCountryCode());

                TextView pubDateinfo = findViewById(R.id.pubDateinfo);
                pubDateinfo.setText(selected.getDate());

                updateFlag();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional
            }
        });
        reloadButton.setOnClickListener(v-> {
            runParser(false);
            updateSpinner();
            updateConverter();
        });


        ConvertSetting.setOnClickListener(v-> {

                if(onGPB) {
                    ConvertSetting.setText("Covert to GBP");
                }
                else{
                    ConvertSetting.setText("Covert From GBP");
                }
                onGPB = !onGPB;
                updateConverter();
        });


        EditText amountBox = findViewById(R.id.currencyAmount);

        amountBox.setOnFocusChangeListener((v, hasFocus) -> {
            boolean firstClick = true;
            if (hasFocus) {
                amountBox.setText("");
                firstClick = false;
            }
        });
        autoCompleteName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUserTyping) {
                    String text = autoCompleteName.getText().toString().trim();

                    new Thread(new countryNameGetter(exchangeItems, text, mainHandler)).start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        autoComplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUserTyping) {
                    String text = autoComplete.getText().toString().trim();

                    new Thread(new listGetter(exchangeItems, text, mainHandler)).start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        autoCompleteName.setOnClickListener(v -> {
            autoCompleteName.setText("", false);
            autoComplete.setText("", false);
       });
       autoComplete.setOnClickListener(v -> {
            autoCompleteName.setText("", false);
            autoComplete.setText("", false);
        });
        autoCompleteName.setOnItemClickListener((parent, view, position, id) -> {

            selected = (exchangeItem) parent.getItemAtPosition(position);

            updateConverter();
        });

        autoComplete.setOnItemClickListener((parent, view, position, id) -> {

            selected = (exchangeItem) parent.getItemAtPosition(position);
            isUserTyping =false;
            autoComplete.setText(selected.getCountryCode(), false);
            isUserTyping =true;

            updateConverter();
        });


        amountBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = amountBox.getText().toString().trim();

                if (!text.isEmpty()) {
                    currencyAmount = Float.parseFloat(String.valueOf(amountBox.getText()));
                }
                updateConverter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    public void runTimer()
    {
        // create a timer that updates the parser every hour
        new Thread(new timer(mainHandler)).start();
    }


    public void runParser(boolean firstTime)
    {
        // Run network access on a separate thread;
        new Thread(new parserGetter(urlSource, mainHandler, firstTime)).start();
    } //


}