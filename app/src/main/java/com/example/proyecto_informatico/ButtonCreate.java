package com.example.proyecto_informatico;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ButtonCreate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_button_create);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // === TÍTULO ===
        EditText editTitle = findViewById(R.id.edit_title);
        TextView charCounterTitle = findViewById(R.id.char_counter_title);

        int maxTitleChars = 100;
        editTitle.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(maxTitleChars)
        });

        editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charCounterTitle.setText(s.length() + "/" + maxTitleChars);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // === DESCRIPCIÓN ===
        EditText editDescription = findViewById(R.id.edit_description);
        TextView charCounterDesc = findViewById(R.id.char_counter_description);

        int maxDescChars = 500;
        editDescription.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(maxDescChars)
        });

        editDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charCounterDesc.setText(s.length() + "/" + maxDescChars);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}

