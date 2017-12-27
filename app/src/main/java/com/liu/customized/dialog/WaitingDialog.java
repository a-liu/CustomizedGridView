/**
 *   tfme Android client application
 *
 *   Copyright (C) 2016 tfme GmbH.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2,
 *   as published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.liu.customized.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liu.customized.R;

public class WaitingDialog extends DialogFragment {

    private static final String ARG_MESSAGE_ID = WaitingDialog.class.getCanonicalName() + ".ARG_MESSAGE_ID";
    private static final String ARG_CANCELABLE = WaitingDialog.class.getCanonicalName() + ".ARG_CANCELABLE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setCancelable(false);
    }

    /**
     * Public factory method to get dialog instances.
     *
     * @return              New dialog instance, ready to show.
     */
    public static WaitingDialog newInstance() {
        WaitingDialog fragment = new WaitingDialog();
        Bundle args = new Bundle();
        args.putBoolean(ARG_CANCELABLE, false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new
                ColorDrawable(Color.TRANSPARENT));
        // Create a view by inflating desired layout
        View v = inflater.inflate(R.layout.waiting_dialog, container,  false);
        v.setBackgroundColor(Color.TRANSPARENT);
        // set message
        TextView tv  = (TextView) v.findViewById(R.id.loadingText);
        tv.setTextIsSelectable(false);
//        int messageId = getArguments().getInt(ARG_MESSAGE_ID, "Loading");
        tv.setText("");

        // set progress wheel color
        ProgressBar progressBar  = (ProgressBar) v.findViewById(R.id.loadingBar);
        progressBar.setFocusable(false);
        progressBar.getIndeterminateDrawable().setColorFilter(
            ContextCompat.getColor(getActivity(), R.color.colorSecondary),
            PorterDuff.Mode.SRC_IN
        );
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        /// set cancellation behavior
        boolean cancelable = getArguments().getBoolean(ARG_CANCELABLE, false);
        dialog.setCancelable(cancelable);
        if (!cancelable) {
            // disable the back button
            DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {

                    if( keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                    return false;
                }
            };
            dialog.setOnKeyListener(keyListener);
        }
        return dialog;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
