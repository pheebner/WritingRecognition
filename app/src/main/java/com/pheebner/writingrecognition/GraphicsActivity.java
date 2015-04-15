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

public class GraphicsActivity extends ActionBarActivity {

    private MyView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = new MyView(this);
        setContentView(mView);
        new Thread( new Runnable() {
            @Override
            public void run() {
                Network n = new Network(5, 6, 3, 5);
                double[] out = n.forwardPass(new double[] {1, 2, 3, 4 , 5});
                for (int i = 0; i < out.length; i++)
                    Log.d("OBS", "" + out[i]);
            }
        }).start();
    }

    private static final int ERASE_MENU_ID = Menu.FIRST;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');

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
            case ERASE_MENU_ID:
                mView.resetCanvas();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}