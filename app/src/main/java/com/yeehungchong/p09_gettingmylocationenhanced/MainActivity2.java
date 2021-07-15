package com.yeehungchong.p09_gettingmylocationenhanced;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    Button btnRefresh;
    TextView tvNumberOfRecords;
    ListView lv;
    ArrayList<String> alCoord;
    ArrayAdapter<String> aaCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btnRefresh = findViewById(R.id.btnRefresh);
        tvNumberOfRecords = findViewById(R.id.tvNumberOfRecords);
        lv = findViewById(R.id.lv);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCoord();
            }
        });
    }

    private void getCoord() {
        alCoord = new ArrayList<String>();
        String folderLocation = getFilesDir().getAbsolutePath() + "/Folder";
        File targetFile = new File(folderLocation, "data.txt");
        if (targetFile.exists() == true) {
            try {
                FileReader reader = new FileReader(targetFile);
                BufferedReader br = new BufferedReader(reader);
                String line = br.readLine();
                while (line != null) {
                    alCoord.add(line);
                    line = br.readLine();
                }
                br.close();
                reader.close();
                aaCoord = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alCoord);
                lv.setAdapter(aaCoord);
                tvNumberOfRecords.setText("Number of records: " + alCoord.size());
            } catch (Exception e) {
                Toast.makeText(MainActivity2.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}