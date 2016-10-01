package com.example.android.calculadorapenal;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    OperationAdapter adapter;
    ArrayList<Operation> operations = new ArrayList<>();


    private static final String DEFAULT_IS_SUM_VALUE = "+";
    private static Sentence sentence = new Sentence(0,0,0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);

        // Initializing sentence textview for the dialog
        final TextView yearSentenceText = (TextView) findViewById(R.id.year_sentence_main);
        final TextView monthSentenceText = (TextView) findViewById(R.id.month_sentence_main);
        final TextView daySentenceText = (TextView) findViewById(R.id.day_sentence_main);



        ListView sumList = (ListView) findViewById(R.id.list);
        sumList.addFooterView(View.inflate(this, R.layout.compute_button, null));

        //Adapter initialization and list setup
        adapter = new OperationAdapter(this, operations);
        sumList.setAdapter(adapter);

        //Setup dialog to show when the sentence is clicked
        LinearLayout sentenceLayout = (LinearLayout) findViewById(R.id.sentence_main);
        sentenceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSentenceDialog(yearSentenceText, monthSentenceText, daySentenceText);
            }
        });


        //Setup dialog for fractions
        sumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                final int actualPosition = position;
                final Dialog fractionDialog = new Dialog(MainActivity.this);
                fractionDialog.setContentView(R.layout.fraction_dialog);

                Button enterNumber = (Button) fractionDialog.findViewById(R.id.fraction_button);

                //Populates Spinner on the fraction dialog
                Spinner operationSpinner = (Spinner) fractionDialog.findViewById(R.id.is_sum_spinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                        R.array.operations_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                operationSpinner.setAdapter(adapter);


                /*Opens the fraction dialog tho choose a number and send send chosen values to
                TextViews on the item clicked */
                // TODO return the value on the picker to the array
                enterNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        NumberPicker numeratorPicker =
                                (com.shawnlin.numberpicker.NumberPicker) fractionDialog.findViewById(R.id.numerator_picker);
                        NumberPicker denominatorPicker =
                                (com.shawnlin.numberpicker.NumberPicker) fractionDialog.findViewById(R.id.denominator_picker);
                        Spinner isSumSpinner = (Spinner) fractionDialog.findViewById(R.id.is_sum_spinner);

                        changeValues(isSumSpinner.getSelectedItem().toString(),
                                numeratorPicker.getValue(), denominatorPicker.getValue(),
                                actualPosition, yearSentenceText, monthSentenceText,daySentenceText);


                        fractionDialog.dismiss();
                    }
                });

                fractionDialog.show();
            }
        });
    }

    public void setMainSentence(int year, int month, int day){
        sentence = new Sentence (year,month,day);
    }


    public void changeValues(String newIsSum, int newNumerator,
                             int newDenominator, int position,TextView yearSentenceText,
                             TextView monthSentenceText, TextView daySentenceText) {

        Operation operation = operations.get(position);
        operation.setNumerator(newNumerator);
        operation.setDenominator(newDenominator);
        operation.setIsSum(newIsSum);
        operation.setBaseSentence(sentence);

        adapter.notifyDataSetChanged();


        //TODO update de list

    }


    //Method that handles the dynamic insertion
    public void addItems(View v) {
        adapter.add(new Operation(0, 0, DEFAULT_IS_SUM_VALUE))
        ;
    }

    /*Method that opens the sentence dialog tho choose a number*/
    public void showSentenceDialog(final TextView yearSentenceText,
                                   final TextView monthSentenceText, final TextView daySentenceText) {
        final Dialog sentenceDialog = new Dialog(MainActivity.this);

        sentenceDialog.setTitle("Selecione a sentença");
        sentenceDialog.setContentView(R.layout.sentence_dialog);

        final NumberPicker yearSentence = (com.shawnlin.numberpicker.NumberPicker)
                sentenceDialog.findViewById(R.id.year_sentence);
        final NumberPicker monthSentence = (com.shawnlin.numberpicker.NumberPicker)
                sentenceDialog.findViewById(R.id.month_sentence);
        final NumberPicker daySentence = (com.shawnlin.numberpicker.NumberPicker)
                sentenceDialog.findViewById(R.id.day_sentence);

        yearSentence.setValue(sentence.getYear());
        monthSentence.setValue(sentence.getMonth());
        daySentence.setValue(sentence.getDay());

        Button enterNumber = (Button) sentenceDialog.findViewById(R.id.sentence_button);


        //Sends chosen values to TextViews on the item clicked
        enterNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                setMainSentence(yearSentence.getValue(),monthSentence.getValue(),daySentence.getValue());


                yearSentenceText.setText(String.valueOf(sentence.getYear()));
                monthSentenceText.setText(String.valueOf(sentence.getMonth()));
                daySentenceText.setText(String.valueOf(sentence.getDay()));

                sentenceDialog.dismiss();
            }
        });

        sentenceDialog.show();
    }


}