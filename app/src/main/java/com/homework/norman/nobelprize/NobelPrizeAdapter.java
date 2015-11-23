package com.homework.norman.nobelprize;

import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by norman on 2015/11/23.
 */
public class NobelPrizeAdapter extends ArrayAdapter<NobelPrizeYearInfo> {
    private final String TAG = "NobelPrizeAdapter";
    private final Activity mContext;

    private ArrayList<NobelPrizeYearInfo> mNobelPrizeYearInfos;

    private final String mUrl = "http://api.nobelprize.org/v1/prize.json?year=";

    private int mMaxCategoryCount = 1;

    int mCountPrizeInfos = 0;

    public NobelPrizeAdapter(Activity context, int years) {
        super(context, R.layout.nobel_prize_list_item);
        mNobelPrizeYearInfos = new ArrayList<NobelPrizeYearInfo>();
        mContext = context;
        getNobelPrizeYearInfos(years);
    }

    private void getNobelPrizeYearInfos(final int yearCount){
        Calendar calendar = Calendar.getInstance();
        int current_year = calendar.get(Calendar.YEAR);

        for(int year = current_year - yearCount + 1 ; year <= current_year; year++){
            Ion.with(mContext)
                    .load(mUrl + year)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {

                                NobelPrizeYearInfo nobelPrizeYearInfo = new NobelPrizeYearInfo();
                                JsonArray prizes = result.getAsJsonArray("prizes");
                                int year = prizes.get(0).getAsJsonObject().get("year").getAsInt();
                                //Log.d(TAG, "year " + year);

                                StringBuilder name = new StringBuilder();

                                nobelPrizeYearInfo.year = year;
                                for (JsonElement element : prizes) {
                                    //get category
                                    NobelPrizeInfo nobelPrizeInfo = new NobelPrizeInfo();

                                    String category = element.getAsJsonObject().get("category").getAsString();
                                    //Log.d(TAG, "CAtegory " + category);
                                    nobelPrizeInfo.category = category;
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
                                        if(firstname != null && !TextUtils.isEmpty(firstname)){
                                            name.append(firstname);
                                        }
                                        if(surname != null && !TextUtils.isEmpty(surname)){
                                            name.append(" ");
                                            name.append(surname);
                                        }
                                        //Log.d(TAG, "name " + name);
                                        nobelPrizeInfo.award_winers.add(name.toString());
                                        name.setLength(0);
                                    }
                                    nobelPrizeYearInfo.prize_info.add(nobelPrizeInfo);
                                    laureates = null;
                                }
                                if(mMaxCategoryCount < nobelPrizeYearInfo.prize_info.size()){
                                    mMaxCategoryCount = nobelPrizeYearInfo.prize_info.size();
                                }
                                mNobelPrizeYearInfos.add(nobelPrizeYearInfo);

                                if(++mCountPrizeInfos == yearCount){
                                    //Log.d(TAG, "notifyDataSetChanged Count " + mNobelPrizeYearInfos.size());
                                    // Sort year informations
                                    Collections.sort(mNobelPrizeYearInfos, new Comparator<NobelPrizeYearInfo>() {
                                        @Override
                                        public int compare(NobelPrizeYearInfo lhs, NobelPrizeYearInfo rhs) {
                                            return lhs.year > rhs.year ? -1 : 1;
                                        }
                                    });
                                    // Notify get data done;
                                    notifyDataSetChanged();
                                }
                                name = null;
                                prizes = null;
                            }
                        }
                    });



        }
    }

    private NobelPrizeInfo getNobelPrizeInfo(int year){
        NobelPrizeInfo result = null;
        return result;
    }

    @Override
    public NobelPrizeYearInfo getItem(int position) {
        return mNobelPrizeYearInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mNobelPrizeYearInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mNobelPrizeYearInfos.get(position).prize_info.size();
    }

    @Override
    public int getViewTypeCount() {
        return mMaxCategoryCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        NobelPrizeYearInfo nobelPrizeYearInfo = getItem(position);
        if(view == null){
            //Log.d(TAG, "View null ");
            LayoutInflater inflater = mContext.getLayoutInflater();
            view = inflater.inflate(R.layout.nobel_prize_list_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.yearTextView = (TextView) view.findViewById(R.id.year);

            LinearLayout horiLayout = (LinearLayout) view.findViewById(R.id.prize_horizon_layout);
            viewHolder.prizeHorizLayout = horiLayout;
            for(int categoryCount = 0 ;categoryCount < nobelPrizeYearInfo.prize_info.size(); categoryCount ++ ){
                LinearLayout prizeTextView = (LinearLayout) inflater.inflate(R.layout.nobel_prize_info_textview, horiLayout);
            }
            view.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.yearTextView.setText(String.valueOf(nobelPrizeYearInfo.year));
        StringBuffer htmlString = new StringBuffer();
        for(int category = 0; category < nobelPrizeYearInfo.prize_info.size(); category++){
            NobelPrizeInfo nobelPrizeInfo = nobelPrizeYearInfo.prize_info.get(category);
            htmlString.append("<div>");
            htmlString.append(nobelPrizeInfo.category);
            htmlString.append("</div>");
            for(String awardWiner: nobelPrizeInfo.award_winers){
                //Log.d(TAG, "award_winers " + awardWiner);
                htmlString.append("&#8226;  ");
                htmlString.append(awardWiner);
                htmlString.append("<br/>");
            }
            ((TextView)viewHolder.prizeHorizLayout.getChildAt(category)).setText(Html.fromHtml(htmlString.toString()));
            htmlString.setLength(0);
        }


        return view;
    }

    static class ViewHolder {
        public TextView yearTextView;
        public LinearLayout prizeHorizLayout;
    }



}
