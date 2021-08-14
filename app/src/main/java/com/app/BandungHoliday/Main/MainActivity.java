package com.app.BandungHoliday.Main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.app.BandungHoliday.Destinasi.Desa1Fragment;
import com.app.BandungHoliday.Main.RecyclerView.Adapter_RecyclerView;
import com.app.BandungHoliday.Map.Map;
import com.app.BandungHoliday.Model.Place;
import com.app.BandungHoliday.MyPlaces_Fragment.My_Places;
import com.app.BandungHoliday.Profile.ProfileFragment;
import com.app.BandungHoliday.R;
import com.app.BandungHoliday.Search_Fragment.Search;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
//13-10-2021 - 10118332 - Nais Farid - IF8
public class MainActivity extends AppCompatActivity implements
        My_Places.OnFragmentListener,
        ProfileFragment.OnFragmentListener,
        Desa1Fragment.OnFragmentListener,
        NavigationView.OnNavigationItemSelectedListener,
        Map.onMapListener,
        Search.SearchListener ,
        MainPresenter.MainPresenterListener,
        MainImplement{
    //Fragment
    private Map map;
    private MainPresenter presenter;
    private My_Places my_places;
    private Search search;
    //region Attr View
        //Attr Toolbar
    private TextView txt_location;
    private ImageButton btn_clear;
    private Toolbar toolbar;
        //Attr Drawer Left
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
        //Attr BottomSheet
    private FloatingActionButton fab;
    private Button btnNearby;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private TextView txtCountPlace;
    private LinearLayout notfound;
    private LinearLayoutManager layoutManager;
    private Button btnShowList;
    private BottomSheetBehavior behavior;
    private ConstraintLayout bottom_sheet;
    private ProfileFragment profileFragment;
    private Desa1Fragment desa1Fragment;
    //endregion

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("TAG","On STOP APPLICATION");
        presenter.saveData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter= new MainPresenter(this);
        presenter.readData(getApplicationContext());
        //Init Map
        map= new Map(this);
        getSupportFragmentManager().beginTransaction().add(R.id.content_map,map).commit();
        //Init Components
        initToolbar();
        initNavigationDrawer();
        initBottomSheet();
        initSpinner();
    }

    private void  initToolbar(){
        //binding View
        toolbar=findViewById(R.id.toolbar);
        txt_location=findViewById(R.id.txt_location);
        btn_clear=findViewById(R.id.btn_clear);
        //Event
        setSupportActionBar(toolbar);
        btn_clear.setVisibility(View.INVISIBLE);
        txt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search==null||!search.isVisible())
                    search= new Search();
                addFragment(search);
                toolbar.setVisibility(View.INVISIBLE);
            }
        });
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_location.getText()!="") {
                    txt_location.setText("");
                    btn_clear.setVisibility(View.INVISIBLE);
                    fab.callOnClick();
                }
            }
        });
    }

    private void initNavigationDrawer() {
        //binding View
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        //event
        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initBottomSheet(){
        //binding View
        bottom_sheet= findViewById(R.id.bottom);
        behavior=BottomSheetBehavior.from(bottom_sheet);
        fab =findViewById(R.id.fab);
        btnNearby=findViewById(R.id.btn_nearby);
        recyclerView=findViewById(R.id.recycler_view);
        txtCountPlace=findViewById(R.id.txt_count);
        notfound=findViewById(R.id.not_found);
        btnShowList=findViewById(R.id.btn_showlist);
        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        //event
            //FAB My location
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.showLocationCurr();
                displayListNearby(new ArrayList<Place>());
                spinner.setSelection(0);
                if (behavior.getState()!=BottomSheetBehavior.STATE_COLLAPSED)
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });
            //Button Show List
        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        });
        btnShowList.setVisibility(View.INVISIBLE);
            //Button Nearby
        btnNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                ArrayList<Place> listPlace= presenter.getListPlaceWithDistance();
                map.showNearby(presenter.getDistance(),listPlace);
                if (listPlace.size()>0)
                    btnShowList.setVisibility(View.VISIBLE);
            }
        });
            //BottomSheet
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState){
                        case BottomSheetBehavior.STATE_COLLAPSED: {
                            btnShowList.setVisibility(View.VISIBLE);
                            break;
                        }
                        case  BottomSheetBehavior.STATE_EXPANDED:{

                            displayListNearby(presenter.getListPlaceWithDistance());
                            btnShowList.setVisibility(View.INVISIBLE);
                            break;
                        }
                        default:
                            btnShowList.setVisibility(View.INVISIBLE);
                            break;
                    }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { }
        });
    }

    private void initSpinner(){
        //binding View
        spinner=findViewById(R.id.spinner_distance);
        //event
        Adapter_Spinner adapter_spinner= new Adapter_Spinner(MainPresenter.Range,this);
        spinner.setAdapter(adapter_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.changeDistance(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void  displayListNearby(ArrayList<Place> places){
        Log.e("TAG","Show list in RecyclerView - "+places.size());
        if (places.size()>0){
            txtCountPlace.setText("Có "+places.size()+" cửa gần bạn.");
            notfound.setVisibility(View.INVISIBLE);
        }
        else {
            notfound.setVisibility(View.VISIBLE);
            txtCountPlace.setText("");
        }
        //reset RecyclerView
        Adapter_RecyclerView adapter= new Adapter_RecyclerView(places,this);
        recyclerView.setOnFlingListener(null);
        recyclerView.clearOnChildAttachStateChangeListeners();
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.swapAdapter(adapter,true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                Log.e("TAG","Attach : " + view.getTag());
                map.onChangeMarker((Integer) view.getTag());
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) { }
        });
    }

    @Override
    public void onMakerClicked(int pos) {
            recyclerView.scrollToPosition(pos);
    }

    //region Init DrawerLeft
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_saved: {
                drawer.closeDrawers();
                if (my_places == null || !my_places.isVisible())
                    my_places = new My_Places(presenter.getDataListPlaceSave());
                addFragment(my_places);
                return true;
            }
            case R.id.nav_clear_history:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirm");
                builder.setMessage("Search history  will delete, you sure ?");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainPresenter.listHistory.clear();
                        drawer.closeDrawers();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
                return true;
            }
            case R.id.nav_about:{
                drawer.closeDrawers();
                if (profileFragment==null||!profileFragment.isVisible())
                    profileFragment= new ProfileFragment();
                addFragment(profileFragment);
                return true;

            }
            case R.id.nav_destination: {
                drawer.closeDrawers();
                if (desa1Fragment==null||!desa1Fragment.isVisible())
                    desa1Fragment= new Desa1Fragment();
                addFragment(desa1Fragment);
                return true;

            }

            case R.id.nav_exit:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirm");
                builder.setMessage("You will exit application, you sure ?");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { exitApplication(); }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
                Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return true;
    }

    //endregion

    //region  Add, Remove Fragment
    private void  addFragment(Fragment fragment){
        btnShowList.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_up,0,0,R.anim.anim_down);
        transaction.add(R.id.content_fragment,fragment).addToBackStack("").commit();
    }

    public  void closeFragment(){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        fab.setVisibility(View.VISIBLE);
        onBackPressed();
    }

    @Override
    public void onCloseMyPlaces() { closeFragment(); }

    @Override
    public void onCloseSearch(Location location, String title, String address) {
        if (location!=null){
            map.changeLocation(location,title);
            displayListNearby(new ArrayList<Place>());
            txt_location.setText(address);
            btn_clear.setVisibility(View.VISIBLE);
        }
        toolbar.setVisibility(View.VISIBLE);
        closeFragment();
    }
    //endregion

    //Result check Turn On Location
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            switch (resultCode){
                case -1:{
                    Log.e("TAB","User Enable");
                    map.showLocationCurr();
                    break;
                }
                case 0: {
                    Log.e("TAB", "User Disable");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Important");
                    builder.setMessage("Application need Location turn on to continue.");
                    builder.setNegativeButton("No and Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { exitApplication();}
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { map.showLocationCurr(); }
                    });
                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();
                    break;
                }
            }
        }
    }

    @Override
    public void onDataFail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Important");
        builder.setMessage("Load data fail !!!");
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { exitApplication();}
        });
        builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { presenter.readData(getApplicationContext());}
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    @Override
    public void onNavigationWithGGMap(Place place) {
        String uri= "google.navigation:q=" +place.getLatitude()+","+place.getLongitude();
        Uri gmmIntentUri = Uri.parse(uri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private  void  exitApplication(){ this.finish(); }

}
