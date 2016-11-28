package com.release.android.calculadorapenal;

import org.jetbrains.annotations.Contract;

/**
 * Created by victo on 29/09/2016.
 */

public class Sentence {


    private int year;
    private int month;
    private int day;
    private int daysFine;

    /*Will hold values for operations*/


    private static final int DAYS_TO_MONTH = 30;
    private static final int DAYS_TO_YEAR = 365;


    public Sentence(int year, int month, int day, int daysFine) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.daysFine = daysFine;

    }

    //@input days of sentence and they are transformed to a formal sentence
    public Sentence(int days) {
        int sentence = days;
        if (days >= DAYS_TO_YEAR) {
            year = (int) ((double) days / DAYS_TO_YEAR);
            sentence = sentence - (year * DAYS_TO_YEAR);
        }
        if (sentence >= DAYS_TO_MONTH) {
            month = (int) ((double) sentence / DAYS_TO_MONTH);
            sentence = sentence - (month * DAYS_TO_MONTH);
        }
        if (sentence > 0) {
            day = sentence;
        }
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

    public int getdaysFine() {
        return daysFine;
    }

    public int getDaysOfSentence() {
        return dateToDays(year, month, day);
    }

    public void setDaysFine(int daysFine){ this.daysFine=daysFine;}

    @Contract(pure = true)
    private int dateToDays(int years, int months, int days) {
        days = days + months * 30 + years * 365;
        return days;
    }


    public Sentence newSentence(int numerator, int denominator, String isSum) {
        int sentenceDays;
        int newYear;
        int newMonth;
        int newDaysFine = (int) (((double) numerator / denominator)*daysFine) ;

        Sentence resultSentence;
        sentenceDays = dateToDays(year, month, day);

        sentenceDays = (int) (((double) numerator / denominator) * sentenceDays);
        newYear = (int) ((double) sentenceDays / DAYS_TO_YEAR);
        sentenceDays = sentenceDays - newYear * DAYS_TO_YEAR;
        newMonth = (int) ((double) sentenceDays / DAYS_TO_MONTH);
        sentenceDays = sentenceDays - newMonth * DAYS_TO_MONTH;



        resultSentence = new Sentence(newYear, newMonth, sentenceDays, newDaysFine);

        return resultSentence;
    }

    //TODO substituir por strings.xml

    //Write daysFine
    public String writeDaysFine(){

        if(daysFine==0)
            return "0 dias multa";
        else if(daysFine==1)
            return "1 dia multa";
        else
            return daysFine + " dias multa";

    }

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
            return getYear() + " ano";
        } else return getYear() + " anos";
    }

    public String writeSentence() {
        if(daysFine==0){
        if (getDaysOfSentence() == 0)
            return "";
        if (getDaysOfSentence() < 30)
            return writeDay();
        else if (getDaysOfSentence() < 365) {
            if (getDaysOfSentence() % 30 == 0)
                return writeMonth();
            else return writeMonth() + " e " + writeDay();
        } else if (getDaysOfSentence() % 365 == 0)
            return writeYear();
        else if (getMonth() == 0 && getDay() != 0)
            return writeYear() + " e " + writeDay();
        else if (getDay() == 0)
            return writeYear() + " e " + writeMonth();
        else return writeYear() + ", " + writeMonth() + " e " + writeDay();
    }
        else{
            if (getDaysOfSentence() == 0)
                return writeDaysFine();
            if (getDaysOfSentence() < 30)
                return writeDay()+ " e " + writeDaysFine();
            else if (getDaysOfSentence() < 365) {
                if (getDaysOfSentence() % 30 == 0)
                    return writeMonth() + " e " + writeDaysFine();
                else return writeMonth() + ", " + writeDay() + " e " + writeDaysFine();
            } else if (getDaysOfSentence() % 365 == 0)
                return writeYear() + " e " + writeDaysFine();
            else if (getMonth() == 0 && getDay() != 0)
                return writeYear() + ", " + writeDay() + " e " + writeDaysFine();
            else if (getDay() == 0)
                return writeYear() + ", " + writeMonth()+ " e " + writeDaysFine();
            else return writeYear() + ", " + writeMonth() + ", " + writeDay() + " e " + writeDaysFine();
        }

        }
}

