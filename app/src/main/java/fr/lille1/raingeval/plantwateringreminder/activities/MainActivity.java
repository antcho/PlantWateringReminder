package fr.lille1.raingeval.plantwateringreminder.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

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

                Intent intent = new Intent(getApplicationContext(), PlantFormActivity.class);
                intent.putExtra(ADD_MODE, true);
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

    }


    @Override
    protected void onRestart() {
        super.onRestart();
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

        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        updatePlants();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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
            startActivity(intent);
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
            final Calendar c = Calendar.getInstance();
            c.setTime(date);

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            calendar.getTimeInMillis();
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
