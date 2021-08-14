package com.app.BandungHoliday.Destinasi;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
//12-10-2021 - 10118332 - Nais Farid - IF8
public class PagerAdapter extends FragmentStateAdapter {
    public PagerAdapter(@NonNull  FragmentManager fragmentManager, @NonNull  Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:return new Desa1Fragment();

            case 1:
                return new Desa2Fragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
