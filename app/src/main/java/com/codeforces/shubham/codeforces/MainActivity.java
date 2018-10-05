package com.codeforces.shubham.codeforces;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://codeforces.com/api/user.rating?handle=shubham190shubham";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri uri = Uri.parse(API_URL);
        new RatingAsync(MainActivity.this).execute(uri.buildUpon().toString());
    }
}
