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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_DRINK_MENU = 1;
    //
    private EditText inputText;
    private CheckBox hide;
    private ListView history; //realbody
    private Spinner storeInfo;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // initialize
        setContentView(R.layout.activity_main);

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
        String[] data = result.split("\n");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data); // make a adapter
        history.setAdapter(adapter);//adapter


    }


    public void submit (View view) { //1.must be public 2. only one mariable ,type:view
        String text =  inputText.getText().toString();
        if (hide.isChecked()){
            text="*******";
        }
        inputText.setText("");
        if (!text.isEmpty()) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();//this is the activity
        }

        Utils.writeFile(this,"history.txt",text+"\n");
        loadHistory();

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
                String result = data.getStringExtra("result");
                Log.d("debug", result);
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
