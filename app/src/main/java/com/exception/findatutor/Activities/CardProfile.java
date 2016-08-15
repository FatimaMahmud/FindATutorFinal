package com.exception.findatutor.Activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;


public class CardProfile extends Activity implements CompoundButton.OnCheckedChangeListener {

    private Bundle dataBundle;
    private String coverTitle, coverAge;
    private TextView titleTextView;
    private ImageView coverImageView;
    private TextView titleAgeView;
    private String coverImage;
    private CheckBox checkboxcontact;
    private TextView PhonenoText, Phoneno;
    private TextView Email;
    private TextView EmailText;
    private MongoDB mongoDB;
    private boolean NotificationBoolean = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mongoDB = MongoDB.getInstance();
        dataBundle = getIntent().getExtras();

        coverTitle = dataBundle.getString("title");
        coverAge = dataBundle.getString("age");
        Bitmap bmp = (Bitmap) dataBundle.getParcelable("image");


        Toast.makeText(getApplicationContext(), coverTitle, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), coverAge, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), bmp.toString(), Toast.LENGTH_SHORT).show();

        titleTextView = (TextView) findViewById(R.id.tv_cardprofile_name);
        titleAgeView = (TextView) findViewById(R.id.tv_profile_age_card);
        coverImageView = (ImageView) findViewById(R.id.coverImageView);
        checkboxcontact = (CheckBox) findViewById(R.id.checkbox_contact);
        Phoneno = (TextView) findViewById(R.id.cardprofile_phoneno);
        PhonenoText = (TextView) findViewById(R.id.cardprofile_phonenotext);
        Email = (TextView) findViewById(R.id.cardprofile_email);
        EmailText = (TextView) findViewById(R.id.cardprofile_emailtext);

        checkboxcontact.setOnCheckedChangeListener(this);
        titleTextView.setText(coverTitle);
        titleAgeView.setText(coverAge);
        coverImageView.setImageBitmap(bmp);

        Phoneno.setVisibility(View.INVISIBLE);
        PhonenoText.setVisibility(View.INVISIBLE);
        Email.setVisibility(View.INVISIBLE);
        EmailText.setVisibility(View.INVISIBLE);

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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (checkboxcontact.isChecked()) {
            new UpdateNotification().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            add notification part

//            Phoneno.setVisibility(View.VISIBLE);
//            PhonenoText.setVisibility(View.VISIBLE);
//            Email.setVisibility(View.VISIBLE);
//            EmailText.setVisibility(View.VISIBLE);
        } else {
            NotificationBoolean = false;
            Phoneno.setVisibility(View.INVISIBLE);
            PhonenoText.setVisibility(View.INVISIBLE);
            Email.setVisibility(View.INVISIBLE);
            EmailText.setVisibility(View.INVISIBLE);
        }
    }

    private class UpdateNotification extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                NotificationBoolean = true;
                mongoDB.UpdateTheTutorNOtification(coverTitle, "true");
                System.out.println("THERE1");

//                if (mongoDB.CheckTutorNotification(coverTitle, NotificationBoolean).equals("sent") && (Tutors.LoginUser.equals(coverTitle))) {
//                    System.out.println("THERE");
//                    System.out.println("LoginUser is :" + Tutors.LoginUser);
//                    NotificationGenerator("Wants to connect", "Hey I want to contact");
//                    mongoDB.UpdateTheTutorNOtification(coverTitle, "false");
//
//                } else
//                    System.out.println("NOT THERE");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
