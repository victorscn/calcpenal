package com.release.android.calculadorapenal;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by victo on 22/09/2016.
 */

public class OperationAdapter extends ArrayAdapter<Operation> {

    private static final int MAXVALUE = 9;
    private static final int MINVALUE = 1;


    public OperationAdapter(Activity context, ArrayList<Operation> operations) {

        /*Initializing the ArrayAdapter with context and list
        * the second is left with no value, because is used when
        * the ArrayAdapter is populating a single TextView*/

        super(context, 0, operations);
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        /*Checks if the item is null and creates a new item if that's the case*/
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.expanded_item, parent, false);
        }

        final Operation currentOperation = getItem(position);


        /*Find the TextView numerator in expanded_item and populates*/
        TextView numeratorText = (TextView) listItemView.findViewById(R.id.numerator);
        numeratorText.setText(String.valueOf(currentOperation.getNumerator()));

        /*Find the TextView denominator in expanded_item and populates*/
        TextView denominatorText = (TextView) listItemView.findViewById(R.id.denominator);
        denominatorText.setText(String.valueOf(currentOperation.getDenominator()));

        /*Fint the TextView for the sentence text and populates*/
        TextView sentenceText = (TextView) listItemView.findViewById(R.id.sentence);
        sentenceText.setText(currentOperation.writeResult());

        /*Find the TextView isSum in expanded_item and populates*/
        TextView isSumText = (TextView) listItemView.findViewById(R.id.isSum);
        isSumText.setText(currentOperation.getIsSum());

        /*Find the TextView description and populates*/
        TextView descriptionText = (TextView) listItemView.findViewById(R.id.description);
        descriptionText.setText(currentOperation.getDescription());


        return listItemView;
    }


}
