package com.example.android.calculadorapenal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_about);
        setSupportActionBar(myToolbar);
    }

    public void startMA (View view){
        startActivity(new Intent(this, MainActivity.class));
    }

    public void showBuy(View view){

    }
}
