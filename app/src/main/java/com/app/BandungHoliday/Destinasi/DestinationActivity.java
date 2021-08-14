package com.app.BandungHoliday.Destinasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.app.BandungHoliday.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
//12-10-2021 - 10118332 - Nais Farid - IF8
public class DestinationActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        initView();
        setAdapter();
    }
    public void setData(String name){
        this.name=name;
    }
    public String getData(){
        return name;
    }

    private void setAdapter() {
        PagerAdapter pagerAdapter=new PagerAdapter(getSupportFragmentManager(),getLifecycle());
        viewPager2.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText("Fragment"+position);

            }
        }).attach();
    }

    private void initView() {
        tabLayout=findViewById(R.id.tabLayout);
        viewPager2=findViewById(R.id.viewPager);
    }
}