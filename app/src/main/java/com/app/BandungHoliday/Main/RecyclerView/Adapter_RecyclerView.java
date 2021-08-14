package com.app.BandungHoliday.Main.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.BandungHoliday.Model.Place;
import com.app.BandungHoliday.R;

import java.util.ArrayList;
//12-10-2021 - 10118332 - Nais Farid - IF8
public class Adapter_RecyclerView  extends RecyclerView.Adapter<ViewHolder_RecyclerView>  {
    private ArrayList<Place> listNearby;
    private Context context;

    public Adapter_RecyclerView(ArrayList<Place> listNearby, Context context) {
        this.listNearby=listNearby;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder_RecyclerView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_recyclerview,parent,false);
        return new ViewHolder_RecyclerView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_RecyclerView holder, int position) {
        Place place= listNearby.get(position);
        holder.setData(place,position);
    }

    @Override
    public int getItemCount() {
        return listNearby.size();
    }
}
