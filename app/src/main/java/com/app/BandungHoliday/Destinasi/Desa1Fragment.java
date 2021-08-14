package com.app.BandungHoliday.Destinasi;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.app.BandungHoliday.Main.MainImplement;
import com.app.BandungHoliday.R;

//12-10-2021 - 10118332 - Nais Farid - IF8
public class Desa1Fragment extends Fragment {
    private ImageButton btn_back;
    private OnFragmentListener listener;
    private MainImplement main;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_destinasi, container, false);
        btn_back=root.findViewById(R.id.btn_back);
        InitBackBtn();
        main= (MainImplement) getContext();
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Desa1Fragment.OnFragmentListener) {
            listener = (Desa1Fragment.OnFragmentListener) context;
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