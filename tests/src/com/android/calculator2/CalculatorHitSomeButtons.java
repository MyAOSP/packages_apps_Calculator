/**
 * Copyright (c) 2008, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calculator2;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.android.calculator2.Page.LargePanel;
import com.android.calculator2.Page.NormalPanel;
import com.android.calculator2.Page.Panel;
import com.android.calculator2.Page.SmallPanel;
import com.android.calculator2.view.CalculatorDisplay;
import com.android.calculator2.view.CalculatorViewPager;
import com.android.calculator2.view.Cling;

/**
 * Instrumentation tests for poking some buttons
 *
 */

public class CalculatorHitSomeButtons extends ActivityInstrumentationTestCase2<Calculator> {
    public boolean setup = false;
    private static final String TAG = "CalculatorTests";
    Calculator mActivity = null;
    Instrumentation mInst = null;

    public CalculatorHitSomeButtons() {
        super(Calculator.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mActivity = getActivity();
        mInst = getInstrumentation();

        final View cling = mActivity.findViewById(R.id.cling_dismiss);
        if(cling != null) {
            cling.post(new Runnable() {
                @Override
                public void run() {
                    mActivity.dismissSimpleCling(cling);
                }
            });
        }
        CalculatorSettings.saveKey(mActivity, Cling.SIMPLE_CLING_DISMISSED_KEY, true);
        CalculatorSettings.saveKey(mActivity, Cling.MATRIX_CLING_DISMISSED_KEY, true);
        CalculatorSettings.saveKey(mActivity, Cling.GRAPH_CLING_DISMISSED_KEY, true);
        CalculatorSettings.saveKey(mActivity, Cling.HEX_CLING_DISMISSED_KEY, true);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @LargeTest
    public void testPressSomeKeys() {
        Log.v(TAG, "Pressing some keys!");

        swipe(NormalPanel.BASIC);
        swipe(LargePanel.BASIC);

        // Make sure that we clear the output
        tap(R.id.clear);
        longClick(R.id.del);

        // 3 + 4 * 5 => 23
        press(KeyEvent.KEYCODE_3);
        press(KeyEvent.KEYCODE_PLUS);
        press(KeyEvent.KEYCODE_4);
        press(KeyEvent.KEYCODE_9 | KeyEvent.META_SHIFT_ON);
        press(KeyEvent.KEYCODE_5);
        press(KeyEvent.KEYCODE_ENTER);

        assertEquals("23", displayVal());
    }

    @LargeTest
    public void testTapSomeButtons() {
        Log.v(TAG, "Tapping some buttons!");

        swipe(NormalPanel.BASIC);
        swipe(LargePanel.BASIC);

        // Make sure that we clear the output
        tap(R.id.clear);
        longClick(R.id.del);

        // 567 / 3 => 189
        tap(R.id.digit5);
        tap(R.id.digit6);
        tap(R.id.digit7);
        tap(R.id.div);
        tap(R.id.digit3);
        tap(R.id.equal);

        assertEquals("189", displayVal());

        // make sure we can continue calculations also
        // 189 - 789 => -600
        tap(R.id.minus);
        tap(R.id.digit7);
        tap(R.id.digit8);
        tap(R.id.digit9);
        tap(R.id.equal);

        // Careful: the first digit in the expected value is \u2212, not "-" (a
        // hyphen)
        assertEquals(mActivity.getString(R.string.minus) + "600", displayVal());
    }

    @LargeTest
    public void testTapSomeMatrixButtons() {
        Log.v(TAG, "Making some matrices!");

        swipe(NormalPanel.MATRIX);
        swipe(LargePanel.MATRIX);

        // Make sure that we clear the output
        tap(R.id.clear);
        longClick(R.id.del);

        // 567 + 3 => 570
        tap(R.id.digit5);
        tap(R.id.digit6);
        tap(R.id.digit7);
        tap(R.id.plus);
        tap(R.id.digit3);
        tap(R.id.equal);

        assertEquals("570", displayVal());
    }

    @LargeTest
    public void testMatrixMult() {
        Log.v(TAG, "Testing correctness of matrix multiplication.");

        swipe(NormalPanel.MATRIX);
        swipe(LargePanel.MATRIX);

        // Clear the input
        tap(R.id.clear);
        longClick(R.id.del);

        // Test square matrix times identity.
        tap(R.id.matrix);
        tap(R.id.digit5);
        tap(R.id.next);
        tap(R.id.digit3);
        tap(R.id.next);
        tap(R.id.digit7);
        tap(R.id.next);
        tap(R.id.digit9);
        tap(R.id.next);

        tap(R.id.mul);
        tap(R.id.matrix);

        tap(R.id.digit1);
        tap(R.id.next);
        tap(R.id.digit0);
        tap(R.id.next);
        tap(R.id.digit0);
        tap(R.id.next);
        tap(R.id.digit1);

        tap(R.id.equal);

        assertEquals(displayVal(), "[[5,3][7,9]]");
    }

    @LargeTest
    public void testDeterminant() {
        Log.v(TAG, "Testing correctness of determinant.");

        swipe(NormalPanel.MATRIX);
        swipe(LargePanel.MATRIX);

        tap(R.id.clear);
        longClick(R.id.del);

        // Type det
        tap(R.id.det);
        // Make test matrix
        tap(R.id.matrix);
        tap(R.id.digit5);
        tap(R.id.next);
        tap(R.id.digit3);
        tap(R.id.digit7);
        tap(R.id.next);
        tap(R.id.digit2);
        tap(R.id.next);
        tap(R.id.digit1);
        tap(R.id.digit9);
        tap(R.id.next);

        swipe(NormalPanel.BASIC);
        swipe(LargePanel.BASIC);

        tap(R.id.rightParen);

        tap(R.id.equal);

        assertTrue(withinTolerance(Double.parseDouble(displayVal()), 21.0));
    }

    @LargeTest
    public void testMatrixScalarOps() {
        Log.v(TAG, "Testing matrix-scalar multiplication, functions, and order of operations.");

        swipe(NormalPanel.MATRIX);
        swipe(LargePanel.MATRIX);

        tap(R.id.clear);
        longClick(R.id.del);

        // Testing det(cos([[1,2][3,4]])*log(1+2^3))
        tap(R.id.det);

        swipe(NormalPanel.ADVANCED);
        swipe(SmallPanel.ADVANCED);

        tap(R.id.cos);

        swipe(NormalPanel.MATRIX);
        swipe(LargePanel.MATRIX);

        tap(R.id.matrix);
        tap(R.id.digit1);
        tap(R.id.next);
        tap(R.id.digit2);
        tap(R.id.next);
        tap(R.id.digit3);
        tap(R.id.next);
        tap(R.id.digit4);
        tap(R.id.next);

        swipe(NormalPanel.ADVANCED);
        swipe(SmallPanel.ADVANCED);

        tap(R.id.rightParen);

        swipe(NormalPanel.MATRIX);
        swipe(LargePanel.MATRIX);

        tap(R.id.mul);

        swipe(NormalPanel.ADVANCED);
        swipe(SmallPanel.ADVANCED);

        tap(R.id.lg);

        swipe(NormalPanel.MATRIX);
        swipe(LargePanel.MATRIX);

        tap(R.id.digit1);
        tap(R.id.plus);
        tap(R.id.digit2);

        swipe(NormalPanel.ADVANCED);
        swipe(SmallPanel.ADVANCED);

        tap(R.id.power);

        swipe(NormalPanel.BASIC);
        swipe(LargePanel.BASIC);

        tap(R.id.digit3);

        swipe(NormalPanel.ADVANCED);
        swipe(SmallPanel.ADVANCED);

        tap(R.id.rightParen);
        tap(R.id.rightParen);

        swipe(NormalPanel.BASIC);
        swipe(LargePanel.BASIC);

        tap(R.id.equal);

        assertTrue(withinTolerance(Double.parseDouble(
                displayVal().replace(Logic.MINUS, '-')), -0.6967269770522611));
    }

    // helper functions
    private void press(int keycode) {
        mInst.sendKeyDownUpSync(keycode);
    }

    private View getView(int id) {
        CalculatorViewPager pager = (CalculatorViewPager) mActivity.findViewById(R.id.panelswitch);
        CalculatorViewPager smallPager = (CalculatorViewPager) mActivity.findViewById(R.id.smallPanelswitch);
        CalculatorViewPager largePager = (CalculatorViewPager) mActivity.findViewById(R.id.largePanelswitch);

        // Phone
        if(pager != null) {
            // Find the view on the current page
            View v = ((CalculatorPageAdapter) pager.getAdapter()).getViewAtDontDetach(
                    pager.getCurrentItem()).findViewById(id);
            if(v != null) {
                return v;
            }
        }
        // Tablet
        else {
            // Find the view on the current pages
            View v = ((CalculatorPageAdapter) smallPager.getAdapter()).getViewAtDontDetach(
                    smallPager.getCurrentItem()).findViewById(id);
            if(v != null) {
                return v;
            }
            v = ((CalculatorPageAdapter) largePager.getAdapter()).getViewAtDontDetach(
                    largePager.getCurrentItem()).findViewById(id);
            if(v != null) {
                return v;
            }
        }

        // Find the view in the entire app (if it wasn't on the pager)
        View view = mActivity.findViewById(id);
        if(view != null) {
            return view;
        }

        return null;
    }

    private void tap(int id) {
        View view = getView(id);
        if(view != null) {
            TouchUtils.clickView(this, view);
        }
    }

    private void longClick(final int id) {
        View view = getView(id);
        if(view != null) {
            TouchUtils.longClickView(this, view);
        }
    }

    protected boolean swipe(final Panel page) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.scrollToPage(new Page(mActivity, page));
            }
        });

        sleep();
        return true;
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String displayVal() {
        CalculatorDisplay display = (CalculatorDisplay) mActivity.findViewById(R.id.display);
        assertNotNull(display);

        return display.getText();
    }

    // Calculate error in a result, relative to the truth
    private boolean withinTolerance(double result, double truth) {
        return (100.0 * Math.abs(truth - result) / Math.abs(truth)) < 0.001;
    }
}
