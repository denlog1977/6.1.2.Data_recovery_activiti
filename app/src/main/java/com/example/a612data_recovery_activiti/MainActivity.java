package com.example.a612data_recovery_activiti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity {

    private SharedPreferences mySharedPref;
    private static String LARGE_TEXT = "";
    private static String textFromSharedPref = "";
    private List<Map<String, String>> content;
    private SimpleAdapter listContentAdapter;
    private SwipeRefreshLayout swipeLayout;
    private ListView listView;
    private ArrayList<Integer> removedListItems = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        getTextFromSharedPref();

        if (textFromSharedPref.isEmpty()) {
            textFromSharedPref = getString(R.string.large_text);
            SharedPreferences.Editor myEditor = mySharedPref.edit();
            myEditor.putString(LARGE_TEXT, textFromSharedPref);
            myEditor.apply();
            Toast.makeText(this, "string.large_text сохранен в LARGE_TEXT SharedPreferences", Toast.LENGTH_SHORT).show();
        }


        listView = findViewById(R.id.listView);

        content = prepareContent();
        listContentAdapter = createAdapter(content);
        listView.setAdapter(listContentAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                content.remove(position);
                listContentAdapter.notifyDataSetChanged();
                removedListItems.add(position);
            }
        });


        swipeLayout = findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                removedListItems = new ArrayList<>();
                getTextFromSharedPref();
                updateList();
                listContentAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });

    }

    private void updateList(){
        content.clear();
        content.addAll(prepareContent());
    }


    @NonNull
    private SimpleAdapter createAdapter(List<Map<String, String>> content) {
        return new SimpleAdapter(this, content, R.layout.listlayout, new String[]{"text","length"}, new int[]{R.id.textView, R.id.textView2});
    }

    @NonNull
    private List<Map<String, String>> prepareContent() {
        List<Map<String, String>> contentList = new ArrayList<>();
        String[] arrayContent = textFromSharedPref.split("\n\n");
        Map<String, String> mapForList;
        for (int i = 0 ; i < arrayContent.length ; i++){
            mapForList = new HashMap<>();
            mapForList.put("text",arrayContent[i]);
            mapForList.put("length",Integer.toString(arrayContent[i].length()));
            contentList.add(mapForList);
        };
        return contentList;
    }

    private void getTextFromSharedPref(){
        textFromSharedPref = mySharedPref.getString(LARGE_TEXT, "");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Toast.makeText(this, "onSaveInstanceState" + "  outState.putIntegerArrayList(removedListItems)", Toast.LENGTH_SHORT).show();
        Log.i("Lifecycle_DEN", "Сработало событие " + "onSaveInstanceState");
        outState.putIntegerArrayList("removedListItems",removedListItems);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Toast.makeText(this, "onRestoreInstanceState" + "  content.remove", Toast.LENGTH_SHORT).show();
        removedListItems = savedInstanceState.getIntegerArrayList("removedListItems");
        for (int i=0 ; i<removedListItems.size(); i++) {
            content.remove(removedListItems.get(i).intValue());
        }
    }



}
