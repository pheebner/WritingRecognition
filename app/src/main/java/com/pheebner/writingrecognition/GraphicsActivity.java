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

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

public class GraphicsActivity extends ActionBarActivity {

    private MyView mView;
    private RadioGroup radioGroup;

    private KickList[] letterData;

    private Network network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics);
        mView = (MyView) findViewById(R.id.my_view);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        letterData = new KickList[5];
        for (int i = 0; i < 5; i++) {
            letterData[i] = new KickList();
        }

        network = new Network(40, 10, 2, 5);
    }

    public void train() {
        double[][] outputs = new double[5][5];

        for (int i = 0; i < 5; i++) {
            outputs[i] = getOuputArray(i);
        }

        for (int i = 0; i < 3000; i++) {
            for (int j = 0; j < letterData.length; j++) {
                network.train(letterData[j].getNext(), outputs[j]);
            }
        }
    }

    public void onSubmission(int[] xCoords, int[] yCoords) {
        double[] submissionData = new double[40];

        int coordsIndex;
        for (int i = 0; i < 40; i++) {
            coordsIndex = i / 2;
            submissionData[i] = xCoords[coordsIndex];
            submissionData[++i] = yCoords[coordsIndex];
        }

        int checkedIndex = getCheckedRadioButtonIndex();
        if (-1 < checkedIndex && checkedIndex < 5) {
            letterData[checkedIndex].put(submissionData);
        }

        double[] output = network.forwardPass(submissionData);

        Log.d("OBS", "output");
        for (int i = 0; i < output.length; i++) {
            Log.d("OBS", output[i] + "");
        }
    }

    private double[] getOuputArray(int index) {

        double[] retVal = new double[5];

        for (int i = 0; i < 5; i++) {
            retVal[i] = i == index ? 1 : 0;
        }

        return retVal;
    }

    private int getCheckedRadioButtonIndex() {
        int checkedID = radioGroup.getCheckedRadioButtonId();
        View button = radioGroup.findViewById(checkedID);
        return radioGroup.indexOfChild(button);
    }

    private static final int MENU_ID = Menu.FIRST;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_ID, 0, "Train").setShortcut('5', 'z');

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
            case MENU_ID:
                train();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}