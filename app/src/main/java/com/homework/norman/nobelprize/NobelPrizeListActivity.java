package com.homework.norman.nobelprize;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class NobelPrizeListActivity extends AppCompatActivity {
    private final static String TAG = "NobelPrizeListActivity";

    ArrayList<JsonObject> m_nobel_prize_objects = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nobel_prize_list);
        Ion.with(getApplicationContext())
                .load("http://api.nobelprize.org/v1/prize.json?year=2015")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            LinearLayout price_item = (LinearLayout) findViewById(R.id.prize_horizon_layout);
                            Log.d(TAG, result.toString());
                            JsonArray prizes = result.getAsJsonArray("prizes");
                            int year = prizes.get(0).getAsJsonObject().get("year").getAsInt();
                            Log.d(TAG, "year " + year);
                            for (JsonElement element : prizes) {
                                //get category
                                StringBuilder html_string = new StringBuilder();

                                String category = element.getAsJsonObject().get("category").getAsString();
                                Log.d(TAG, "CAtegory " + category);
                                html_string.append("<div>");
                                html_string.append(category);
                                html_string.append("</div>");
//                                html_string.append("<ul>");
                                JsonArray laureates = (JsonArray) element.getAsJsonObject().get("laureates");
                                for (JsonElement laureate : laureates) {
                                    JsonObject laureate_object = (JsonObject) laureate;
                                    String firstname = "";
                                    if (laureate_object.get("firstname") != null){
                                        firstname = ((JsonObject) laureate).get("firstname").getAsString();
                                    }

                                    String surname = "";
                                    if(laureate_object.get("surname") != null){
                                        surname = ((JsonObject) laureate).get("surname").getAsString();
                                    }
                                    html_string.append("&#8226;");
                                    html_string.append(firstname);
                                    html_string.append(" ");
                                    html_string.append(surname);
                                    html_string.append("<br/>");
                                    Log.d(TAG, "firstname " + firstname);
                                    Log.d(TAG, "surname " + surname);
                                }
//                                html_string.append("</ul>]]>");
                                TextView prize_text_view = new TextView(getApplicationContext());
                                prize_text_view.setTextColor(Color.DKGRAY);
                                prize_text_view.setPadding(10,10,10,10);
                                prize_text_view.setText(Html.fromHtml(html_string.toString()));
                                price_item.addView(prize_text_view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            }
                        }

                        HorizontalScrollView scroll_view = (HorizontalScrollView) findViewById(R.id.scroll_view);
                    }
                });
    }



}
