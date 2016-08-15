package com.exception.findatutor.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;


public class FullStudentProfileActivityOnNotification extends Activity {

    private MongoDB mongoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mongoDB = MongoDB.getInstance();
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

}
