package com.android.aaroo.helper;

import android.content.res.Resources;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.cardview.widget.CardView;

import com.android.aaroo.R;

import static android.graphics.Color.parseColor;

public class CustomTextWatcher implements TextWatcher {

    Button button;
    EditText[] edList;

    public CustomTextWatcher(EditText[] edList, Button button) {
        this.button = button;
        this.edList = edList;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        for (EditText editText : edList) {
            if (editText.getText().toString().trim().length() <= 0) {
                button.setEnabled(false);
                button.setBackgroundColor(Resources.getSystem().getColor(R.color.blue_300));

                break;
            }
            else button.setEnabled(true);
            button.setBackgroundColor(Resources.getSystem().getColor(R.color.blue_500));

        }
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        for (EditText editText : edList) {
            if (editText.getText().toString().trim().length() <= 0) {
                button.setEnabled(false);
                button.setBackgroundColor(Resources.getSystem().getColor(R.color.blue_300));
                break;
            }
            else button.setEnabled(true);
            button.setBackgroundColor(Resources.getSystem().getColor(R.color.blue_500));
        }
    }
    @Override
    public void afterTextChanged(Editable s) {
        for (EditText editText : edList) {
            if (editText.getText().toString().trim().length() <= 0) {
                button.setEnabled(false);
                button.setBackgroundColor(Resources.getSystem().getColor(R.color.blue_300));

                break;
            }
            else button.setEnabled(true);
            button.setBackgroundColor(Resources.getSystem().getColor(R.color.blue_500));
        }
    }
}