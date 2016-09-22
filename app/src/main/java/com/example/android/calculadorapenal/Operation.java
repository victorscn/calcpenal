package com.example.android.calculadorapenal;

/**
 * Created by victor on 22/09/2016.
 */

public class Operation {

//  Will hold the numerator for the operation
    private int numerator;

//  Will hold the denominator for the operation
    private int denominator;

//  Will hold the value for the sum or subtraction
    private boolean isSum;

    /*Constructs a new Operation with values set by the user*/

    public Operation(int numerator, int denominator, boolean isSum){
        this.numerator = numerator;
        this.denominator = denominator;
        this.isSum = isSum;
    }

    /*@return the numerator value*/
    public int getNumerator(){
        return numerator;
    }

    /*@return the denominator value*/
    public int getDenominator(){
        return denominator;
    }

    public boolean getIsSum(){
        return isSum;
    }
}
