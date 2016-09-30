package com.example.android.calculadorapenal;

/**
 * Created by victo on 29/09/2016.
 */

public class Sentence {


    private int year;
    private int month;
    private int day;

    /*Will hold values for operations*/


    private static final int DAYS_TO_MONTH = 30;
    private static final int DAYS_TO_YEAR = 365;


    public Sentence(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;

    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getDaysOfSentence() {
        return dateToDays(year, month, day);
    }

    private int dateToDays(int years, int months, int days) {
        days = days + months * 30 + years * 365;
        return days;
    }


    public Sentence newSentence(int numerator, int denominator, String isSum) {
        int sentenceDays;
        int newYear;
        int newMonth;

        Sentence resultSentence;
        sentenceDays = dateToDays(year, month, day);

            sentenceDays=(int)(((double)numerator/denominator)*sentenceDays);
            newYear = (int) ((double) sentenceDays / DAYS_TO_YEAR);
            sentenceDays = sentenceDays - newYear * DAYS_TO_YEAR;
            newMonth = (int) ((double) sentenceDays / DAYS_TO_MONTH);
            sentenceDays = sentenceDays - newMonth * DAYS_TO_MONTH;


        resultSentence = new Sentence(newYear, newMonth, sentenceDays);

        return resultSentence;
    }

    //TODO substituir por strings.xml
    private String writeDay() {
        if (getDay() == 0)
            return "";
        else if (getDay() == 1) {
            return getDay() + " dia";
        } else return getDay() + " dias";
    }

    private String writeMonth() {
        if (getMonth() == 0)
            return "";
        else if (getMonth() == 1) {
            return getMonth() + " mês";
        } else return getMonth() + " meses";
    }

    private String writeYear() {
        if (getYear() == 0)
            return "";
        else if (getYear() == 1) {
            return getYear()+ " ano";
        } else return getYear() + " anos";
    }

    public String writeSentence() {
        if(getDaysOfSentence()==0)
            return "Sem sentença";
        if (getDaysOfSentence() < 30)
            return writeDay();
        else if (getDaysOfSentence() < 365) {
            if (getDaysOfSentence() % 30 == 0)
                return writeMonth();
            else return writeMonth() + " e " + writeDay();
        } else if (getDaysOfSentence() % 365 == 0)
            return writeYear();
        else if (getMonth() == 0 && getDay() == 1)
            return writeYear() + " e " + writeDay();
        else if (getDay() == 0)
            return writeYear() + " e " + writeMonth();
        else return writeYear() + " e " + writeMonth() + " e " + writeDay();
    }
}

