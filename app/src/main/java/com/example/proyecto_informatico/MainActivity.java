package com.example.proyecto_informatico;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        assignGradient();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void assignGradient() {
        GradientDrawable gradient = new GradientDrawable();
        gradient.setColors(new int[] {
                Color.parseColor("#fbecfa"),
                Color.parseColor("#fff1ca")
        });
        gradient.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        gradient.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradient.setShape(GradientDrawable.RECTANGLE);
        findViewById(R.id.main).setBackground(gradient);
    }

}