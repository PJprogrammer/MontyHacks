package com.example.stanl.hackathonproject;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private ArrayList<SchoolClass> classArrayList;
    private SchoolClass unsortedClass;
    final Context thisContext = this;
    private static final String FILENAME = "BoardImage_";
    private static final int CONTENT_REQUEST = 1337;
    private File lastOutputImg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Classes");

        final ListView classList = (ListView) findViewById(R.id.myList);

        classArrayList = new ArrayList<>();
        classArrayList.add(unsortedClass = new SchoolClass("Unsorted", new Time(), new Time()) {
            @Override
            public String toString() {
                return getName();
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////
        try {
            File[] classFiles = getExternalFilesDir(Environment.DIRECTORY_DCIM).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".ser");
                }
            });
            if (null != classFiles) {
                for (File f : classFiles) {
                    FileInputStream fIS = new FileInputStream(f.getAbsoluteFile());
                    ObjectInputStream oIS = new ObjectInputStream(fIS);
                    SchoolClass schoolClass = (SchoolClass) oIS.readObject();
                    classArrayList.add(schoolClass);
                }
            }
        } catch (IOException | ClassNotFoundException except) {
            System.out.println("/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
            System.out.println("Exception: " + except.getMessage());
            except.printStackTrace();
            System.out.println("/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

        //class adding stuff
        FloatingActionButton addClassBtn = (FloatingActionButton) findViewById(R.id.fab);
        addClassBtn.setImageResource(R.drawable.ic_add_black_18dp);
        addClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog newClassDlg = new Dialog(thisContext);
                newClassDlg.setContentView(R.layout.new_class_dialog);

                //get class name
                final EditText classNameField = (EditText) newClassDlg.findViewById(R.id.classNameField);

                final Time startTime = new Time();
                final Time endTime = new Time();

                Button timePickBtn = (Button) newClassDlg.findViewById(R.id.timePickBtn);
                timePickBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar currentTime = Calendar.getInstance();
                        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                        int currentMin = currentTime.get(Calendar.MINUTE);

                        //TODO: Default add 45 mins to start time
                        TimePickerDialog endTimePicker = new TimePickerDialog(thisContext,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        endTime.setHour(selectedHour);
                                        endTime.setMin(selectedMinute);
                                    }
                                }, currentHour, currentMin, false);
                        endTimePicker.setTitle("Select End Time");
                        endTimePicker.show();

                        TimePickerDialog startTimePicker = new TimePickerDialog(thisContext,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        startTime.setHour(selectedHour);
                                        startTime.setMin(selectedMinute);
                                    }
                                }, currentHour, currentMin, false);
                        startTimePicker.setTitle("Select Start Time");
                        startTimePicker.show();
                    }
                });

                Button okBtn = (Button) newClassDlg.findViewById(R.id.okBtn);
                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newClassDlg.dismiss();
                        String name = classNameField.getText().toString();
                        if (!name.equals("") && 0 > startTime.compareTo(endTime)) {
                            //TODO: Check times for overlap
                            boolean classAlreadyExists = false;
                            for (SchoolClass schoolClass : classArrayList) {
                                if (schoolClass.getName().equals(name)) {
                                    classAlreadyExists = true;
                                }
                            }
                            if (!classAlreadyExists) {
                                SchoolClass schoolClass = new SchoolClass(name, startTime, endTime);
                                classArrayList.add(schoolClass);
                                try {
                                    File objFile = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM) +
                                            File.separator + schoolClass.getName() + ".ser");
                                    FileOutputStream fOS = new FileOutputStream(objFile);
                                    ObjectOutputStream oOs = new ObjectOutputStream(fOS);
                                    oOs.writeObject(schoolClass);
                                    oOs.close();
                                    fOS.close();
                                } catch (IOException except) {
                                    System.out.println("/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
                                    System.out.println("Exception: " + except.getMessage());
                                    except.printStackTrace();
                                    System.out.println("/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
                                }
                            }
                        }
                    }
                });

                newClassDlg.show();

            }
        });

        //camera stuff
        FloatingActionButton camBtn = (FloatingActionButton) findViewById(R.id.cameraBtn);
        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Figure out time; based on the time, figure out class and get name

                //mkdir for that class

                Calendar currTime = Calendar.getInstance();
                int currentHour = currTime.get(Calendar.HOUR_OF_DAY);
                int currentMin = currTime.get(Calendar.MINUTE);
                Time currentTime = new Time(currentHour, currentMin);

                SchoolClass currentClass = null;
                for (int i = 0; i < classArrayList.size(); ++i) {
                    if (0 > classArrayList.get(i).getStartTime().compareTo(currentTime) &&
                            0 < classArrayList.get(i).getEndTime().compareTo(currentTime)) {
                        currentClass = classArrayList.get(i);
                    }
                }

                Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File classDir = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM) +
                        File.separator +
                        (null == currentClass ? unsortedClass.getName() : currentClass.getName()));

                classDir.mkdirs();

                lastOutputImg = new File(classDir, FILENAME + System.currentTimeMillis() + ".jpeg");
                camIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(lastOutputImg));

                startActivityForResult(camIntent, CONTENT_REQUEST);
            }
        });

        classList.setAdapter(new ArrayAdapter<>(this, R.layout.activity_listview, classArrayList));

        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Show list of files in directory for class
                //show image when one is clicked

                File classDir = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM) +
                        File.separator + classArrayList.get(position).getName());
                Intent imageListIntent = new Intent(thisContext, ImageListActivity.class);
                imageListIntent.putExtra("classname", classArrayList.get(position).getName());
                imageListIntent.putExtra("classImages", classDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jpeg");
                    }
                }));

                startActivity(imageListIntent);

                //Create ArrayList of files in the classDir
                //Create new activity with list of files
                //When an item in that list is tapped, open the selected image
            }
        });
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent dispIntent = new Intent(Intent.ACTION_VIEW);
                dispIntent.setDataAndType(Uri.fromFile(lastOutputImg), "image/jpeg");
                startActivity(dispIntent);
            }
        }
    }
}
