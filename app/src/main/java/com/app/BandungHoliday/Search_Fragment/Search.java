package com.app.BandungHoliday.Search_Fragment;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.app.BandungHoliday.Main.MainPresenter;
import com.app.BandungHoliday.Model.myModal;
import com.app.BandungHoliday.R;

import java.util.ArrayList;
//13-10-2021 - 10118332 - Nais Farid - IF8
public class Search extends Fragment {

    //View
    private SearchListener listener;
    private ImageButton bnt_back;
    private SearchView searchView;
    private ListView listView;
    private TextView txt_result;
    private TextView txt_header;
    //Data
        //History
    private ArrayList<String> listHistory= MainPresenter.listHistory;
    private ArrayAdapter<String> adapter;
    private ArrayList<Object> arrayResult = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener= (SearchListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        searchView=view.findViewById(R.id.search_view);
        bnt_back=view.findViewById(R.id.btn_back);
        listView=view.findViewById(R.id.listview_history);
        txt_result=view.findViewById(R.id.txt_search_result);
        txt_header=view.findViewById(R.id.txt_header_search);
        txt_result.setVisibility(View.INVISIBLE);
        initBtnBack();
        initListView();
        initSearchView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        searchView.setQuery("",false);
    }

    private void  initSearchView(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txt_header.setText("Result");
                txt_result.setVisibility(View.VISIBLE);
                 arrayResult =myModal.getLocationFrom(query,getContext());
                if (arrayResult!=null){
                    txt_result.setText(arrayResult.get(1).toString());
                    txt_result.setClickable(true);
                    txt_result.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String re= txt_result.getText().toString();
                            listHistory.add(re);
                            adapter.notifyDataSetChanged();
                            listener.onCloseSearch((Location) arrayResult.get(0),arrayResult.get(2).toString(),arrayResult.get(1).toString());
                        }
                    });
                }
                else { txt_result.setText("Not found !"); }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                txt_header.setText("History");
                txt_result.setVisibility(View.INVISIBLE);
                return false;
            }
        });
    }

    private void  initBtnBack(){
        bnt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               searchView.clearFocus();
                listener.onCloseSearch(null,null,null);
            }
        });
    }

    private void  initListView(){
        adapter =new ArrayAdapter<>(getContext(),R.layout.item_list_history,listHistory);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(listHistory.get(position),false);
                arrayResult=myModal.getLocationFrom(listHistory.get(position),getContext());
                listener.onCloseSearch((Location) arrayResult.get(0),arrayResult.get(2).toString(),arrayResult.get(1).toString());
            }
        });
    }

    public interface SearchListener{
        void onCloseSearch(Location location, String name, String address );
    }

}
