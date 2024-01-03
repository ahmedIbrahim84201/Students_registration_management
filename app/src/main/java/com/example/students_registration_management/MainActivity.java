package com.example.students_registration_management;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private EditText facultyBox,departmentBox,lecturerBox,studentNameBox,studentLastNameBox;
    private TextView msgBox,msgBoxRegistration;
    private ListView listView,registrationListView,regStudentsListView;
    private String path;
    private RadioGroup radioGroup;
    private SQLiteDatabase database=null;
    private TabHost tabHost;
    private RadioButton maleRadioButton,femaleRadioButton;
    private Spinner facultySpinner,departmentSpinner,advisorSpinner;
    private String SID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File myDbPath=getApplication().getFilesDir();
        path = myDbPath+"/"+"AdministrationDatabase";//Name of Database
        //Connecting java Variables with XML files IDs
        facultyBox=findViewById(R.id.facultyBox);
        departmentBox=findViewById(R.id.departmentBox);
        lecturerBox=findViewById(R.id.lecturerBox);
        studentNameBox=findViewById(R.id.studentNameBox);
        studentLastNameBox=findViewById(R.id.studentLastNameBox);
        listView=findViewById(R.id.listView);
        msgBox=findViewById(R.id.msgBox);
        radioGroup=findViewById(R.id.radioGroup);
        msgBoxRegistration=findViewById(R.id.msgBoxRegistration);
        facultySpinner=findViewById(R.id.facultySpinner);
        departmentSpinner=findViewById(R.id.departmentSpinner);
        advisorSpinner=findViewById(R.id.advisorSpinner);
        maleRadioButton=findViewById(R.id.maleRadioBtn);
        femaleRadioButton=findViewById(R.id.femaleRadioBtn);
        registrationListView=findViewById(R.id.registrationListView);
        regStudentsListView=findViewById(R.id.regStudentsListView);
        populateSpinners();


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

        // Adding the OnTabChangeListener
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                // Check if the tab changed to "Registered Students"
                if ("Tab3".equals(tabId)) {
                    // Call the method to show registered students
                    showRegistration(null);
                }

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked item data
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Parse the data to extract faculty, department, and lecturer
                String[] dataParts = selectedItem.split("\n");
                String faculty = dataParts[0].substring(dataParts[0].indexOf(":") + 1).trim();
                String department = dataParts[1].substring(dataParts[1].indexOf(":") + 1).trim();
                String lecturer = dataParts[2].substring(dataParts[2].indexOf(":") + 1).trim();

                // Fill the corresponding fields with the clicked item's data
                facultyBox.setText(faculty);
                departmentBox.setText(department);
                lecturerBox.setText(lecturer);
            }
        });

        registrationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked item data
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Parse the data to extract name, lastName, gender, faculty, department, and advisor
                String[] dataParts = selectedItem.split("\n");
                String name = dataParts[1].substring(dataParts[1].indexOf(":") + 1).trim();
                String lastName = dataParts[2].substring(dataParts[2].indexOf(":") + 1).trim();
                String gender = dataParts[3].substring(dataParts[3].indexOf(":") + 1).trim();
                String faculty = dataParts[4].substring(dataParts[4].indexOf(":") + 1).trim();
                String department = dataParts[5].substring(dataParts[5].indexOf(":") + 1).trim();
                String advisor = dataParts[6].substring(dataParts[6].indexOf(":") + 1).trim();
                String ID = dataParts[0].substring(dataParts[0].indexOf(":") + 1).trim();
                SID=ID;

                // Fill the corresponding fields with the clicked item's data
                studentNameBox.setText(name);
                studentLastNameBox.setText(lastName);

                // Set the correct radio button based on gender
                if (gender.equalsIgnoreCase("Male")) {
                    maleRadioButton.setChecked(true);
                } else if (gender.equalsIgnoreCase("Female")) {
                    femaleRadioButton.setChecked(true);
                }
                // Set the selected values in the spinners
                setSpinnerSelection(facultySpinner, faculty);
                setSpinnerSelection(departmentSpinner, department);
                setSpinnerSelection(advisorSpinner, advisor);
            }
        });
        regStudentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked item data
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Parse the data to extract student information
                String[] dataParts = selectedItem.split("\n");
                String studentId = dataParts[0].substring(dataParts[0].indexOf(":") + 1).trim();
                String name = dataParts[1].substring(dataParts[1].indexOf(":") + 1).trim();
                String lastName = dataParts[2].substring(dataParts[2].indexOf(":") + 1).trim();
                String gender = dataParts[3].substring(dataParts[3].indexOf(":") + 1).trim();
                String faculty = dataParts[4].substring(dataParts[4].indexOf(":") + 1).trim();
                String department = dataParts[5].substring(dataParts[5].indexOf(":") + 1).trim();
                String advisor = dataParts[6].substring(dataParts[6].indexOf(":") + 1).trim();
                // Display student information in a popup frame
                showStudentInfoPopup(studentId, name, lastName, gender, faculty, department, advisor);
            }
        });
        //try catch and its content to Create the Registration table
        try{
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            if(!tableExists(database,"registration")) {
                String registrationTable = "create table registration (studentID integer PRIMARY KEY autoincrement, name text, lastName text,gender text, faculty text, department text, advisor text);";
                database.execSQL(registrationTable);
                Toast.makeText(getApplication(), "Tables Created", Toast.LENGTH_LONG).show();
                msgBox.setText("Registration table created");
            }
        }catch (Exception e){
            msgBox.setText(e.getMessage());
        }
        //try catch and its content to Create the Administration table
        try {
            if(!tableExists(database,"administration")) {
                database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                Toast.makeText(getApplication(), "Database is Created", Toast.LENGTH_LONG).show();
                // Creating Administration table
                String administrationTable = "create table administration (admID integer PRIMARY KEY autoincrement, faculty text, department text, lecturer text);";
                database.execSQL(administrationTable);
//                 Creating Registration table
                msgBox.setText("Administration table created");
            }
        } catch (Exception e) {
            msgBox.setText(e.getMessage());
        }
    }


    public void show(View V){
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
            //Add method connects the input of the user with the database, so when the user gives variables the data gets inserted into the table
            database=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.CREATE_IF_NECESSARY);
            //toUpperCase to keep all data the same size, trim to remove extra spaces at the end of the text
            String faculty=facultyBox.getText().toString().toUpperCase().trim();
            String department=departmentBox.getText().toString().toUpperCase().trim();
            String lecturer=lecturerBox.getText().toString().toUpperCase().trim();
            if (faculty.isEmpty() || department.isEmpty() || lecturer.isEmpty() ) {
                Toast.makeText(getApplication(), "Please fill in all the fields", Toast.LENGTH_LONG).show();
            } else {
            String input="insert into administration (faculty, department, lecturer) values ('"+faculty+"','"+department+"','"+lecturer+"')";
            database.execSQL(input);
            Toast.makeText(getApplication(),"Data Inserted",Toast.LENGTH_LONG).show();
            //Setting text fields to empty when the user press on "ADD" button
            facultyBox.setText("");
            departmentBox.setText("");
            lecturerBox.setText("");
            database.close();
            }
        }catch (SQLiteException e){
            msgBox.setText(e.getMessage());
        }
        populateSpinners();
    }
    public void delete(View V){
        try{
            //Delete method connects the input of the user with the database, so when the user gives variables the data gets Deleted from the table
            database=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.CREATE_IF_NECESSARY);
            //toUpperCase to keep all data the same size, trim to remove extra spaces at the end of the text
            String faculty=facultyBox.getText().toString().toUpperCase().trim();
            String department=departmentBox.getText().toString().toUpperCase().trim();
            String lecturer=lecturerBox.getText().toString().toUpperCase().trim();
            String remove="delete from administration where faculty='"+faculty+"' AND  department='"+department+"' AND lecturer='"+lecturer+"'";
            database.execSQL(remove);
            Toast.makeText(getApplication(),lecturer +" from department:"+department+" in faculty:"+faculty+" has been deleted",Toast.LENGTH_LONG).show();
            //Setting text fields to empty when the user press on "Delete" button
            facultyBox.setText("");
            departmentBox.setText("");
            lecturerBox.setText("");
            database.close();
        }catch (SQLiteException e){
            msgBox.setText(e.getMessage());
        }
        populateSpinners();
    }
    public void search(View v) {
        try {
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            // Get the input data from your EditText fields
            String faculty = facultyBox.getText().toString().toUpperCase().trim();
            String department = departmentBox.getText().toString().toUpperCase().trim();
            String lecturer = lecturerBox.getText().toString().toUpperCase().trim();
            String lookFor = "SELECT * FROM administration WHERE faculty='" + faculty + "' AND department='" + department + "' OR lecturer='" + lecturer + "'";
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
    public void updateAdministration(View v) {
        try {
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);

            // Get the input data from your EditText fields
            String faculty = facultyBox.getText().toString().toUpperCase().trim();
            String department = departmentBox.getText().toString().toUpperCase().trim();
            String lecturer = lecturerBox.getText().toString().toUpperCase().trim();
            if (faculty.isEmpty() || department.isEmpty() || lecturer.isEmpty() ) {
                Toast.makeText(getApplication(), "Please fill in all the fields", Toast.LENGTH_LONG).show();
            } else {
            // Execute the update query based on your table structure and conditions
            String updateQuery = "UPDATE administration SET faculty='" + faculty + "', department='" + department + "' WHERE lecturer='" + lecturer + "'";
            database.execSQL(updateQuery);

            Toast.makeText(getApplication(), "Data Updated", Toast.LENGTH_LONG).show();
            facultyBox.setText("");
            departmentBox.setText("");
            lecturerBox.setText("");
            database.close();
            }
        } catch (SQLiteException e) {
            Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        populateSpinners(); // Update spinners after the data is modified
        show(null);

    }

    //Second Tab
    public void updateStudent(View v) {
        try {
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);

            String gender = "";
            int checkId = radioGroup.getCheckedRadioButtonId();
            //If statement to check if the user is a male or female
            if (checkId == R.id.maleRadioBtn) {
                gender = "Male";
            } else if (checkId == R.id.femaleRadioBtn) {
                gender = "Female";
            } else {
                Toast.makeText(getApplication(), "It's not funny, there are 2 genders, Pick one..", Toast.LENGTH_LONG).show();
                return; // Stop further execution if gender is not selected
            }

            // Get the input data from your EditText fields
            String name = studentNameBox.getText().toString().toUpperCase().trim();
            String lastName = studentLastNameBox.getText().toString().toUpperCase().trim();
            String faculty = facultySpinner.getSelectedItem().toString();
            String department = departmentSpinner.getSelectedItem().toString();
            String advisor = advisorSpinner.getSelectedItem().toString();

            if(name.isEmpty() || lastName.isEmpty() || faculty=="Faculty" || department=="Department" || advisor=="Advisor"){
                Toast.makeText(getApplication(), "You forgot one or more variable", Toast.LENGTH_LONG).show();
            }else{
            // Execute the update query based on your table structure and conditions
            String updateQuery = "UPDATE registration SET name='" + name + "', lastName='" + lastName + "', gender= '" + gender +"', faculty= '" + faculty + "', department= '" + department + "', advisor= '" + advisor + "'  WHERE studentID= '" + SID + "'";
            database.execSQL(updateQuery);

            Toast.makeText(getApplication(), "Data Updated", Toast.LENGTH_LONG).show();
            studentNameBox.setText("");
            studentLastNameBox.setText("");

            database.close();
            showRegistration(null);
            }
        } catch (SQLiteException e) {
            Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void showRegistration(View V){
        try {
            //Opening the Database
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            String data = "select * from registration";
            Cursor cursor = database.rawQuery(data, null);
            ArrayList<String> registration = new ArrayList<>();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, registration);
            while (cursor.moveToNext()) {
                String studentId = cursor.getString(cursor.getColumnIndexOrThrow("studentID"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName"));
                String faculty = cursor.getString(cursor.getColumnIndexOrThrow("faculty"));
                String department = cursor.getString(cursor.getColumnIndexOrThrow("department"));
                String advisor = cursor.getString(cursor.getColumnIndexOrThrow("advisor"));
                String result = "StudentID:" + studentId + "\nName: " + name + "\nLast Name: " + lastName + "\n Gender: " + gender + "\n Faculty: " + faculty + "\n Department: " + department + "\n Advisor: " + advisor;
                registration.add(result);

            }
            registrationListView.setAdapter(adapter);
            regStudentsListView.setAdapter(adapter);
            database.close();

        }
        catch(Exception e){
            Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    public void register(View V){
        //If statement for the gender to make sure one of the radio buttons are selected
        try {
            Random random = new Random();
            long studentID = 1000000000L + random.nextInt(900000000); // Generate a random 10-digit number
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);

            String gender = "";
            int checkId = radioGroup.getCheckedRadioButtonId();

            if (checkId == R.id.maleRadioBtn) {
                gender = "Male";
            } else if (checkId == R.id.femaleRadioBtn) {
                gender = "Female";
            } else {
                Toast.makeText(getApplication(), "It's not funny, there are 2 genders, Pick one..", Toast.LENGTH_LONG).show();
                return; // Stop further execution if gender is not selected
            }

            String name = studentNameBox.getText().toString().toUpperCase().trim();
            String lastName = studentLastNameBox.getText().toString().toUpperCase().trim();
            String faculty = facultySpinner.getSelectedItem().toString();
            String department = departmentSpinner.getSelectedItem().toString();
            String advisor = advisorSpinner.getSelectedItem().toString();
            //If statement to make sure all text fields are filled
            if(name.isEmpty() || lastName.isEmpty() || faculty=="Faculty" || department=="Department" || advisor=="Advisor"){
                Toast.makeText(getApplication(), "You forgot one or more variable", Toast.LENGTH_LONG).show();
            }else{
                String input = "insert into registration (studentID, name, lastName,gender,faculty,department,advisor) values ('" + studentID + "','" + name + "','" + lastName + "','" + gender + "','" + faculty + "','" + department + "','" + advisor + "')";
                database.execSQL(input);
                Toast.makeText(getApplication(), "Data Inserted", Toast.LENGTH_LONG).show();
                radioGroup.clearCheck();
                studentNameBox.setText("");
                studentLastNameBox.setText("");
                facultySpinner.setSelection(0);
                departmentSpinner.setSelection(0);
                advisorSpinner.setSelection(0);
                database.close();
            }

        } catch (SQLiteException e) {
            Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
public void cancel(View V) {
    try {
        database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);

        // Get the selected item's data
        String name = studentNameBox.getText().toString().toUpperCase().trim();
        String lastName = studentLastNameBox.getText().toString().toUpperCase().trim();

        // Check if the student is registered
        String checkIfExists = "SELECT * FROM registration WHERE name='" + name + "' AND lastName='" + lastName + "'";
        Cursor cursor = database.rawQuery(checkIfExists, null);

        if (cursor.moveToFirst()) {
            // Student is registered, proceed with deletion
            String deleteStudent = "DELETE FROM registration WHERE name='" + name + "' AND lastName='" + lastName + "'";
            database.execSQL(deleteStudent);

            Toast.makeText(getApplication(), "Registration for " + name + " " + lastName + " has been canceled", Toast.LENGTH_LONG).show();
        } else {
            // Student is not registered, show a message
            Toast.makeText(getApplication(), "Student is not registered", Toast.LENGTH_LONG).show();
        }

        // Clear input fields and spinners
        radioGroup.clearCheck();
        studentNameBox.setText("");
        studentLastNameBox.setText("");
        facultySpinner.setSelection(0);
        departmentSpinner.setSelection(0);
        advisorSpinner.setSelection(0);

        database.close();
    } catch (SQLiteException e) {
        Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();
    }
}

    public void searchStudent(View V) {
        try {
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);

            String name = studentNameBox.getText().toString().toUpperCase().trim();
            String lastName = studentLastNameBox.getText().toString().toUpperCase().trim();
            String lookFor = "SELECT * FROM registration WHERE name='" + name + "' AND lastName='" + lastName + "'";

            Cursor cursor = database.rawQuery(lookFor, null);
            ArrayList<String> registration = new ArrayList<>();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, registration);
            //Loop to print all of the variables in the table
            while (cursor.moveToNext()) {
                String studentIdCol = cursor.getString(cursor.getColumnIndexOrThrow("studentID")); // Corrected column name
                String nameCol = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String lastNameCol = cursor.getString(cursor.getColumnIndexOrThrow("lastName"));
                String genderCol = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
                String facultyCol = cursor.getString(cursor.getColumnIndexOrThrow("faculty"));
                String departmentCol = cursor.getString(cursor.getColumnIndexOrThrow("department"));
                String advisorCol = cursor.getString(cursor.getColumnIndexOrThrow("advisor"));
                String result = "StudentID:" + studentIdCol + "\nName:" + nameCol + "\nLast Name:" + lastNameCol + "\nGender:" + genderCol + "\nFaculty:" + facultyCol + "\nDepartment: " + departmentCol + "\nAdvisor: " + advisorCol;
                registration.add(result);
            }
            registrationListView.setAdapter(adapter);
            database.close();
        } catch (Exception e) {
            Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    //Third tab

    private void showStudentInfoPopup(String studentId, String name, String lastName, String gender, String faculty, String department, String advisor) {
        // Create a StringBuilder to build the message for the popup
        StringBuilder popupMessage = new StringBuilder();
        popupMessage.append("Student ID: ").append(studentId).append("\n");
        popupMessage.append("Name: ").append(name).append("\n");
        popupMessage.append("Last Name: ").append(lastName).append("\n");
        popupMessage.append("Gender: ").append(gender).append("\n");
        popupMessage.append("Faculty: ").append(faculty).append("\n");
        popupMessage.append("Department: ").append(department).append("\n");
        popupMessage.append("Advisor: ").append(advisor);

        // Show the popup message dialog
        showMessageDialog("Student Information", popupMessage.toString());
    }

    private void showMessageDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing or add any additional action if needed
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
//Helper Methods:

    private boolean databaseExist() {
        // Check if the database file exists at the specified path
        File dbfile = new File(path);
        return dbfile.exists();
    }

    private List<String> getDistinctColumnValues(String columnName) {
        List<String> values = new ArrayList<>();
        try {
            // Open the database
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);

            // Query distinct values from the specified column in the "administration" table
            Cursor cursor = database.rawQuery("SELECT DISTINCT " + columnName + " FROM administration", null);

            // Add a label based on the columnName
            String label;
            switch (columnName) {
                case "faculty":
                    label = "Faculty";
                    break;
                case "department":
                    label = "Department";
                    break;
                case "lecturer":
                    label = "Advisor";
                    break;
                default:
                    label = "Select one...";
            }
            values.add(label);

            // Iterate through the cursor and add distinct values to the list
            while (cursor.moveToNext()) {
                String value = cursor.getString(cursor.getColumnIndexOrThrow(columnName));
                values.add(value);
            }

            cursor.close();
            database.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return values;
    }

    private void populateSpinners() {
        // Retrieve distinct values for faculty, department, and advisor
        List<String> facultyValues = getDistinctColumnValues("faculty");
        List<String> departmentValues = getDistinctColumnValues("department");
        List<String> advisorValues = getDistinctColumnValues("lecturer");

        // Create ArrayAdapter and set it to the respective Spinners
        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, facultyValues);
        facultyAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        facultySpinner.setAdapter(facultyAdapter);

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentValues);
        departmentAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        departmentSpinner.setAdapter(departmentAdapter);

        ArrayAdapter<String> advisorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, advisorValues);
        advisorAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        advisorSpinner.setAdapter(advisorAdapter);
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        // Set the selection of the provided Spinner based on the specified value
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            if (position != -1) {
                spinner.setSelection(position);
            }
        }
    }

    private boolean tableExists(SQLiteDatabase db, String tableName) {
        // Check if the specified table exists in the database
        Cursor cursor = db.query("sqlite_master", new String[]{"name"}, "type='table' AND name=?", new String[]{tableName}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu from the XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void createDialog() {
        // Create an AlertDialog.Builder instance
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title and message for the dialog
        builder.setTitle("Purpose");
        builder.setMessage("The Purpose of this app is to add/delete/update new administrators and register/cancel/update students' registration");

        // Set up positive (OK) and negative (Cancel) buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the OK button click event
                Toast.makeText(MainActivity.this, "OK clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check if the selected menu item is "Show Dialog"
        if (item.getItemId() == R.id.menu_item) {
            // Call the method to create and show the dialog
            createDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


