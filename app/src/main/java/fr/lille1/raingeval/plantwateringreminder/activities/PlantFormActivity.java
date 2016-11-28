package fr.lille1.raingeval.plantwateringreminder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fr.lille1.raingeval.plantwateringreminder.App;
import fr.lille1.raingeval.plantwateringreminder.R;
import fr.lille1.raingeval.plantwateringreminder.entities.DaoSession;
import fr.lille1.raingeval.plantwateringreminder.entities.Plant;
import fr.lille1.raingeval.plantwateringreminder.entities.PlantDao;

public class PlantFormActivity extends AppCompatActivity {

    private PlantDao plantDao;
    private Plant plant;

    private EditText nameEdit;
    private EditText frequencyEdit;

    private TextView nameView;
    private TextView frequencyView;
    private TextView daysView;

    private TextView daysLabel;

    private Intent intent;



    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private Button updateButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_form);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        intent = getIntent();
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        plantDao = daoSession.getPlantDao();

        nameEdit = (EditText) findViewById(R.id.nameForm);
        frequencyEdit = (EditText) findViewById(R.id.frequencyForm);

        nameView = (TextView) findViewById(R.id.nameView);
        frequencyView= (TextView) findViewById(R.id.frequencyView);
        daysView= (TextView) findViewById(R.id.daysView);

        daysLabel = (TextView) findViewById(R.id.daysLabel);

        addButton = (Button) findViewById(R.id.addButton);
        editButton = (Button) findViewById(R.id.editButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        updateButton = (Button) findViewById(R.id.updateButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        setAddButtonListener();

        if(intent.getBooleanExtra(MainActivity.ADD_MODE, false)) {
            displayAddMode();
        } else if(intent.getBooleanExtra(MainActivity.VIEW_MODE, false)) {
            displayViewMode();
        }
    }

    private void setAddButtonListener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Plant newPlant = createPlantByForm();
                plantDao.insert(newPlant);

                finish();
            }
        });
    }

    private void setDeleteButtonListener(final Long plantId) {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                plantDao.deleteByKey(plantId);
                finish();
            }
        });
    }

    private void setEditButtonListener() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayEditMode();
            }
        });
    }

    private void setUpdateButtonListener() {
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plant.setName(nameEdit.getText().toString());
                plant.setWateringFrequency(Integer.parseInt(frequencyEdit.getText().toString()));
                plantDao.update(plant);
                displayViewMode();
            }
        });
    }

    private void setCancelButtonListener() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayViewMode();
            }
        });
    }

    private void displayAddMode() {
        nameEdit.setVisibility(View.VISIBLE);
        frequencyEdit.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.VISIBLE);

        daysLabel.setVisibility(View.GONE);


    }

    private void displayViewMode() {

        nameEdit.setVisibility(View.GONE);
        frequencyEdit.setVisibility(View.GONE);;

        updateButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);

        nameView.setVisibility(View.VISIBLE);
        frequencyView.setVisibility(View.VISIBLE);
        daysView.setVisibility(View.VISIBLE);

        daysLabel.setVisibility(View.VISIBLE);

        editButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);

        Long plantId = intent.getLongExtra(MainActivity.PLANT_ID, new Long(0));

        plant = plantDao.load(plantId);

        setDeleteButtonListener(plantId);
        setEditButtonListener();
        setFields(true);
    }

    private void displayEditMode() {

        nameView.setVisibility(View.GONE);
        frequencyView.setVisibility(View.GONE);
        daysView.setVisibility(View.GONE);

        daysLabel.setVisibility(View.GONE);

        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);

        updateButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        setUpdateButtonListener();
        setCancelButtonListener();


        nameEdit.setVisibility(View.VISIBLE);
        frequencyEdit.setVisibility(View.VISIBLE);;

        setFields(false);
    }

    private void setFields(boolean viewMode) {
        String name = plant.getName();
        int frequency = plant.getWateringFrequency();
        int days = plant.getDaysSinceLastWatering();

        if (viewMode) {
            nameView.setText(name);
            frequencyView.setText(Integer.toString(frequency));
            daysView.setText(Integer.toString(days));
        } else {
            nameEdit.setText(name);
            frequencyEdit.setText(Integer.toString(frequency));
        }

    }

    private Plant createPlantByForm() {

        String name = nameEdit.getText().toString();
        int frequency = Integer.parseInt(frequencyEdit.getText().toString());
        Plant newPlant = new Plant(null, name, frequency, 0, MainActivity.currentTime);
        return newPlant;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
