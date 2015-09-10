package com.caece.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.parse.Parse;
import com.parse.ParseObject;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_DRINK_MENU = 1;
    //
    private EditText inputText;
    private CheckBox hide;
    private ListView history; //realbody
    private Spinner storeInfo;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private String drinkMenuResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // initialize
        setContentView(R.layout.activity_main);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "z10MwinJq8BEBMdsX4VLIqlNmwnB4uXSEW3KKEsD", "yWIk3us2tuffEGFModKzZPBPqjmwJflDGRTH0T2Y");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);//read
        editor =sp.edit(); //write

        inputText = (EditText) findViewById(R.id.editText); //cast object->{...}->view->{....}->EDittext
        //inputText.setText("hello world");
        inputText.setOnKeyListener(new View.OnKeyListener() { //new a class interface
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                editor.putString("input", inputText.getText().toString());
                editor.commit();

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    submit(v);
                    return true; // no next line
                }
                return false;
            }
        });//method

        inputText.setText(sp.getString("input", ""));

        hide = (CheckBox) findViewById(R.id.checkBox);
        hide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hide", isChecked);
                editor.commit();
            }
        });
        hide.setChecked(sp.getBoolean("hide", false));
        history = (ListView) findViewById(R.id.history);
        storeInfo = (Spinner) findViewById(R.id.spinner);

        loadHistory();
        loadStoreInfo();;
    }

    //lv3
    //lv2
    //lv1

    private  void loadStoreInfo(){
        String[] data =getResources().getStringArray(R.array.store_info); // {"台大店","師大店","西門店"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,data);
        storeInfo.setAdapter(adapter);

    }

    private void loadHistory(){
        String result = Utils.readFile(this, "history.txt");
        //TextView history = (TextView) findViewById(R.id.history);
        //history.setText(result);
        String[] rawData = result.split("\n");

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (int i = 0; i < rawData.length; i++) {
            try {
                JSONObject object = new JSONObject(rawData[i]);
                String note = object.getString("note");
                String storeInfo = object.getString("store_info");
                JSONArray menu = object.getJSONArray("menu");

                Map<String, String> item = new HashMap<String, String>();
                item.put("note", note);//map key
                item.put("store_info", storeInfo);
                item.put("sum", "5");

                data.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String[] from = new String[]{"note", "store_info", "sum"}; //map key-> layout id
        int[] to = new int[] {R.id.note, R.id.store_info, R.id.sum};//in order
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.listview_item, from, to);

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data); // make a adapter
        history.setAdapter(adapter);//adapter


    }

    private JSONObject pack(){
        try{
            JSONObject object = new JSONObject();
            object.put("note", inputText.getText().toString());
            object.put("store_info", (String) storeInfo.getSelectedItem());
            if (drinkMenuResult != null){
                object.put("menu", new JSONArray(drinkMenuResult));
                return object;
            }
            //object.put("menu", new JSONArray(drinkMenuResult));
            return object;
        }catch ( JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    public void submit (View view) { //1.must be public 2. only one mariable ,type:view
        String text =  inputText.getText().toString();
        if (hide.isChecked()){
            text="*******";
        }
        //inputText.setText("");
        if (!text.isEmpty()) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();//this is the activity
        }

        Utils.writeFile(this,"history.txt",pack().toString()+"\n");
        loadHistory();

        inputText.setText("");
        drinkMenuResult = null;

    }

    public void toDrinkMenu (View view){

        String storeInfoString = (String) storeInfo.getSelectedItem();
        Intent intent = new Intent();//jump? opento?
        intent.setClass(this, DrinkMenuActivity.class);
        intent.putExtra("store_info", storeInfoString);
        startActivityForResult(intent,REQUEST_DRINK_MENU);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // requestcode:know which method back
        if (requestCode == REQUEST_DRINK_MENU)
            if(resultCode == RESULT_OK){
                drinkMenuResult = data.getStringExtra("result");
                Log.d("debug", drinkMenuResult);
            }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
