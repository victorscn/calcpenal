package com.release.android.calculadorapenal;

/**
 * Created by victor on 22/09/2016.
 */

public class Operation {

    //  Will hold the numerator for the operation
    private int numerator;

    //  Will hold the denominator for the operation
    private int denominator;

    //  Will hold the value for the sum or subtraction
    private String isSum;

    // Will hold the description message
    private String description;

    //  Will hold the result sentence
    private Sentence sentence;

    /*Constructs a new Operation with values set by the user*/

    public Operation(int numerator, int denominator, String isSum) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.isSum = isSum;
    }

    public Operation(int numerator, int denominator, String isSum, String description) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.isSum = isSum;
        this.description = description;
    }

    public Operation(int numerator, int denominator, String isSum, Sentence sentence) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.isSum = isSum;
        this.sentence = sentence.newSentence(numerator, denominator, isSum);
    }

    //Change the numerator
    public void setNumerator(int newNumerator) {
        numerator = newNumerator;
    }

    //Change the description
    public void setDescription(String newDescription) {
        description = newDescription;
    }

    //Change the denominator
    public void setDenominator(int newDenominator) {
        denominator = newDenominator;
    }

    public void setIsSum(String newIsSum) {
        isSum = newIsSum;
    }

    /*@return the numerator value*/
    public int getNumerator() {
        return numerator;
    }

    /*@return the denominator value*/
    public int getDenominator() {
        return denominator;
    }

    /*@return the description message*/
    public String getDescription() {
        if (description != null)
            return description;
        else return null;
    }

    /*@return the if the signal is positive of negative*/
    public String getIsSum() {
        return isSum;
    }

    //Change base sentence
    public void setBaseSentence(Sentence sentence) {
        this.sentence = sentence.newSentence(numerator, denominator, isSum);
    }

    public int getYear() {
        if (sentence != null)
            return sentence.getYear();
        return 0;
    }

    public int getMonth() {
        if (sentence != null)
            return sentence.getMonth();
        return 0;
    }

    public int getDay() {
        if (sentence != null)
            return sentence.getYear();
        return 0;
    }

    public int getDaysOfSentence() {
        if (sentence != null)
            return sentence.getDaysOfSentence();
        return 0;
    }

    public String writeResult() {
        String message;
        if (sentence != null){
            message = sentence.writeSentence();
            if(message.length()>25)
                return sentence.getYear() +"A " + sentence.getMonth() +"M "
                        + sentence.getDay()+ "D "+ sentence.getdaysFine()+"DM ";
            else return message;
            }
        return "";
    }

}
