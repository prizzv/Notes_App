package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    ListView listView;

    static ArrayList<String> notes = new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);

        listView = findViewById(R.id.listView);

        HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("notes", null);

        if(set == null){
            notes.add("Example Note");
        }else{
            notes = new ArrayList(set);
        }

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, notes);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NotesEditActivity.class);
                intent.putExtra("noteId", position);

                startActivity(intent);
            }
        });

    // To delete a note
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Are you sure?")
                    .setMessage("Do you want to delete this note?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            notes.remove(position);
                            arrayAdapter.notifyDataSetChanged();  // have to always notify the changes

                            HashSet<String> set = new HashSet<>(notes);  // this is a new method of saving i.e. saving as a hashset
                            sharedPreferences.edit().putStringSet("notes", set).apply();
                        }
                    })
                    .setNegativeButton("No",null)
                    .show();

                return true;
            }
        });
    }

    //MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            case R.id.add_note:
                Intent intent = new Intent(getApplicationContext(), NotesEditActivity.class);
                startActivity(intent);

                return true;

            case R.id.enable_fingerprint_auth:
                SharedPreferences fingerPrintPreferences = getSharedPreferences("FingerPrint", 0);
                SharedPreferences.Editor sharedPrefsEdit = fingerPrintPreferences.edit();
                boolean isFingerPrintAuthOn = fingerPrintPreferences.getBoolean("FingerprintAuth", false);

                if(isFingerPrintAuthOn){
                    sharedPrefsEdit.putBoolean("FingerprintAuth", false);
                }else{
                    sharedPrefsEdit.putBoolean("FingerprintAuth", true);
                }
                sharedPrefsEdit.apply();

                return true;
            case R.id.dark_mode_switch:
                Log.i("Item Selected", "Dark Mode");

                SharedPreferences appSettingsPreference = getSharedPreferences("AppSettingPrefs", 0);
                SharedPreferences.Editor sharedPrefEdit = appSettingsPreference.edit();
                boolean isNightModeOn = appSettingsPreference.getBoolean("NightMode", false);

                if(isNightModeOn){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPrefEdit.putBoolean("NightMode", false);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPrefEdit.putBoolean("NightMode", true);
                }
                sharedPrefEdit.apply();

                return true;
        }


        return false;
    }
}