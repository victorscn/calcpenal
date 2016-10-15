package com.example.android.calculadorapenal;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
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

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    OperationAdapter adapter;
    ArrayList<Operation> operations = new ArrayList<>();
    TextView computeButton;

    private static final String DEFAULT_IS_SUM_VALUE = "+";
    private static Sentence sentence = new Sentence(0, 0, 0, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        final TextView resultSentence = (TextView) findViewById(R.id.result);

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

//TODO make the remove dialog
/*
        sumList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final int actualPosition = position;
                final Dialog removeDialog = new Dialog(MainActivity.this);
                removeDialog.setContentView(R.layout.remove_dialog);


                return false;
            }
        });
*/
        //Ads setup
        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);

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
                        (com.shawnlin.numberpicker.NumberPicker) fractionDialog.findViewById(R.id.numerator_picker);
                final NumberPicker denominatorPicker =
                        (com.shawnlin.numberpicker.NumberPicker) fractionDialog.findViewById(R.id.denominator_picker);


                numeratorPicker.setValue(operations.get(actualPosition).getNumerator());
                denominatorPicker.setValue(operations.get(actualPosition).getDenominator());
                operationSpinner.setSelection(adapter.getPosition(operations.get(actualPosition).getIsSum()));



                /*Opens the fraction dialog tho choose a number and send send chosen values to
                TextViews on the item clicked */

                enterNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        changeValues(operationSpinner.getSelectedItem().toString(),
                                numeratorPicker.getValue(), denominatorPicker.getValue(),
                                actualPosition, yearSentenceText, monthSentenceText, daySentenceText);


                        fractionDialog.dismiss();
                    }
                });

                fractionDialog.show();
            }
        });

        //Setting up add button
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
                             int newDenominator, int position, TextView yearSentenceText,
                             TextView monthSentenceText, TextView daySentenceText) {

        Operation operation = operations.get(position);
        operation.setNumerator(newNumerator);
        operation.setDenominator(newDenominator);
        operation.setIsSum(newIsSum);
        operation.setBaseSentence(sentence);

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

        final NumberPicker yearSentence = (com.shawnlin.numberpicker.NumberPicker)
                sentenceDialog.findViewById(R.id.year_sentence);
        final NumberPicker monthSentence = (com.shawnlin.numberpicker.NumberPicker)
                sentenceDialog.findViewById(R.id.month_sentence);
        final NumberPicker daySentence = (com.shawnlin.numberpicker.NumberPicker)
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


                setMainSentence(yearSentence.getValue(), monthSentence.getValue(),
                        daySentence.getValue(), Integer.parseInt(daysFine.getText().toString()));

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

    public void clear(View v) {
        operations.clear();
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

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.show();
    }


}
