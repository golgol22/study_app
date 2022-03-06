package com.example.hjy.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dhfl9 on 2018-06-12.
 */

public class List extends Activity{

    ListView listView;
    DB database;
    SQLiteDatabase mydb;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    EditText edt1,edtUpdate;
    TextView txtUpdate, txtDate, per;
    Button btnInsert;
    Calendar cal;
    View dialogView;
    ProgressBar prog;
    Double value=0.0;
    String check1;
    String day;

    int listsize;
    double size, checksize;
    SparseBooleanArray sbArray;
    CheckBox ch;
    int j;
    String abc;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        per =(TextView) findViewById(R.id.per);
        listView = (ListView) findViewById(R.id.listView);
        edt1 = (EditText) findViewById(R.id.edt1);
        edtUpdate = (EditText) findViewById(R.id.edtUpdate);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtUpdate = (TextView) findViewById(R.id.txtUpdate);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        prog = (ProgressBar)findViewById(R.id.prog);
        ch= new CheckBox(this);

        Intent intent = getIntent();
        day = intent.getStringExtra("day");
        txtDate.setText(day+" 계획");

        cal = Calendar.getInstance();
        database = new DB(this,"calList.db",null,1);
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);



        mydb = database.getWritableDatabase();
        Cursor cr;
        String  strList="";
        list.clear();

        String sql = "select list, checked from calList where sdate='"+day+"';";
        cr = mydb.rawQuery(sql, null);

        int a=cr.getCount();


        for( j=0; j<a; j++){
            cr.moveToNext();
            strList=cr.getString(0);
            check1 = cr.getString(1);
            if(check1.equals("true")){
                list.add(strList);
                listView.performItemClick(listView, j, 0);
            }else{
                list.add(strList);
            }
        }

        listsize = list.size();
        sbArray= listView.getCheckedItemPositions();
        size = listView.getCheckedItemCount();
        checksize = sbArray.size();

        if(checksize!=0) {
            value =100*(size/listsize);
            abc = String.format("%.0f", Math.floor(value));
            prog.setProgress(Integer.parseInt(abc));
            per.setText("성취도: "+Integer.parseInt(abc)+"%");
        }


        adapter.notifyDataSetChanged();
        cr.close();
        mydb.close();


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu p = new PopupMenu(getApplicationContext(),view);
                MenuInflater min = getMenuInflater();
                Menu menu = p.getMenu();
                min.inflate(R.menu.menu,menu);

                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId()==R.id.item1){
                            dialogView = View.inflate(List.this,R.layout.dialog,null);
                            AlertDialog.Builder dig = new AlertDialog.Builder(List.this);
                            dig.setTitle("수정하기");
                            dig.setView(dialogView);
                            txtUpdate = (TextView) dialogView.findViewById(R.id.txtUpdate);
                            edtUpdate = (EditText) dialogView.findViewById(R.id.edtUpdate);
                            final String selectItem=(String)listView.getItemAtPosition(position); //수정전list
                            txtUpdate.setText(selectItem);//변경전

                            dig.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mydb = database.getWritableDatabase();
                                    String a = txtUpdate.getText().toString();
                                    String b = edtUpdate.getText().toString();
                                    String sql = "update calList set list=? where list=?;";
                                    Object[] args = {b,a};
                                    mydb.execSQL(sql, args);
                                    mydb.close();
                                    adapter.notifyDataSetChanged();
                                    txtUpdate.setText("");
                                    edtUpdate.setText("");
                                    list.set(position, b);
                                }
                            });
                            dig.setNegativeButton("취소",null);
                            dig.show();
                        }else{
                            mydb = database.getWritableDatabase();
                            String a=list.get(position);
                            String sql = "delete from calList where list='"+a+"';";
                            mydb.execSQL(sql);
                            list.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        return false;
                    }
                });
                p.show();
                return false;
            }
        });

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listitem = edt1.getText().toString();
                if( listitem.trim().equals("")){
                    Toast.makeText(List.this, "입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    list.add(listitem);
                    mydb = database.getWritableDatabase();
                    String sql = "insert into calList(sdate, list) values (?,?);";
                    Object[] args = {day, listitem};
                    mydb.execSQL(sql, args);

                    mydb.close();
                    edt1.setText("");
                    adapter.notifyDataSetChanged();
                }
                listsize = list.size();
                sbArray= listView.getCheckedItemPositions();
                size = listView.getCheckedItemCount();
                checksize = sbArray.size();

                if(checksize!=0) {
                    value =100*(size/listsize);
                     abc = String.format("%.0f", Math.floor(value));
                    prog.setProgress(Integer.parseInt(abc));
                    per.setText("성취도: "+Integer.parseInt(abc)+"%");
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                listsize = list.size();
                String [] item = new String [listsize];
                sbArray= listView.getCheckedItemPositions();
                size = listView.getCheckedItemCount();
                checksize = sbArray.size();

//                ListItem items = (ListItem) listView.getItemAtPosition(i);
//                String ab= items.tv;
                for(i=0; i<listsize; i++){
                    if(sbArray.get(i)){
                       item[i]=list.get(i);
                    }
                }

                mydb = database.getWritableDatabase();
                String sql1 = "update calList set checked='false' where sdate = '"+day+"';";
                mydb.execSQL(sql1);

                for(int j=0; j<item.length; j++ ) {
                    String sql2 = "update calList set checked='true' where list = '" + item[j] + "';";
                    mydb.execSQL(sql2);
                }

                if(checksize!=0) {
                    value =100*(size/listsize);
                    abc = String.format("%.0f", Math.floor(value));
                    prog.setProgress(Integer.parseInt(abc));
                    per.setText("성취도: "+Integer.parseInt(abc)+"%");
                }

                String sql = "update calList set per ='"+value+"' where sdate='"+day+"'";
                mydb.execSQL(sql);



                adapter.notifyDataSetChanged();
            }
        });
    }




}