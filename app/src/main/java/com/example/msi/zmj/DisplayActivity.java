package com.example.msi.zmj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {
    private TextView time;
    private Button done;
    private EditText edit;
    private Data data = new Data();
    private List<Data> ListData = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_display);
        final AllData allData = (AllData)getApplication();
        ListData = allData.getData();
        edit = (EditText)findViewById(R.id.diary);
        edit.setFocusable(false);
        edit.setFocusableInTouchMode(false);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setFocusableInTouchMode(true);
                edit.setFocusable(true);
            }
        });

        String signal = (String)this.getIntent().getExtras().get("signal");
        String detail = "";
        if(signal.equals("add")){
            detail = (String)this.getIntent().getExtras().get("detail");
        }

        time = (TextView)findViewById(R.id.date);
        String timeAtPresent[] = (String[]) this.getIntent().getExtras().get("timeAtPresent");
        data.setYear(timeAtPresent[0]);
        data.setMonth(timeAtPresent[1]);
        data.setDay(timeAtPresent[2]);
        data.setWeek(timeAtPresent[3]);
        data.setDate(timeAtPresent[4]);
        data.setDetail(detail);
        edit.setText(data.getDetail());
        time.setText(Html.fromHtml(timeAtPresent[3]));
        time.append("/"+timeAtPresent[1]+" "+timeAtPresent[2]+"/"+timeAtPresent[0]);
        if(signal.equals("edit")){
            data = (Data)this.getIntent().getExtras().get("data");
            edit.setText(data.getDetail());
        }

        done = (Button)findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.setDetail(edit.getText().toString());
                for(int i = 0;i < ListData.size();i++){
                    if(ListData.get(i).getDate().equals(data.getDate())){
                        ListData.get(i).setDetail(data.getDetail());
                        allData.setData(ListData);
                        saveObject("daygram",allData.getData());
                        break;
                    }
                }
                Intent intent = new Intent();
                intent.setClass(DisplayActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView ret = (ImageView) findViewById(R.id.home);
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(DisplayActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void saveObject(String name,List<Data> data) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = this.openFileOutput(name, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
