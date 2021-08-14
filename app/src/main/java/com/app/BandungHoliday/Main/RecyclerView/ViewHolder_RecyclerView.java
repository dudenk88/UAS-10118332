package com.app.BandungHoliday.Main.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.BandungHoliday.Main.MainImplement;
import com.app.BandungHoliday.Main.MainPresenter;
import com.app.BandungHoliday.Model.Place;
import com.app.BandungHoliday.R;

import java.util.ArrayList;
//12-10-2021 - 10118332 - Nais Farid - IF8
public class ViewHolder_RecyclerView extends RecyclerView.ViewHolder {
    private View view;
    private TextView name;
    private TextView address;
    private Button btnDirection;
    private ToggleButton btnSave;
    private Place place;
    private MainImplement main;

    public ViewHolder_RecyclerView(@NonNull View itemView) {
        super(itemView);
        view=itemView;
        name=itemView.findViewById(R.id.txt_name);
        address=itemView.findViewById(R.id.txt_address);
        btnDirection=itemView.findViewById(R.id.btn_dire);
        btnSave=itemView.findViewById(R.id.btn_save);
        main= (MainImplement) itemView.getContext();
    }

    public void setData(Place obj, int pos){
        place=obj;
        name.setText(obj.getName());
        address.setText(obj.getAddress());
        btnSave.setChecked(MainPresenter.listIDSave.contains(obj.getId()));
        view.setTag(pos);
        btnSave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ArrayList<Integer> list=MainPresenter.listIDSave;
                int id= place.getId();
                if (isChecked)
                    if (!list.contains(id))
                        list.add(id);
                else
                    if (list.contains(id))
                        list.remove(list.indexOf(id));
            }
        });
        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.onNavigationWithGGMap(place);
            }
        });
    }
}
