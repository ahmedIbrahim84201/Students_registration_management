package com.example.students_registration_management;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    private EditText facultyBox,departmentBox,lecturerBox;
    private TextView msgBox;
    private ListView listView;
    private String path;
    private SQLiteDatabase database=null;
    private TabHost tabHost;
    //AHMED IBRAHIM: Trim between words doesn't work.
    //AHMED IBRAHIM: I am not sure if we use the same "Update","Cancel","Search","Register" for the REGISTRATION tab too or not.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        facultyBox=findViewById(R.id.facultyBox);
        departmentBox=findViewById(R.id.departmentBox);
        lecturerBox=findViewById(R.id.lecturerBox);
        listView=findViewById(R.id.listView);
        msgBox=findViewById(R.id.msgBox);

        //Managing tabs
        tabHost=findViewById(android.R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tabSpec;
        //Define the First (TAB)
        tabSpec= tabHost.newTabSpec("Tab1");
        tabSpec.setContent(R.id.administrationTab);
        tabSpec.setIndicator("Administrat-ion",null);
        tabHost.addTab(tabSpec);
        //Define the Second (TAB)
        tabSpec= tabHost.newTabSpec("Tab2");
        tabSpec.setContent(R.id.registration);
        tabSpec.setIndicator("Registration",null);
        tabHost.addTab(tabSpec);
        //Define the Third (TAB)
        tabSpec= tabHost.newTabSpec("Tab3");
        tabSpec.setContent(R.id.registeredStudents);
        tabSpec.setIndicator("Registered Students",null);
        tabHost.addTab(tabSpec);


        File myDbPath=getApplication().getFilesDir();
        path = myDbPath+"/"+"AdministrationDatabase";//Name of Database
        //Create Database For Administration
        try {
            if (!databaseExist()){
            database=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.CREATE_IF_NECESSARY);
            Toast.makeText(getApplication(),"Database is Created",Toast.LENGTH_LONG).show();
            String table="create table administration (admID integer PRIMARY KEY autoincrement, faculty text,department text,lecturer text);";
            // executing the SQL script
                database.execSQL(table);//Now we have the table
                Toast.makeText(getApplication(),"Table Created",Toast.LENGTH_LONG).show();
                //Inserting data into the table
                String faculty="Engineering and Natural Science";
                String department= "Computer Engineering";
                String lecturer="Murat Orhun";
                //Writing the insert script
                String input="insert into administration(faculty,department,lecturer) values ('"+faculty+"','"+department+"','"+lecturer+"')";
                database.execSQL(input);
                Toast.makeText(getApplication(),"Data Inserted",Toast.LENGTH_LONG).show();
                msgBox.setText("We Have Data In The Table");
            }else{
                Toast.makeText(getApplication(),"We Have A Database Already",Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            msgBox.setText(e.getMessage());
        }

    }
    // helper method to check if you have a database
    private boolean databaseExist(){
        File dbfile = new File(path);
        return dbfile.exists();
    }
    public void update(View V){
        //Opening the Database
        database=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.CREATE_IF_NECESSARY);
        String data ="select * from administration";
        Cursor cursor=database.rawQuery(data,null);
        ArrayList<String> administration =new ArrayList<>();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,administration);
        while(cursor.moveToNext()){
            String faculty =cursor.getString(cursor.getColumnIndexOrThrow("faculty"));
            String department =cursor.getString(cursor.getColumnIndexOrThrow("department"));
            String lecturer =cursor.getString(cursor.getColumnIndexOrThrow("lecturer"));
            String result="Faculty:"+faculty+"\nDepartment: "+department+"\nLecturer: "+lecturer;
            administration.add(result);
        }
        listView.setAdapter(adapter);
        database.close();
    }
    public void add(View V){
        try {
            database=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.CREATE_IF_NECESSARY);
            String faculty=facultyBox.getText().toString().toUpperCase().trim();
            String department=departmentBox.getText().toString().toUpperCase().trim();
            String lecturer=lecturerBox.getText().toString().toUpperCase().trim();
            String input="insert into administration (faculty, department, lecturer) values ('"+faculty+"','"+department+"','"+lecturer+"')";
            database.execSQL(input);
            Toast.makeText(getApplication(),"Data Inserted",Toast.LENGTH_LONG).show();
            facultyBox.setText("");
            departmentBox.setText("");
            lecturerBox.setText("");
            database.close();
        }catch (SQLiteException e){
            msgBox.setText(e.getMessage());
        }
    }
    public void delete(View V){
        try{
            database=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.CREATE_IF_NECESSARY);
            String faculty=facultyBox.getText().toString().toUpperCase().trim();
            String department=departmentBox.getText().toString().toUpperCase().trim();
            String lecturer=lecturerBox.getText().toString().toUpperCase().trim();
//            String remove = "DELETE FROM administration";
            String remove="delete from administration where faculty='"+faculty+"' AND  department='"+department+"' AND lecturer='"+lecturer+"'";
            database.execSQL(remove);
            Toast.makeText(getApplication(),lecturer +" from department:"+department+" in faculty:"+faculty+" has been deleted",Toast.LENGTH_LONG).show();
            facultyBox.setText("");
            departmentBox.setText("");
            lecturerBox.setText("");
            database.close();
        }catch (SQLiteException e){
            msgBox.setText(e.getMessage());
        }
    }
    public void search(View v) {
        try {
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            String faculty = facultyBox.getText().toString().toUpperCase().trim();
            String department = departmentBox.getText().toString().toUpperCase().trim();
            String lecturer = lecturerBox.getText().toString().toUpperCase().trim();

            String lookFor = "SELECT * FROM administration WHERE faculty='" + faculty + "' AND department='" + department + "' AND lecturer='" + lecturer + "'";
            Cursor cursor = database.rawQuery(lookFor, null);
            ArrayList<String> administration = new ArrayList<>();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, administration);
            while (cursor.moveToNext()) {
                String facultyCol = cursor.getString(cursor.getColumnIndexOrThrow("faculty"));
                String departmentCol = cursor.getString(cursor.getColumnIndexOrThrow("department"));
                String lecturerCol = cursor.getString(cursor.getColumnIndexOrThrow("lecturer"));
                String result = "Faculty:" + facultyCol + "\nDepartment: " + departmentCol + "\nLecturer: " + lecturerCol;
                administration.add(result);
            }
            listView.setAdapter(adapter);
            database.close();
        } catch (SQLiteException e) {
            msgBox.setText(e.getMessage());
        }
    }
}