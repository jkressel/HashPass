package eu.japk.hashpass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.util.List;
import eu.japk.hashpass.db.PasswordRecord;
import eu.japk.hashpass.db.RecordViewModel;

public class MainActivity extends AppCompatActivity {

    private RecordViewModel mRecordViewModel;
    RecordListAdapter adapter;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle t;
    Toolbar toolbar;

    RecyclerView recyclerView;
    int request_Code = 4;
    int FIRST_CODE = 3;
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getData("FirstRun")){
            Intent first = new Intent(MainActivity.this, Onboarding.class);
            startActivityForResult(first, FIRST_CODE);
        }

        mRecordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);

        recyclerView = findViewById(R.id.rec_view);
        adapter = new RecordListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab1 = findViewById(R.id.fabMain);
        fab1.setSize(FloatingActionButton.SIZE_AUTO);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNew.class);
                startActivity(intent);
            }
        });
        sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);

        mRecordViewModel.getAllRecords().observe(this, new Observer<List<PasswordRecord>>() {
            @Override
            public void onChanged(@Nullable final List<PasswordRecord> records) {
                // Update the cached copy of the words in the adapter.
                adapter.setRecords(records);
            }
        });

        //set up the navigation drawer
        initDrawer();


    addButton(fab1).show();

    }

    private BubbleShowCaseBuilder addButton(View view){
        return new BubbleShowCaseBuilder(this) //Activity instance
                .title("Add New Password") //Any title for the bubble view
                .description("Press this button to add a new password")
                .highlightMode(BubbleShowCase.HighlightMode.VIEW_SURFACE)
                .backgroundColorResourceId(R.color.colorPrimary)
                .showOnce("MainFabAdd")
                .targetView(view);
    }


    private void initDrawer() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimary));

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        t = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_app_bar_open_drawer_description, R.string.nav_app_bar_navigate_up_description);

        drawerLayout.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nv = (NavigationView)findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.new_phrase:
                        Intent i = new Intent(MainActivity.this, EnterPhrase.class);
                        i.putExtra("save", true);
                        startActivityForResult(i, request_Code);
                        break;
                    case R.id.import_export:
                        Intent intent = new Intent(MainActivity.this, ImportExport.class);
                        startActivity(intent);
                        break;
                    case R.id.about:
                        Intent intent1 = new Intent(MainActivity.this, About.class);
                        startActivity(intent1);
                        break;

                    default:
                        return true;
                }
                return true;

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        ImageView icon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        icon.setColorFilter(Color.WHITE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        return t.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FIRST_CODE) {
            if (resultCode == RESULT_OK) {
                storeData("FirstRun", false);
            }
            else{
                finish();
            }
        }
    }


    private void storeData(String key, boolean data){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, data);
        editor.apply();
    }

    private boolean getData(String key){
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key,true);
    }





}
