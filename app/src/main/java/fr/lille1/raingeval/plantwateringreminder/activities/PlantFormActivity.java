package fr.lille1.raingeval.plantwateringreminder.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

import fr.lille1.raingeval.plantwateringreminder.App;
import fr.lille1.raingeval.plantwateringreminder.R;
import fr.lille1.raingeval.plantwateringreminder.entities.DaoSession;
import fr.lille1.raingeval.plantwateringreminder.entities.Plant;
import fr.lille1.raingeval.plantwateringreminder.entities.PlantDao;

public class PlantFormActivity extends AppCompatActivity {

    private PlantDao plantDao;

    private EditText nameEdit;
    private EditText frequencyEdit;
    private EditText daysEdit;

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

    private void setUpdateButtonListener(final Long plantId) {
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Plant plant = createPlantByForm();
                plant.setId(plantId);
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

        setDeleteButtonListener(plantId);
        setEditButtonListener();
        setFields(true, plantId);
    }

    private void displayEditMode() {

        Long plantId = intent.getLongExtra(MainActivity.PLANT_ID, new Long(0));

        nameView.setVisibility(View.GONE);
        frequencyView.setVisibility(View.GONE);
        daysView.setVisibility(View.GONE);

        daysLabel.setVisibility(View.GONE);

        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);

        updateButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        setUpdateButtonListener(plantId);
        setCancelButtonListener();


        nameEdit.setVisibility(View.VISIBLE);
        frequencyEdit.setVisibility(View.VISIBLE);;

        setFields(false, plantId);
    }

    private void setFields(boolean viewMode, Long plantId) {
        plantId = intent.getLongExtra(MainActivity.PLANT_ID, new Long(0));
        if (plantId != 0){
            Plant plant = plantDao.load(plantId);
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
    }

    private Plant createPlantByForm() {
        String name = nameEdit.getText().toString();
        int frequency = Integer.parseInt(frequencyEdit.getText().toString());
        Date date = new Date();
        long dateValue = date.getTime();
        Plant newPlant = new Plant(null, name, frequency, 0, MainActivity.currentTime);
        System.out.println(newPlant.getId());
        return newPlant;
    }
}
