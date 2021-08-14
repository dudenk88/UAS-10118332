package com.app.BandungHoliday.Profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.app.BandungHoliday.Main.MainImplement;
import com.app.BandungHoliday.MyPlaces_Fragment.My_Places;
import com.app.BandungHoliday.R;

//13-10-2021 - 10118332 - Nais Farid - IF8
public class ProfileFragment extends Fragment {
    private CardView instagram,email;
    private ImageButton btn_back;
    private OnFragmentListener listener;
    private MainImplement main;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        btn_back=root.findViewById(R.id.btn_back);
        InitBackBtn();
        main= (MainImplement) getContext();
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        email = view.findViewById(R.id.cardEmail);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent e = new Intent(Intent.ACTION_VIEW);
                e.setData(Uri.parse("mailto:achmad.zhaki21@gmail.com"));
                e.setType("text/plain");
                startActivity(e);
            }
        });

        instagram = view.findViewById(R.id.cardInstagram);
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.instagram.com/faridnais8";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
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
        if (context instanceof ProfileFragment.OnFragmentListener) {
            listener = (ProfileFragment.OnFragmentListener) context;
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