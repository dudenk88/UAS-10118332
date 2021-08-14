package com.app.BandungHoliday.MyPlaces_Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.app.BandungHoliday.Main.MainImplement;
import com.app.BandungHoliday.Main.MainPresenter;
import com.app.BandungHoliday.Model.Place;
import com.app.BandungHoliday.R;

import java.util.ArrayList;
//13-10-2021 - 10118332 - Nais Farid - IF8
public class My_Places extends Fragment {
    private ImageButton btn_back;
    private ListView list_my_places;
    //private  Context context;
    private OnFragmentListener listener;
    private MainImplement main;
    //property
    private Adapter_myPlaces adapter;
    private ArrayList<Place> placeSave;

    public My_Places(ArrayList<Place> placeSave ) {
        this.placeSave=placeSave;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_places, container, false);
        btn_back=view.findViewById(R.id.btn_back);
        list_my_places=view.findViewById(R.id.rv_list_store);
        InitBackBtn();
        InitListView();
        main= (MainImplement) getContext();
        return view;
    }

    private void InitBackBtn(){
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Log","Detach");
                listener.onCloseMyPlaces();
            }
        });
    }

    private void InitListView() {
        adapter= new Adapter_myPlaces(placeSave);
        list_my_places.setAdapter(adapter);
        list_my_places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPop(view,adapter.getItem(position));
            }
        });
    }

    private  void showPop(View view, final Place place){
        PopupMenu popup =new PopupMenu(getContext(),view, Gravity.END);
        popup.getMenuInflater().inflate(R.menu.menu_list_your,popup.getMenu()) ;
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.router: {
                        main.onNavigationWithGGMap(place);
                        Toast.makeText(getContext(), "Router " + place.getName(), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case  R.id.unsave: {
                        //Del Place
                        placeSave.remove(place);
                        //Del Id in MainPresenter
                        ArrayList<Integer> listIDSave = MainPresenter.listIDSave;
                        listIDSave.remove(listIDSave.indexOf(place.getId()));
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListener) {
            listener = (OnFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentListener {
            void  onCloseMyPlaces();
    }
}
