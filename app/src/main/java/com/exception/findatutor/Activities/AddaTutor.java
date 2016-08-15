package com.exception.findatutor.Activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;
import com.google.android.gms.location.LocationServices;

import static com.exception.findatutor.R.id.button2;
import static com.exception.findatutor.R.id.editText2;
import static com.exception.findatutor.R.id.editText3;
import static com.exception.findatutor.R.id.editText4;
import static com.exception.findatutor.R.id.editText6;
import static com.exception.findatutor.R.id.editText7;
import static com.exception.findatutor.R.id.editText9;

public class AddaTutor extends Activity {

    private EditText name, edu, exp, courses, from, to;
    private Button save;
    private ProgressDialog progressDialog;
    private MongoDB mongoDB;
    int a = 0;
    RadioGroup radioGroup;
    RadioButton radioButton;
    static String fee = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tutor);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        name = (EditText) findViewById(editText2);
        edu = (EditText) findViewById(editText3);
        exp = (EditText) findViewById(editText4);
        courses = (EditText) findViewById(editText7);
        from = (EditText) findViewById(editText9);
        to = (EditText) findViewById(editText6);
        save = (Button) findViewById(button2);

        mongoDB = MongoDB.getInstance();
        progressDialog = new ProgressDialog(AddaTutor.this, android.R.style.Theme_Holo_Dialog);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyTutor();
                if (a == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                        radioGroup = (RadioGroup) findViewById(R.id.radioGroup2);
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton) findViewById(selectedId);
                        fee = radioButton.getText().toString();
                        //same for signup as done with login
                        new AddaTutor.AsynchronousAddTutor(LoginActivity.uname, name.getText().toString(), edu.getText().toString(),
                                exp.getText().toString(), courses.getText().toString(), fee, from.getText().toString(),
                                to.getText().toString(), MainActivity.lat, MainActivity.lng)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    } else {
                        radioGroup = (RadioGroup) findViewById(R.id.radioGroup2);
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton) findViewById(selectedId);
                        fee = radioButton.getText().toString();
                        new AddaTutor.AsynchronousAddTutor(LoginActivity.uname, name.getText().toString(), edu.getText().toString(),
                                exp.getText().toString(), courses.getText().toString(), fee, from.getText().toString(),
                                to.getText().toString(), MainActivity.lat, MainActivity.lng)
                                .execute();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

    }

    private void verifyTutor() {

        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError("This is a required field");
            a = 1;
        }
        if (TextUtils.isEmpty(edu.getText().toString())) {
            edu.setError("This is a required field");
            a = 1;
        }
        if (TextUtils.isEmpty(exp.getText().toString())) {
            exp.setError("This is a required field");
            a = 1;
        }
        if (TextUtils.isEmpty(courses.getText().toString())) {
            courses.setError("This is a required field");
            a = 1;
        }
        if (TextUtils.isEmpty(from.getText().toString())) {
            from.setError("This is a required field");
            a = 1;
        }
        if (TextUtils.isEmpty(to.getText().toString())) {
            to.setError("This is a required field");
            a = 1;
        }
    }

    private class AsynchronousAddTutor extends AsyncTask<Void, Void, String> {
        private String username, name, edu, exp, courses, fee, from, to, lat, lng;

        public AsynchronousAddTutor(String... array) {
            super();
            this.username = array[0];
            this.name = array[1];
            this.edu = array[2];
            this.exp = array[3];
            this.courses = array[4];
            this.fee = array[5];
            this.from = array[6];
            this.to = array[7];
            this.lat = array[8];
            this.lng = array[9];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // viewMessageDialog("Register", message, android.R.drawable.ic_dialog_alert, "Ok");
            // Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            // this.cancel(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            return mongoDB.authenticateAddTutor(username, name, edu, exp, courses, fee, from, to, lat, lng);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (result == "j") {
                Toast.makeText(AddaTutor.this, "You've successfully registered!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddaTutor.this, "Tutor already exists!", Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

}