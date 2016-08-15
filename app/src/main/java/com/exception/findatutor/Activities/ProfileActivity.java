package com.exception.findatutor.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;

import static com.exception.findatutor.R.id.textView12;
import static com.exception.findatutor.R.id.textView14;
import static com.exception.findatutor.R.id.textView15;
import static com.exception.findatutor.R.id.textView17;
import static com.exception.findatutor.R.id.textView19;
import static com.exception.findatutor.R.id.textView6;
import static com.exception.findatutor.R.id.textView7;
import static com.exception.findatutor.R.id.textView8;
import static com.exception.findatutor.R.id.textView9;


public class ProfileActivity extends Activity {
    private TextView name, registeringAs, occupation, city, phoneno, edu, exp, age, charges;
    private ProgressDialog progressDialog;
    private MongoDB mongoDB;
    private static String username = null;
    private Bundle dataBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        name = (TextView) findViewById(textView6);
        registeringAs = (TextView) findViewById(textView7);
        occupation = (TextView) findViewById(textView8);
        city = (TextView) findViewById(textView9);
        phoneno = (TextView) findViewById(textView14);
        edu = (TextView) findViewById(textView19);
        exp = (TextView) findViewById(textView12);
        age = (TextView) findViewById(textView15);
        charges = (TextView) findViewById(textView17);

        mongoDB = MongoDB.getInstance();
        progressDialog = new ProgressDialog(ProfileActivity.this, android.R.style.Theme_Holo_Dialog);

        dataBundle = getIntent().getExtras();
        username = dataBundle.getString("username");
        Toast.makeText(ProfileActivity.this, "Username is: " + username, Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ProfileActivity.getFromUsers(username).execute();

        } else {
            new ProfileActivity.getFromUsers(username).execute();
        }

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

    private class getFromUsers extends AsyncTask<Void, Void, String[]> {
        private String username;
        String data[] = {"", "", "", "", "", ""};


        public getFromUsers(String... array) {
            super();
            this.username = array[0];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(Void... params) {
            data = mongoDB.getFromUsers(username).clone();
            return data;
        }

        @Override
        protected void onPostExecute(final String[] result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (data[0] == null) {
                Toast.makeText(ProfileActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                name.setText("");
                registeringAs.setText("");
                occupation.setText("");
                city.setText("");
                phoneno.setText("");
                edu.setText("");
                exp.setText("");
                age.setText("");
                charges.setText("");
            } else {
                Toast.makeText(ProfileActivity.this, "Data found", Toast.LENGTH_SHORT).show();
                //name.setText("");
                registeringAs.setText(data[0]);
                occupation.setText(data[1]);
                city.setText(data[2]);
                phoneno.setText(data[3]);
                //edu.setText("");
                //exp.setText("");
                age.setText(data[4]);
                //charges.setText("");
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
