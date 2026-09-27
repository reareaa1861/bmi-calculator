package com.example.bmicalculator;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText editWeight, editHeight;
    private TextView txtBmi, txtCategory;
    DecimalFormat formatter = new DecimalFormat("#,###.##");
    private float currentFontScale = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editWeight  = findViewById(R.id.editWeight);
        editHeight  = findViewById(R.id.editHeight);
        txtBmi      = findViewById(R.id.txtBmi);
        txtCategory = findViewById(R.id.txtCategory);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        editWeight.setFilters(new InputFilter[]{ new DecimalDigitsInputFilter(8, 2) });
        editHeight.setFilters(new InputFilter[]{ new DecimalDigitsInputFilter(8, 2) });

        currentFontScale = getResources().getConfiguration().fontScale;

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBmi();
            }
        });
    }

    private void calculateBmi() {
        String sWeight = editWeight.getText().toString().trim();
        String sHeight = editHeight.getText().toString().trim();

        if (sWeight.isEmpty() || sHeight.isEmpty()) {
            Toast.makeText(this, R.string.error_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        double weight   = Double.parseDouble(sWeight);
        double heightCm = Double.parseDouble(sHeight);
        if (heightCm <= 0) {
            Toast.makeText(this, R.string.error_zero, Toast.LENGTH_SHORT).show();
            return;
        }
        double heightM = heightCm / 100.0;
        double bmi = weight / (heightM * heightM);
        txtBmi.setText(formatter.format(bmi));
        showCategory(bmi);
    }

    private void showCategory(double bmi) {
        int textRes, colorRes;
        if (bmi < 18.5) {
            textRes = R.string.cat_underweight; colorRes = R.color.cat_underweight;
        } else if (bmi < 25.0) {
            textRes = R.string.cat_normal;      colorRes = R.color.cat_normal;
        } else if (bmi < 30.0) {
            textRes = R.string.cat_overweight;  colorRes = R.color.cat_overweight;
        } else {
            textRes = R.string.cat_obese;       colorRes = R.color.cat_obese;
        }
        txtCategory.setText(textRes);
        int color = ContextCompat.getColor(this, colorRes);
        txtCategory.setTextColor(color);
        txtBmi.setTextColor(color);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.fontScale != currentFontScale) {
            currentFontScale = newConfig.fontScale;
            new AlertDialog.Builder(this)
                    .setTitle(R.string.font_dialog_title)
                    .setMessage(R.string.font_dialog_msg)
                    .setPositiveButton(R.string.font_use_system, (d, w) -> recreate())
                    .setNegativeButton(R.string.font_keep, (d, w) -> {})
                    .show();
        }
    }
}
class DecimalDigitsInputFilter implements InputFilter {
    private Pattern mPattern;
    DecimalDigitsInputFilter(int digits, int digitsAfterZero) {
        mPattern = Pattern.compile("[0-9]{0," + (digits - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = mPattern.matcher(dest);
        if (!matcher.matches()) return "";
        return null;
    }
}