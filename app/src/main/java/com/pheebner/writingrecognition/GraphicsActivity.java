/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pheebner.writingrecognition;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class GraphicsActivity extends ActionBarActivity {

    private MyView mView;
    private RadioGroup radioGroup;
    private ProgressDialog dialog;

    private ExampleList[] exampleLists;

    private Network network;

    public static final int NUM_COORD_INPUT = 64;
    public static final int NUM_SECTOR_INPUT = 16;
    public static final int NUM_INPUT = NUM_COORD_INPUT + NUM_SECTOR_INPUT;
    public static final int NUM_HIDDEN = 12;
    public static final int NUM_HIDDEN_LAYERS = 3;
    public static final int NUM_OUTPUT = 5;
    public static final double CURVATURE = 1;

    public static final int TRAINING_REPS = 5000;

    public static final String[] LETTERS = new String[] {
            "A", "B", "C", "D", "E"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics);
        mView = (MyView) findViewById(R.id.my_view);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Training...");
        dialog.setCancelable(false);

        initNetworkAndExamples();
    }

    public void train() {
        dialog.show();

        new Thread(new TrainRunnable()).start();
    }

    private void initNetworkAndExamples() {
        network = new Network(CURVATURE, NUM_INPUT, NUM_HIDDEN, NUM_HIDDEN_LAYERS, NUM_OUTPUT);
        exampleLists = new ExampleList[NUM_OUTPUT];
        for (int i = 0; i < NUM_OUTPUT; i++) {
            exampleLists[i] = new ExampleList();
        }
    }

    private class TrainRunnable implements Runnable {

        @Override
        public void run() {
            double[][] outputs = new double[NUM_OUTPUT][NUM_OUTPUT];
            Handler handler = new Handler(Looper.getMainLooper());
            UpdateRunnable runnable = new UpdateRunnable();

            for (int i = 0; i < NUM_OUTPUT; i++) {
                outputs[i] = getOuputArray(i);
            }

            for (int i = 0; i < TRAINING_REPS; i++) {
                int progress = (int) ((((float) i) / ((float) TRAINING_REPS)) * 100);
                runnable.message = "Training..." + progress + "%";
                handler.post(runnable);

                for (int j = 0; j < NUM_OUTPUT; j++) {
                    network.train(
                            exampleLists[j].getExample(),
                            outputs[j]
                    );
                }
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });
        }
    }

    private class UpdateRunnable implements Runnable {

        public String message;

        @Override
        public void run() {
            dialog.setMessage(message);
        }
    }

    public void onSubmission(double[] inputs) {
        int checkedIndex = getCheckedRadioButtonIndex();
        if (-1 < checkedIndex && checkedIndex < NUM_OUTPUT) {
            //letters[checkedIndex] = submissionData;
            exampleLists[checkedIndex].addExample(inputs);
        } else if (checkedIndex == NUM_OUTPUT) {
            double[] output = network.forwardPass(inputs);

            showOutput(output);

            Log.d("OBS", "output");
            for (int i = 0; i < output.length; i++) {
                Log.d("OBS", output[i] + "");
            }
        }
    }

    private double[] getOuputArray(int index) {

        double[] retVal = new double[NUM_OUTPUT];

        for (int i = 0; i < NUM_OUTPUT; i++) {
            retVal[i] = i == index ? 1 : 0;
        }

        return retVal;
    }

    private void showOutput(double[] output) {
        int indexOfBiggest = 0;
        double biggest = 0;

        for (int i = 0; i < output.length; i++) {
            if (output[i] > biggest) {
                biggest = output[i];
                indexOfBiggest = i;
            }
        }

        Toast.makeText(this, LETTERS[indexOfBiggest], Toast.LENGTH_SHORT).show();
    }

    private int getCheckedRadioButtonIndex() {
        int checkedID = radioGroup.getCheckedRadioButtonId();
        View button = radioGroup.findViewById(checkedID);
        return radioGroup.indexOfChild(button);
    }

    private static final int TRAIN_ID = 1;
    private static final int CLEAR_ID = 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, TRAIN_ID, 0, "Train").setShortcut('5', 'z');
        menu.add(0, CLEAR_ID, 1, "Clear").setShortcut('5', 'z');

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case TRAIN_ID:
                train();
                return true;
            case CLEAR_ID:
                initNetworkAndExamples();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}