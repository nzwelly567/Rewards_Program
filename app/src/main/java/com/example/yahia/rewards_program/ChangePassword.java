package com.example.yahia.rewards_program;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    private EditText oldPass;
    private EditText newPass;
    private EditText secNewPass;
    private String newPassword;
    private String secNewPassword;
    private Button changePasswordBtn;
    private MobileServiceClient mClient;
    private MobileServiceTable<Admin> mAdminTable;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        setContentView(R.layout.activity_change_password);
        //Hides keyboard until clicked on
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        oldPass = (EditText)findViewById(R.id.oldPass);
        newPass = (EditText)findViewById(R.id.newPass);
        secNewPass = (EditText)findViewById(R.id.secNewPass);
        changePasswordBtn = (Button)findViewById(R.id.changePasswordBtn);
        changePasswordBtn.setOnClickListener(ChangePassword.this);

        //**************

        //Makes sure the button is not pressed before validation of text
        oldPass.setError("Required");
        newPass.setError("Required");
        secNewPass.setError("Required");
        changePasswordBtn.setClickable(false);

        //Validation for passwords
        oldPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(oldPass.getText().length() < 4)
                {
                    oldPass.setError("Passwords are 4 digits!");
                    changePasswordBtn.setClickable(false);
                }

            }
        });


        newPass.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(newPass.getText().length() < 4)
                {
                    newPass.setError("Passwords are 4 digits!");
                    changePasswordBtn.setClickable(false);
                }

            }
        });


        secNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(secNewPass.getText().length() < 4)
                {
                    secNewPass.setError("Passwords are 4 digits!");
                    changePasswordBtn.setClickable(false);
                }
                else if (secNewPass.getText().length() == 4 && newPass.getText().length() == 4 && oldPass.getText().length() == 4) {
                    changePasswordBtn.setClickable(true);
                }

            }
        });




        //**************

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
                        Intent intent1 = new Intent(ChangePassword.this, MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_dashboard:
                        Intent intent2 = new Intent(ChangePassword.this, AddNewMember.class);
                        startActivity(intent2);
                        break;
                    case R.id.navigation_enter_phone:
                        Intent intent3 = new Intent(ChangePassword.this, EnterAlternateID.class);
                        startActivity(intent3);
                        break;
                    case R.id.navigation_notifications:
                        Intent intent4 = new Intent(ChangePassword.this, AdminMain.class);
                        startActivity(intent4);
                        break;
                }
                return false;
            }
        });

        try {
            mClient = new MobileServiceClient(
                    "https://rewards-program.azurewebsites.net",
                    this);

            mAdminTable = mClient.getTable(Admin.class);

        } catch (MalformedURLException e) {
            //  createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e) {
            //createAndShowDialog(e, "Error");
        }
    }


    public void onClick(View view) {
        String oldPassword = oldPass.getText().toString();
        newPassword = newPass.getText().toString();
        secNewPassword = secNewPass.getText().toString();
        getOldPass(oldPassword);
    }

    public void getOldPass(final String pass) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>(){

            ProgressDialog progDailog = new ProgressDialog(ChangePassword.this, R.style.AppCompatAlertDialogStyle);

            @Override
            protected void onPreExecute () {
                progDailog.setMessage("Loading...");
                progDailog.setIndeterminate(false);
                progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDailog.setCancelable(true);
                progDailog.show();
            }

            //Runs a query to the database in the background.
            @Override
            protected String doInBackground(Void... voids) {

                String result = "";

                try {
                    List<Admin> list = mAdminTable.where().field("password").eq(getSecurePassword(pass, "97C70718-EA34-4ED3-87D2-EB274BD4B340")).execute().get();
                    if(list.size() == 0) {
                        result = "fail";
                    }
                    else
                    {
                        if(newPassword.toString().equals(secNewPassword.toString())){
                            updateItemInTable(list.get(0));
                            result = "success";
                        }
                        else {
                            result = "matchPasswords";
                        }
                    }
                } catch (final Exception e) {
                    //e.printStackTrace();
                }

                return result;
            }

            //After the query has been completed, this method runs and shows the messages corresponding to the results.
            @Override
            protected void onPostExecute(String result) {
                progDailog.dismiss();
                if(result.equalsIgnoreCase("fail")){
                    View titleView = inflater.inflate(R.layout.layout, null);
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ChangePassword.this)
                            .setCustomTitle(titleView);
                    ((TextView) titleView.findViewById(R.id.Alert)).setText("Error Message...");
                    dlgAlert.setMessage(Html.fromHtml("<Big>"+"Oops that must be the wrong password!"+"</Big>"));
                    dlgAlert.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            overridePendingTransition(0, 0);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                        }
                    });
                    dlgAlert.create().show();
                    dlgAlert.setCancelable(true);
                }
                else if (result.equals("matchPasswords")) {
                    progDailog.dismiss();
                    View titleView = inflater.inflate(R.layout.layout, null);
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ChangePassword.this)
                            .setCustomTitle(titleView);
                    ((TextView) titleView.findViewById(R.id.Alert)).setText("Error Message...");
                    dlgAlert.setMessage(Html.fromHtml("<Big>"+"Oops, make sure the new password matches the confirmation password!"+"</Big>"));
                    dlgAlert.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            overridePendingTransition(0, 0);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                        }
                    });
                    dlgAlert.create().show();
                    dlgAlert.setCancelable(true);

                }

                else {
                    progDailog.dismiss();
                    View titleView = inflater.inflate(R.layout.layout, null);
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ChangePassword.this)
                            .setCustomTitle(titleView);
                    ((TextView) titleView.findViewById(R.id.Alert)).setText("Success...");
                    dlgAlert.setMessage(Html.fromHtml("<Big>"+"Password has been successfully changed!"+"</Big>"));
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();

                    //Delays the running of method goToMemberAdded() by 3000 milliseconds
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goToAdminMain();
                        }
                    }, 2500);
                }
            }
        };

        task.execute();

    }


    public void goToAdminMain(){
        Intent intent = new Intent(ChangePassword.this, AdminMain.class);
        startActivity(intent);
    }

    public void updateItemInTable(Admin item) throws ExecutionException, InterruptedException {
        item.setPassword(getSecurePassword(newPass.getText().toString(), item.getId()));
        mAdminTable.update(item).get();
    }


    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    public String getSecurePassword(String passwordToHash, String   salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
