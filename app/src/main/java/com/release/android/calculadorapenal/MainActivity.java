package com.release.android.calculadorapenal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.release.android.calculadorapenal.util.IabBroadcastReceiver;
import com.release.android.calculadorapenal.util.IabHelper;
import com.release.android.calculadorapenal.util.IabHelper.IabAsyncInProgressException;
import com.release.android.calculadorapenal.util.IabResult;
import com.release.android.calculadorapenal.util.Inventory;
import com.release.android.calculadorapenal.util.Purchase;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;

import static android.util.Log.e;
import static android.view.View.GONE;
import static com.release.android.calculadorapenal.R.id.result;

public class MainActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {

    static final String TAG = "CALCULADORA PENAL";
    // Does the user have the full version?
    boolean mIsNoAds = false;

    //Adview setup
    private AdView mPublisherAdView;
    private InterstitialAd mInterstitialAd;

    //SKUs for the no ads version
    static final String SKU_NOADS = "noads";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    //Helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;


    OperationAdapter adapter;
    ArrayList<Operation> operations = new ArrayList<>();
    TextView computeButton;


    private static final String DEFAULT_IS_SUM_VALUE = "+";
    private static Sentence sentence = new Sentence(0, 0, 0, 0);
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize the Mobile Ads SDK
        MobileAds.initialize(this, "ca-app-pub-1231118493223046~4446000418");

        //app-specific public key
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BA" + middleBit() + "xkxq/LayOJkmfvpe9eHBOVtpaMrCYTSlgoJZTqhEDBDQVtBTuzurUR3iQZklDQ4uJmUPFXwOtU7xuG/dv8i4GGr1bk+tuZm" + endBit("FZMjqZsm7yYnD/rAKlmZH09iHJ0k448w5M20qPLmWAUNI9lON4Ed+HXwU2mi+GJPQGuKVdPl3gwmrRoCkaNuaMIfbgh/ON6avGGYv3abHiUuNYsKiXQoa");

        // Create the helper, passing it our context and the public key to verify signatures with
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.enableDebugLogging(false);

        //Start setup
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problema iniciando compra in-app: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                mBroadcastReceiver = new IabBroadcastReceiver(MainActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabAsyncInProgressException e) {
                    complain("Erro carregando inventário. Outra operação em progresso.");
                }
            }
        });


        //Set the toolbar and status bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_FULLSCREEN);


        //Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);


        // Initializing sentence textview for the dialog
        final TextView yearSentenceText = (TextView) findViewById(R.id.year_sentence_main);
        final TextView monthSentenceText = (TextView) findViewById(R.id.month_sentence_main);
        final TextView daySentenceText = (TextView) findViewById(R.id.day_sentence_main);
        final TextView daysFSentenceText = (TextView) findViewById(R.id.days_fine_sentence_main);

        //Initializing result textbox
        final TextView resultSentence = (TextView) findViewById(result);

        final ListView sumList = (ListView) findViewById(R.id.list);

        //Footer settings
        View footer = View.inflate(this, R.layout.compute_button, null);
        computeButton = (TextView) footer.findViewById(R.id.compute_button);
        computeButton.setVisibility(GONE);
        computeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResult(resultSentence);
            }
        });

        sumList.addFooterView(footer);


        //Adapter initialization and list setup
        adapter = new OperationAdapter(this, operations);
        sumList.setAdapter(adapter);


        //Setup dialog to show when the sentence is clicked
        LinearLayout sentenceLayout = (LinearLayout) findViewById(R.id.sentence_main);
        sentenceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSentenceDialog(yearSentenceText, monthSentenceText, daySentenceText,
                        daysFSentenceText);
            }
        });

        //Bottom banner setup
        mPublisherAdView = (AdView) findViewById(R.id.publisherAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);

        //Interstitial Ads setup
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1231118493223046/7273212411");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();


        //Setup dialog for fractions
        sumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                final int actualPosition = position;
                final Dialog fractionDialog = new Dialog(MainActivity.this);
                fractionDialog.setContentView(R.layout.fraction_dialog);
                fractionDialog.setTitle(getString(R.string.fraction_dialog_title));

                Button enterNumber = (Button) fractionDialog.findViewById(R.id.fraction_button);

                //Populates Spinner on the fraction dialog
                final Spinner operationSpinner = (Spinner) fractionDialog.findViewById(R.id.is_sum_spinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                        R.array.operations_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                operationSpinner.setAdapter(adapter);

                final NumberPicker numeratorPicker =
                        (NumberPicker) fractionDialog.findViewById(R.id.numerator_picker);
                final NumberPicker denominatorPicker =
                        (NumberPicker) fractionDialog.findViewById(R.id.denominator_picker);
                final EditText fractionDescription = (EditText) fractionDialog.findViewById(R.id.description_edit);


//              Set values on the dialog from the arrayList
                numeratorPicker.setValue(operations.get(actualPosition).getNumerator());
                denominatorPicker.setValue(operations.get(actualPosition).getDenominator());
                operationSpinner.setSelection(adapter.getPosition(operations.get(actualPosition).getIsSum()));

                //Checks if description is null
                if (operations.get(actualPosition).getDescription() != null) {
                    fractionDescription.setText(operations.get(actualPosition).getDescription());
                }


                /*Opens the fraction dialog tho choose a number and send send chosen values to
                TextViews on the item clicked */

                enterNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        changeValues(operationSpinner.getSelectedItem().toString(),
                                numeratorPicker.getValue(), denominatorPicker.getValue(),
                                fractionDescription.getText().toString(), actualPosition, yearSentenceText,
                                monthSentenceText, daySentenceText);


                        fractionDialog.dismiss();
                    }
                });

                fractionDialog.show();
            }
        });

        /*Setup on hold actions sumList item. Opens a dialog and asks the user if he wants to delete
        the operation*/
        sumList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final int actualPosition = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.delete_dialog_message)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                operations.remove(actualPosition);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                builder.create().show();


                return true;
            }
        });

        //Setting up add button, adiciona operação no Array List
        Button addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sentence != null && sentence.getDaysOfSentence() != 0) {
                    adapter.add(new Operation(0, 0, DEFAULT_IS_SUM_VALUE));
                    clickAfter(sumList, operations.size());
                    if (operations.size() == 1)
                        computeButton.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.toast_add_sentence), Toast.LENGTH_SHORT).show();
                }


            }
        });

        //Set the image in the toolbar
        final ImageView bImageView = (ImageView) findViewById(R.id.bg_image);

/*
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.justica, options);
*/
        ViewTreeObserver vto = bImageView.getViewTreeObserver();

        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                bImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                bImageView.setImageBitmap(
                        decodeSampledBitmapFromResource(getResources(),
                                R.drawable.justica, bImageView.getMeasuredWidth(),
                                bImageView.getMeasuredHeight()));

                return true;
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //Opens the fraction dialog after the list item is added
    private void clickAfter(ListView sumList, int lastPos) {
        sumList.performItemClick(sumList.getChildAt(lastPos - 1),
                lastPos - 1, sumList.getAdapter().getItemId(lastPos - 1));
    }

    public void setMainSentence(int year, int month, int day, int daysFine) {
        sentence = new Sentence(year, month, day, daysFine);
    }


    public void changeValues(String newIsSum, int newNumerator,
                             int newDenominator, String newDescription, int position, TextView yearSentenceText,
                             TextView monthSentenceText, TextView daySentenceText) {

        Operation operation = operations.get(position);
        operation.setNumerator(newNumerator);
        operation.setDenominator(newDenominator);
        operation.setIsSum(newIsSum);
        operation.setBaseSentence(sentence);
        operation.setDescription(newDescription);

        adapter.notifyDataSetChanged();


    }

    /*Method that opens the sentence dialog tho choose a number*/
    public void showSentenceDialog(final TextView yearSentenceText,
                                   final TextView monthSentenceText,
                                   final TextView daySentenceText,
                                   final TextView daysFineSentenceText) {

        final Dialog sentenceDialog = new Dialog(MainActivity.this);

        sentenceDialog.setTitle("Selecione a pena inicial");
        sentenceDialog.setContentView(R.layout.sentence_dialog);

        final NumberPicker yearSentence = (NumberPicker)
                sentenceDialog.findViewById(R.id.year_sentence);
        final NumberPicker monthSentence = (NumberPicker)
                sentenceDialog.findViewById(R.id.month_sentence);
        final NumberPicker daySentence = (NumberPicker)
                sentenceDialog.findViewById(R.id.day_sentence);
        final EditText daysFine = (EditText) sentenceDialog.findViewById(R.id.days_fine_sentence);

        yearSentence.setValue(sentence.getYear());
        monthSentence.setValue(sentence.getMonth());
        daySentence.setValue(sentence.getDay());

        Button enterNumber = (Button) sentenceDialog.findViewById(R.id.sentence_button);


        //Sends chosen values to TextViews on the item clicked
        enterNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String daysFineCheck = daysFine.getText().toString();
                if (daysFineCheck.isEmpty())
                    daysFineCheck = "0";

                setMainSentence(yearSentence.getValue(), monthSentence.getValue(),
                        daySentence.getValue(), Integer.parseInt(daysFineCheck));

                yearSentenceText.setText(String.valueOf(sentence.getYear()));
                monthSentenceText.setText(String.valueOf(sentence.getMonth()));
                daySentenceText.setText(String.valueOf(sentence.getDay()));


                daysFineSentenceText.setText(String.valueOf(sentence.getdaysFine()));

                if (operations.size() > 0) {

                    for (int i = 0; i < operations.size(); i++) {
                        Operation currentOperation = operations.get(i);
                        currentOperation.setBaseSentence(sentence);
                        adapter.notifyDataSetChanged();
                    }
                }

                sentenceDialog.dismiss();
            }
        });

        sentenceDialog.show();
    }

    /*Show the result sentence TextView*/
    public void showResult(TextView resultSentence) {
        if (sentence != null) {
            resultSentence.setText(finalSentence());
            resultSentence.setVisibility(View.VISIBLE);
        }

    }

    /*Calculates the result sentence, does not check if sentence is null*/
    private String finalSentence() {
        int daysOfSentence = sentence.getDaysOfSentence();
        int daysFine = sentence.getdaysFine();
        int position;
        double fraction = 0.0;
        Operation currentOperation;

        if (sentence != null)
            for (position = 0; position != operations.size(); position++) {
                currentOperation = operations.get(position);
                if (currentOperation.getIsSum().equals("+"))
                    fraction += ((double) currentOperation.getNumerator() / currentOperation.getDenominator());
                else
                    fraction -= ((double) currentOperation.getNumerator() / currentOperation.getDenominator());
            }
        daysOfSentence = (int) (fraction * daysOfSentence) + daysOfSentence;
        //Result sentence
        Sentence finalSentence = new Sentence(daysOfSentence);
        daysFine = (int) (fraction * daysFine) + daysFine;
        finalSentence.setDaysFine(daysFine);

        return finalSentence.writeSentence();

    }

    public void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    public void clear(View v) {
        operations.clear();

        if (mInterstitialAd.isLoaded() && mInterstitialAd != null && !mIsNoAds)
            mInterstitialAd.show();


        TextView result = (TextView) findViewById(R.id.result);
        TextView yearSentenceText = (TextView) findViewById(R.id.year_sentence_main);
        TextView monthSentenceText = (TextView) findViewById(R.id.month_sentence_main);
        TextView daySentenceText = (TextView) findViewById(R.id.day_sentence_main);
        TextView daysFineText = (TextView) findViewById(R.id.days_fine_sentence_main);
        result.setVisibility(GONE);
        sentence = new Sentence(0, 0, 0, 0);

        yearSentenceText.setText("0");
        monthSentenceText.setText("0");
        daySentenceText.setText("0");
        daysFineText.setText("0");
        adapter.notifyDataSetChanged();
        computeButton.setVisibility(GONE);


    }

    //Image loading: Loads a scaled down version into Memory

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public void startAbout(View view) {
        startActivity(new Intent(this, About.class));
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Ambar")
                .setUrl(Uri.parse("http://ambar.tech"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    void complain(String message) {
        e(TAG, "**** CalculadoraPenal Error: " + message);
        alert(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }

        mPublisherAdView.destroy();
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Compra concluída: " + result + ", compra: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Erro na compra: " + result);
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Erro. Falha na verificação de autenticidade .");
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_NOADS)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is NoAds upgrade. Congratulating user.");
                alert("Obrigado por adquirir a versão NoAds!");
                mIsNoAds = true;
                updateUi();
                setWaitScreen(false);
            }

        }
    };

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        return true;
    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabAsyncInProgressException e) {
            complain("Erro obtendo inventário. Outra operação em progresso.");
        }
    }

    public void updateUi() {
        // update the ads visibility to reflect premium user status
        findViewById(R.id.publisherAdView).setVisibility(mIsNoAds ? View.GONE : View.VISIBLE);
        findViewById(R.id.purchase_button).setVisibility(mIsNoAds ? View.GONE : View.VISIBLE);

    }

    void setWaitScreen(boolean set) {
        findViewById(R.id.activity_main).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_waiting).setVisibility(set ? View.VISIBLE : View.GONE);

    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Falha obtendo inventário: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_NOADS);
            mIsNoAds = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (mIsNoAds ? "NoAds" : "Ads"));

            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    public void onNoAdsButtonClicked(View arg0) {
        Log.d(TAG, "NoAds button clicked; launching purchase flow for NoAds");
        setWaitScreen(true);

        String payload = "";

        try {
            mHelper.launchPurchaseFlow(this, SKU_NOADS, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabAsyncInProgressException e) {
            complain("Erro iniciando o fluxo de compra. Outra operação em progresso.");
            setWaitScreen(false);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPublisherAdView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPublisherAdView.pause();
    }

    private String middleBit(){

        return "QEFAAOCAQ8AMIIBCgKCAQEAyyQXyuhUy7IOCrZx2I++hsQxdqQzTJhvSSe1T2ZWrDhFRlCF0pEMMsljpUNVg3xTuVQcy4P64DQqyq";
    }

    private String endBit(String initial){
        return initial+"VJ5pQAu7XJ1JT+G2GO6D/PQ+iZF243W2j+0ujr8ROODLi8gmEIPQIDAQAB";}
}
