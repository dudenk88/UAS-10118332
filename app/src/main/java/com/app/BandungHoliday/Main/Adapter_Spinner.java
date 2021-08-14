package com.app.BandungHoliday.Main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.BandungHoliday.R;
//12-10-2021 - 10118332 - Nais Farid - IF8
public class Adapter_Spinner extends BaseAdapter {
    private Integer[] range;
    private Context context;

    public Adapter_Spinner(Integer[] range, Context context) {
        this.range = range;
        this.context = context;
    }

    @Override
    public int getCount() {
        return range.length;
    }

    @Override
    public Integer getItem(int position) {
        return range[position];
    }

    @Override
    public long getItemId(int position) {
        return range[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        int distance= range[position];
        String unit="m";
        if (convertView==null)
            view=View.inflate(context, R.layout.item_spinner,null);
        if (distance>=1000){
            distance=distance/1000;
            unit="km";
        }
        ((TextView)view.findViewById(R.id.item)).setText(distance+" "+unit);
        return view;
    }
}
