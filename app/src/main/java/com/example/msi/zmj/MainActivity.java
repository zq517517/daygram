package com.example.msi.zmj;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private ListView list;
    private List<Data> ListData;
    private List<Data> listOfYear = new ArrayList<>();
    private List<Data> listOfMonth = new ArrayList<>();
    private List<Data> data = new ArrayList<>();
    private Map<String,String> mEngMonth = new HashMap<String,String>(){{
        put("1","JANUARY");put("2","FEBRUARY");put("3","MARCH");put("4","APRIL");put("5","MAY");put("6","JUNE");
        put("7","JULY");put("8","AUGUST");put("9","SEPTEMBER");put("10","OCTOBER");put("11","NOVEMBER");put("12","DECEMBER");
    }};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
        if (preferences.getBoolean("firststart", true)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firststart", false);
            editor.commit();
            try{
                AllData allData = (AllData)getApplication();
                allData.setData(init());
                saveObject("daygram",allData.getData());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);

        final AllData allData = (AllData)getApplication();
        allData.setData((List<Data>)getObject("daygram"));
        ListData = allData.getData();

        final Spinner spinner = (Spinner)findViewById(R.id.year);
        spinner.getSelectedItem();
        spinner.setSelection(6);
        final Spinner spinner2 = (Spinner)findViewById(R.id.month);
        spinner2.getSelectedItem();
        spinner2.setSelection(8);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String year = MainActivity.this.getResources().getStringArray(R.array.ctype0)[position];
                String selectMonth = spinner2.getSelectedItem().toString();
                refresh(year,selectMonth);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String month = MainActivity.this.getResources().getStringArray(R.array.ctype1)[position];
                String selectYear = spinner.getSelectedItem().toString();
                refresh(selectYear,month);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ImageView addDiary = (ImageView) findViewById(R.id.add);
        addDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String time[] = new String[5];
                final Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                time[0] = String.valueOf(c.get(Calendar.YEAR));
                time[1] = mEngMonth.get(String.valueOf(c.get(Calendar.MONTH) + 1));
                time[2] = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                time[3] = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
                Date date = c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String str = sdf.format(date);
                String detail = "";
                time[4] = str;
                for(int i = 0;i < ListData.size();i++){
                    if(ListData.get(i).getDate().equals(str)){
                        detail = ListData.get(i).getDetail();
                        break;
                    }
                }
                if("1".equals(time[3])){
                    time[3] ="SUNDAY";
                }else if("2".equals(time[3])){
                    time[3] ="MONDAY";
                }else if("3".equals(time[3])){
                    time[3] ="TUESDAY";
                }else if("4".equals(time[3])){
                    time[3] ="WEDNESDAY";
                }else if("5".equals(time[3])){
                    time[3] ="THURSDAY";
                }else if("6".equals(time[3])){
                    time[3] ="FRIDAY";
                }else if("7".equals(time[3])){
                    time[3] ="SATURDAY";
                }
                intent.putExtra("signal","add");
                intent.putExtra("timeAtPresent", time);
                intent.putExtra("detail",detail);
                intent.setClass(MainActivity.this, DisplayActivity.class);
                startActivity(intent);
            }
        });
    }

    private void refresh(String year,String month){
        int num1 = ListData.size();
        for(int i = 0;i < num1;i++){
            if(ListData.get(i).getYear().equals(year)){
                listOfYear.add(ListData.get(i));
                if((i < num1-1)&&(!ListData.get(i+1).getYear().equals(year))){
                    break;
                }
            }
        }
        listOfMonth.clear();
        int num2 = listOfYear.size();
        for(int i = 0;i < num2;i++){
            if(listOfYear.get(i).getMonth().equals(month)){
                listOfMonth.add(listOfYear.get(i));
                if((i < num2-1)&&(!listOfYear.get(i).getMonth().equals(month))){
                    break;
                }
            }
        }
        try{
            ABAdapter adapter = new ABAdapter(this,(ArrayList<Data>)listOfMonth);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra("signal","edit");
                    intent.putExtra("data", listOfMonth.get(position));
                    String time[] = new String[5];
                    time[0] = listOfMonth.get(position).getYear();
                    time[1] = listOfMonth.get(position).getMonth();
                    time[2] = listOfMonth.get(position).getDay();
                    time[3] = listOfMonth.get(position).getWeek();
                    time[4] = listOfMonth.get(position).getDate();

                    intent.putExtra("timeAtPresent", time);
                    intent.setClass(MainActivity.this,DisplayActivity.class);
                    startActivity(intent);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveObject(String name,List<Data> data) {
        FileOutputStream fos ;
        ObjectOutputStream oos ;
        try {
            fos = this.openFileOutput(name, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getObject(String name) {
        FileInputStream fis;
        ObjectInputStream ois;
        try {
            fis = this.openFileInput(name);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Data> init(){
        Date date;
        Calendar c = Calendar.getInstance();
        Map<String,String> mEngMonth = new HashMap<String,String>(){{
            put("1","JANUARY");put("2","FEBRUARY");put("3","MARCH");put("4","APRIL");put("5","MAY");put("6","JUNE");
            put("7","JULY");put("8","AUGUST");put("9","SEPTEMBER");put("10","OCTOBER");put("11","NOVEMBER");put("12","DECEMBER");
        }};
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dBegin = null;
        Date dEnd = null;
        try{
            dBegin = sdf.parse("2000-01-01");
            dEnd = sdf.parse("2016-12-31");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        List<Date> listDate = getDatesBetweenTwoDate(dBegin, dEnd);
        for (Date aListDate : listDate) {
            date = aListDate;
            c.setTime(date);
            Data temp = new Data();
            temp.setDate(sdf.format(date));
            temp.setYear(c.get(Calendar.YEAR) + "");
            temp.setMonth(mEngMonth.get((c.get(Calendar.MONTH) + 1) + ""));
            temp.setDay(c.get(Calendar.DATE) + "");
            temp.setWeek(getWeekOfDate(date));
            temp.setDetail("");
            data.add(temp);
        }
        return data;
    }
    private static List<Date> getDatesBetweenTwoDate(Date beginDate, Date endDate) {
        List<Date> Date = new ArrayList<>();
        Date.add(beginDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginDate);
        while (true) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            if (endDate.after(cal.getTime())) {
                Date.add(cal.getTime());
            }
            else {
                break;
            }
        }
        Date.add(endDate);
        return Date;
    }
    private static String getWeekOfDate(Date date) {
        String[] weekDaysName = {"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return weekDaysName[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }
}
