package com.example.yahia.rewards_program;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Yahia on 5/15/2017.
 */

public class AdminView extends AppCompatActivity {

    private EditText easy;
    private EditText medium;
    private EditText hard;
    private EditText target;
    Button update_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        update_btn = (Button)findViewById(R.id.update_btn);
        easy = (EditText)findViewById(R.id.easy);
        medium = (EditText)findViewById(R.id.medium);
        hard = (EditText)findViewById(R.id.hard);
        target = (EditText)findViewById(R.id.target);

        //Calls the helper function to stop basic android animation.
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        Intent intent1 = new Intent(AdminView.this, MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_dashboard:
                        Intent intent2 = new Intent(AdminView.this, AddNewMember.class);
                        startActivity(intent2);
                        break;
                    case R.id.navigation_enter_phone:
                        Intent intent3 = new Intent(AdminView.this, EnterAlternateID.class);
                        startActivity(intent3);
                        break;
                    case R.id.navigation_notifications:
                        Intent intent4 = new Intent(AdminView.this, AdminView.class);
                        startActivity(intent4);
                        break;
                }
                return false;
            }
        });
    }
    public void onClick(View view) {
        if (view == update_btn) {

        }
    }
}


