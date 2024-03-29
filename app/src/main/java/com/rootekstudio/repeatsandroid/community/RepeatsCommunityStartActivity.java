package com.rootekstudio.repeatsandroid.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.UIHelper;

import java.util.ArrayList;
import java.util.Objects;

public class RepeatsCommunityStartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    ArrayList<String> resultNames;
    ArrayList<QueryDocumentSnapshot> documents;
    FirebaseFirestore db;
    ProgressBar progressBar;
    LinearLayout linearSearchInfo;
    TextView searchInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIHelper.DarkTheme(this, false);
        setContentView(R.layout.activity_repeats_community_start);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBarSearchC);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.resultsRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        linearSearchInfo = findViewById(R.id.linearSearchInfo);
        searchInfoText = findViewById(R.id.searchInfoText);

        final EditText search = findViewById(R.id.searchRC);
        search.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_SEARCH) {
                String queryText = search.getText().toString();
                if (!queryText.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    linearSearchInfo.setVisibility(View.GONE);

                    search(queryText);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    Objects.requireNonNull(getCurrentFocus()).clearFocus();

                    return true;
                }
            }
            return false;
        });
    }

    void search(String text) {
        text = text + " ";
        ArrayList<String> queries = new ArrayList<>();

        outerloop:
        while (text.contains(" ")) {
            String tag = text.substring(0, text.indexOf(" "));
            while (tag.equals("")) {
                text = text.replaceFirst(" ", "");
                if (text.length() == 0) {
                    break outerloop;
                }
                tag = text.substring(0, text.indexOf(" "));
            }

            text = text.replace(tag + " ", "");
            String upper = tag.substring(0, 1).toUpperCase() + tag.substring(1);
            queries.add(upper);
            String lower = tag.substring(0, 1).toLowerCase() + tag.substring(1);
            queries.add(lower);
        }

        if (queries.size() > 10) {
            Toast.makeText(this, R.string.upTo5Words, Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            linearSearchInfo.setVisibility(View.VISIBLE);
            searchInfoText.setText(R.string.nothingFound);
            return;
        }

        if (queries.size() == 0) {
            progressBar.setVisibility(View.GONE);
            linearSearchInfo.setVisibility(View.VISIBLE);
            searchInfoText.setText(R.string.nothingFound);
            return;
        }

        if(RepeatsHelper.isOnline(this)) {
            db.collection("sets")
                    .whereArrayContainsAny("tags", queries)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            documents = new ArrayList<>();
                            resultNames = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String a = (String) document.get("availability");
                                if (a.equals("PUBLIC")) {
                                    documents.add(document);
                                    resultNames.add(document.get("displayName").toString());
                                }
                            }

                            mAdapter = new RCmainListAdapter(resultNames, 0);
                            mAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(mAdapter);

                            if (documents.size() == 0) {
                                linearSearchInfo.setVisibility(View.VISIBLE);
                                searchInfoText.setText(R.string.nothingFound);
                            }

                            progressBar.setVisibility(View.GONE);
                        } else {
                            Objects.requireNonNull(task.getException()).printStackTrace();
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.problem_connecting_to_db), Toast.LENGTH_LONG).show();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repeats_community_menu, menu);
        return true;
    }

    public void searchResultClick(View view) {
        int tag = (Integer) view.findViewById(R.id.setNameListItemRC).getTag();
        CommunityHelper.getSetAndStartPreviewActivity(tag, this, documents);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.yourSetsOption) {
            Intent intent = new Intent(this, MySetsActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
