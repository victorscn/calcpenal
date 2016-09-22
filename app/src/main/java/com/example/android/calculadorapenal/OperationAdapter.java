package com.example.android.calculadorapenal;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by victo on 22/09/2016.
 */

public class OperationAdapter extends ArrayAdapter<Operation>{

    public OperationAdapter(Activity context, ArrayList<Operation> operations) {

        /*Initializing the ArrayAdapter with context and list
        * the second is left with no value, because is used when
        * the ArrayAdapter is populating a single TextView*/

        super(context, 0);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        /*Checks if the item is null and creates a new item if that's the case*/
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.expanded_item, parent, false);
            }

        Operation currentOperation = getItem(position);

        return super.getView(position, convertView, parent);
    }
}
