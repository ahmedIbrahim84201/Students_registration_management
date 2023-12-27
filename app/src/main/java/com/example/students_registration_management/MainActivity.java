package com.example.students_registration_management;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private TextView facultyTextView,departmentTextView,lecturerTextView;
    private EditText facultyBox,departmentBox,lecturerBox; //Might be problems with facultyBox becauseAhmed Doesn't really get it
    private  EditText msgBox;
    private Button addBtnAdmTab,deleteBtnAdmTab,updateBtnAdmTab,searchBtnAdmTab;
    private ListView listView;
    private String path;
    private SQLiteDatabase database=null;
    //AHMED IBRAHIM: I need to add Read method to display values on the screen before carrying on
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        facultyBox=findViewById(R.id.facultyBox);
        departmentBox=findViewById(R.id.departmentBox);
        lecturerBox=findViewById(R.id.lecturerBox);
        listView=findViewById(R.id.listView);

        File myDbPath=getApplication().getFilesDir();
        path = myDbPath+"/"+"AdministrationDatabase";//Name of Database
        msgBox.setText(path);
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
}