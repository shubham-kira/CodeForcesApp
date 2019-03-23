package com.codeforces.shubham.codeforces;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://codeforces.com/api/";
    private EditText handle;
    private Button submit, save;
    private String name;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        handle = (EditText) findViewById(R.id.title);
        submit = (Button) findViewById(R.id.submit);
        save = (Button) findViewById(R.id.save);
        progressBar = (ProgressBar) findViewById(R.id.progress_circular);

        save.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        handle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(handle.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
                progressBar.setVisibility(View.VISIBLE);
                name = handle.getText().toString();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .build();
                Api api = retrofit.create(Api.class);
                Call<ResponseBody> call = api.getContests(name);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            String data = response.body().string();
                            ArrayList<Contest> contestList = new ArrayList<>();
                            try {
                                JSONObject basejsonResponse = new JSONObject(data);
                                JSONArray res = basejsonResponse.getJSONArray("result");
                                for (int i = 0; i < res.length(); i++) {
                                    Contest contest = new Contest();
                                    contest.setContestName(res.getJSONObject(i).getString("contestName"));
                                    contest.setRank(Integer.parseInt(res.getJSONObject(i).getString("rank")));
                                    contest.setOldRating(Integer.parseInt(res.getJSONObject(i).getString("oldRating")));
                                    contest.setNewRating(Integer.parseInt(res.getJSONObject(i).getString("newRating")));
                                    contest.setChange(contest.getNewRating() - contest.getOldRating());
                                    contestList.add(contest);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            contestList = reverse(contestList);
                            RecyclerView recyclerView = findViewById(R.id.contestsAppeared);
                            ContestsAppearedAdapter adapter = new ContestsAppearedAdapter(MainActivity.this, contestList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            submit.setVisibility(View.GONE);
                            save.setVisibility(View.VISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });
    }

    static ArrayList<Contest> reverse(ArrayList<Contest> contestList) {
        ArrayList<Contest> newList = new ArrayList<>();
        for (int i = contestList.size() - 1; i >= 0; i--)
            newList.add(contestList.get(i));
        return newList;
    }

    public interface Api {
        @GET("user.rating")
        Call<ResponseBody> getContests(
                @Query("handle") String handle
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.fav) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
            builderSingle.setTitle("Select Handle:");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_item);
            //attach data to adapter here
            arrayAdapter.add("shubham__");

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String name = arrayAdapter.getItem(i);
                    handle.setText(name);
                    submit.performClick();
                }
            });

            builderSingle.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
