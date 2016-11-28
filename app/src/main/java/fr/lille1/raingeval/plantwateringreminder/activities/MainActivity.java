package fr.lille1.raingeval.plantwateringreminder.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.lille1.raingeval.plantwateringreminder.App;
import fr.lille1.raingeval.plantwateringreminder.R;
import fr.lille1.raingeval.plantwateringreminder.adapters.PlantsAdapter;
import fr.lille1.raingeval.plantwateringreminder.entities.DaoSession;
import fr.lille1.raingeval.plantwateringreminder.entities.Plant;
import fr.lille1.raingeval.plantwateringreminder.entities.PlantDao;

public class MainActivity extends AppCompatActivity {

    public final static String PLANT_ID = "plantId";
    public final static String ADD_MODE = "addMode";
    public final static String VIEW_MODE = "viewMode";


    private FloatingActionButton fab;
    private PlantsAdapter plantsAdapter;
    private PlantDao plantDao;
    private Menu menu;

    public static long currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(getApplicationContext(), PlantFormActivity.class);
                intent.putExtra(ADD_MODE, true);
                //intent.putExtra(CURRENT_TIME, currentTime);
                startActivity(intent);
            }
        });

        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        plantDao = daoSession.getPlantDao();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewPlants);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentTime = new Date().getTime();

        plantsAdapter = new PlantsAdapter(plantClickListener, plantLongClickListener);

        recyclerView.setAdapter(plantsAdapter);

        //updatePlants();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println(MainActivity.currentTime);
        updatePlants();
    }


    private void updatePlants() {
        List<Plant> plants = plantDao.loadAll();
        plantsAdapter.setPlants(plants);
        MenuItem fixturesItem = menu.findItem(R.id.action_fixtures);
        if(plants.isEmpty()) {
            fixturesItem.setVisible(true);
            return;
        }
        fixturesItem.setVisible(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        updatePlants();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fixtures) {
            fillDatabaseFixtures();
            return true;
        }
        if (id == R.id.action_setdate) {
            showTimePickerDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    PlantsAdapter.PlantClickListener plantClickListener = new PlantsAdapter.PlantClickListener() {
        @Override
        public void onPlantClick(int position) {
            Plant plant = plantsAdapter.getPlant(position);
            Long plantId = plant.getId();

            Intent intent = new Intent(getApplicationContext(), PlantFormActivity.class);
            intent.putExtra(PLANT_ID, plantId);
            intent.putExtra(VIEW_MODE, true);
            //intent.putExtra(CURRENT_TIME, currentTime);
            startActivity(intent);
            System.out.println(plant.getLastWateringDate());
            updatePlants();
        }
    };

    PlantsAdapter.PlantLongClickListener plantLongClickListener = new PlantsAdapter.PlantLongClickListener() {
        @Override
        public void onPlantLongClick(int position) {
            Plant plant = plantsAdapter.getPlant(position);
            plant.setLastWateringDate(currentTime);
            plantDao.update(plant);
            updatePlants();
        }
    };

    private void fillDatabaseFixtures() {
        for (int i=0; i<10; i++) {
            Date date = new Date();
            //long dateValue = date.getTime();
            Plant plant = new Plant(null, "Plant" + i, 21-i , 0, currentTime);
            plantDao.insert(plant);
        }
        updatePlants();

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Date date = new Date(currentTime);
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            c.setTime(date);

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of TimePickerDialog and return it*


            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            calendar.getTimeInMillis();
            //System.out.println("baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa : " + calendar.getTimeInMillis());
            currentTime = calendar.getTimeInMillis();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            MainActivity activity = (MainActivity) getActivity();
            activity.updatePlants();
        }

    }

    public void showTimePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        FragmentManager fragmentManager = getFragmentManager();
        newFragment.show(fragmentManager, "datePicker");
    }

}
