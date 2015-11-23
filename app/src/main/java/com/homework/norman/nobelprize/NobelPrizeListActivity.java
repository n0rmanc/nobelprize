package com.homework.norman.nobelprize;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class NobelPrizeListActivity extends AppCompatActivity {
    private final static String TAG = "NobelPrizeListActivity";

    ArrayList<JsonObject> m_nobel_prize_objects = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nobel_prize_list);

        Intent intent = getIntent();

        int years = intent.getIntExtra(YearSelectActivity.MESSAGE_SELECT_YEAR, 1);


        Log.d(TAG, "receive years " + years);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new NobelPrizeAdapter(this, years));
    }



}
