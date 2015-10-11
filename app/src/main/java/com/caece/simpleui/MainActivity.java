package com.caece.simpleui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_DRINK_MENU = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    //
    private EditText inputText;
    private CheckBox hide;
    private ListView history; //realbody
    private Spinner storeInfo;
    private ImageView imageView;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private String drinkMenuResult;
    private List<ParseObject> orderResult;

    private boolean hasPhoto = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // initialize
        setContentView(R.layout.activity_main);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        //hw
         //Parse.initialize(this, "N5ytfpTopfdCCmIqCgZZk5PDjkUiGudm1UaygmOv", "TH9KM8xYSN4nFD7GIxGDrppNsXVN1exKrm0v6KuA");
        //my
        Parse.initialize(this, "z10MwinJq8BEBMdsX4VLIqlNmwnB4uXSEW3KKEsD", "yWIk3us2tuffEGFModKzZPBPqjmwJflDGRTH0T2Y");

        ParseObject HomeworkParse = new ParseObject("HomeworkParse");
        HomeworkParse.put("sid", "AND26017");
        HomeworkParse.put("email","xxxxxtsuki@gmail.com");
        HomeworkParse.saveInBackground();
        //testObject.saveInBackground();

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
                if (hide.isChecked()){
                imageView.setVisibility(View.INVISIBLE);}
                else{
                    imageView.setVisibility(View.VISIBLE);
                }
                editor.commit();
            }
        });

        hide.setChecked(sp.getBoolean("hide", false));
        history = (ListView) findViewById(R.id.history);
        history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //viwe = item (littel)
                goToOrderDetail(position);//can by view or id or positon


            }
        });

        storeInfo = (Spinner) findViewById(R.id.spinner);
        imageView = (ImageView)findViewById(R.id.imageView);

        loadHistory();
        loadStoreInfo();
    }

    //lv3
    //lv2
    //lv1

    private  void loadStoreInfo(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                String[] data = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    ParseObject object = list.get(i);
                    data[i] = object.getString("name") + " " + object.getString("address");
                }


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, data);
                storeInfo.setAdapter(adapter);
            }
        });
        //String[] data =getResources().getStringArray(R.array.store_info); // {"台大店","師大店","西門店"};


    }

    private void loadHistory() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    orderResult = list;
                    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                    for (int i = 0; i < list.size(); i++) {
                        ParseObject object = list.get(i);
                        String note = object.getString("note");
                        String storeInfo = object.getString("store_info");
                        //JSONArray menu = object.getJSONArray("menu");
                        JSONArray test = object.getJSONArray("menu");
                        try {
                            JSONObject jsonObject = test.getJSONObject(0);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }


                        JSONObject temp = new JSONObject();//menu.optJSONObject(0);


                        int sum = 0;


                        String total = sum+"";

                        Map<String, String> item = new HashMap<String, String>();
                        item.put("note", note);
                        item.put("store_info", storeInfo);
                        item.put("sum", total);

                        data.add(item);
                    }
                    String[] from = new String[]{"note", "store_info", "sum"};
                    int[] to = new int[]{R.id.note, R.id.store_info, R.id.sum};
                    SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,
                            data, R.layout.listview_item, from, to);

                    history.setAdapter(adapter);
                }
            }
        });

    }

    private void saveOrder(SaveCallback saveCallback){
        ParseObject object = new ParseObject("Order");
        object.put("note",inputText.getText().toString());
        object.put("store_info", (String) storeInfo.getSelectedItem());
        if (drinkMenuResult != null){
            try {
                object.put("menu", new JSONArray(drinkMenuResult));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (hasPhoto) {
            ParseFile file = new ParseFile("photo.png", Utils.uriToBytes(this, Utils.getPhotoUri()));
            object.put("photo", file);
                    }
        object.saveInBackground(saveCallback);//better than not in back
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

    private void goToOrderDetail(int position){
        ParseObject order = orderResult.get(position);

        Intent intent = new Intent();
        intent.setClass(this, OrderDetailActivity.class);
        intent.putExtra("note", order.getString("note"));
        intent.putExtra("store_info",order.getString("store_info"));
        intent.putExtra("menu", order.getJSONArray("menu").toString());
        startActivity(intent);
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

        //Utils.writeFile(this, "history.txt", pack().toString() + "\n");
        saveOrder(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                loadHistory();
            }
        });


        inputText.setText("");
        drinkMenuResult = null;
        hasPhoto = false;

    }

    public void toDrinkMenu (View view){

        String storeInfoString = (String) storeInfo.getSelectedItem();
        Intent intent = new Intent();//jump? opento?
        intent.setClass(this, DrinkMenuActivity.class);
        intent.putExtra("store_info", storeInfoString);
        startActivityForResult(intent,REQUEST_DRINK_MENU);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DRINK_MENU) {
            if (resultCode == RESULT_OK) {
                drinkMenuResult = data.getStringExtra("result");
                Log.d("debug", drinkMenuResult);
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                //Bitmap bm = data.getParcelableExtra("data");
                //imageView.setImageBitmap(bm);
                Uri uri = Utils.getPhotoUri();
                imageView.setImageURI(uri);
                hasPhoto=true;
            }
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
        if (id == R.id.action_take_photo) {
            //Toast.makeText(this,"take photo", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE); // find app to take shot
            intent.putExtra(MediaStore.EXTRA_OUTPUT,Utils.getPhotoUri());

            startActivityForResult(intent, REQUEST_TAKE_PHOTO); //call directly
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
