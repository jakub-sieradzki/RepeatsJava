package com.rootekstudio.repeatsandroid.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rootekstudio.repeatsandroid.JsonFile;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RepeatsListDB;
import com.rootekstudio.repeatsandroid.RepeatsSingleSetDB;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.notifications.ConstNotifiSetup;
import com.rootekstudio.repeatsandroid.notifications.NotificationHelper;
import com.rootekstudio.repeatsandroid.notifications.RegisterNotifications;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class AddEditSetActivity extends AppCompatActivity {

    //View that contains ImageView
    ViewParent imagePreview;
    Context context = null;
    DatabaseHelper DB;
    ViewGroup parent = null;
    boolean deleted = false;

    //Last index from database
    int lastIndex = 0;
    static boolean IsDark;

    //id of the table
    static String id;

    //region onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsDark = RepeatsHelper.DarkTheme(this, false);

        setContentView(R.layout.activity_repeats_add_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        //Getting basic information about set (if new or  already existing, id, and ignore chars option)
        Intent intent = getIntent();
        final String editing = intent.getStringExtra("ISEDIT");
        String SetName = intent.getStringExtra("NAME");

        //Project name edit text
        final EditText editName = findViewById(R.id.projectname);
        editName.setOnFocusChangeListener(newName);

        //Replacing default menu of the bottom app bar
        BottomAppBar bottomAppBar = findViewById(R.id.AddQuestionBar);

        if (!IsDark) {
            editName.setBackgroundResource(R.drawable.editname_light);
        }

        //Configuring bottom app bar menu click listener
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.share) {
                    resetFocus();
                    String name = editName.getText().toString();
                    name = name.trim();
                    if(name.equals("") || name.equals("text") || name.equals("Text") || name.equals("text text") || name.equals("text text text")) {
                        Toast.makeText(AddEditSetActivity.this, R.string.cantShareSet, Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent intentShare = new Intent(context, ShareActivity.class);
                        intentShare.putExtra("name", name);
                        intentShare.putExtra("id", id);
                        startActivity(intentShare);
                    }
                }
                else if(item.getItemId() == R.id.deleteSet) {
                    MaterialAlertDialogBuilder ALERTbuilder = new MaterialAlertDialogBuilder(context);
                    ALERTbuilder.setBackground(getDrawable(R.drawable.dialog_shape));

                    ALERTbuilder.setTitle(R.string.WantDelete);
                    ALERTbuilder.setNegativeButton(R.string.Cancel, null);

                    ALERTbuilder.setPositiveButton(R.string.Delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DeleteSet(id);
                            JsonFile.removeSetFromJSON(context, id);

                            List<RepeatsListDB> a = DB.AllItemsLIST();
                            int size = a.size();

                            //if there is no set left in database, turn off notifications
                            if (size == 0) {
                                ConstNotifiSetup.CancelNotifications(context);

                                try {
                                    JSONObject advancedFile = new JSONObject(JsonFile.readJson(context, "advancedDelivery.json"));

                                    Iterator<String> iterator = advancedFile.keys();

                                    while(iterator.hasNext()) {
                                        String key = iterator.next();
                                        NotificationHelper.cancelAdvancedAlarm(context, Integer.parseInt(key));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("ListNotifi", "0");
                                editor.apply();
                            }
                            deleted = true;
                            AddEditSetActivity.super.onBackPressed();
                        }
                    });
                    ALERTbuilder.show();
                }
                return false;
            }
        });

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");

        DB = new DatabaseHelper(context);

        parent = findViewById(R.id.AddRepeatsLinear);

        //if this is a new set
        if (editing.equals("FALSE")) {
            String date = s.format(new Date());
            id = "R" + date;

            SimpleDateFormat createD = new SimpleDateFormat("dd.MM.yyyy");
            String createDate = createD.format(new Date());

            RepeatsListDB list;
            if(Locale.getDefault().toString().equals("pl_PL")) {
                list = new RepeatsListDB("", id, createDate, "true", "", "false", "pl_PL", "en_GB");
            }
            else {
                list = new RepeatsListDB("", id, createDate, "true", "", "false", "en_US", "es_ES");
            }

            //Registering set in database
            DB.CreateSet(id);
            DB.AddName(list);

            addView(parent, context);

            //Adding single item (with question and answer) to database
            DB.AddItem(id);

            JsonFile.putSetToJSON(context, id);

        } else {
            id = editing;
            readSetFromDatabase(editing, SetName);
        }

        int firstRun = sharedPreferences.getInt("firstRunTerms", 3);
        if (firstRun != 3) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ListNotifi", String.valueOf(firstRun));
            editor.apply();

            if (firstRun == 1) {
                ConstNotifiSetup.RegisterNotifications(context, null, RepeatsHelper.staticFrequencyCode);
            } else if (firstRun == 2) {
                RegisterNotifications.registerAdvancedDelivery(context);
            }

            editor.putInt("firstRunTerms", 3);
            editor.apply();
        }

        FloatingActionButton fab = findViewById(R.id.AddQuestionFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addView(parent, context);
                DB.AddItem(id);
            }
        });
    }
    //endregion

    //region readSetFromDatabase
    void readSetFromDatabase(final String SetID, String NAME) {
        final LayoutInflater inflater = LayoutInflater.from(this);
        EditText editName = findViewById(R.id.projectname);
        editName.setText(NAME);

        Thread readFromDatabase = new Thread(new Runnable() {
            @Override
            public void run() {
                List<RepeatsSingleSetDB> SET = DB.AllItemsSET(SetID, -1);
                int ItemsCount = SET.size();

                for (int i = 0; i < ItemsCount; i++) {
                    RepeatsSingleSetDB Single = SET.get(i);
                    int ID = Single.getID();
                    String Question = Single.getQuestion();
                    String Answer = Single.getAnswer();
                    String Image = Single.getImag();

                    final View child = inflater.inflate(R.layout.addrepeatslistitem, parent, false);

                    RelativeLayout RL = child.findViewById(R.id.RelativeAddItem);
                    RL.setTag(ID);

                    EditText Q = child.findViewById(R.id.questionBox);
                    EditText A = child.findViewById(R.id.answerBox);
                    ImageButton B = child.findViewById(R.id.deleteItem);
                    final ImageButton I = child.findViewById(R.id.addImage);
                    ImageView img = child.findViewById(R.id.imageView);
                    ImageButton imgbut = child.findViewById(R.id.deleteImage);
                    ImageButton addAnswer = child.findViewById(R.id.addAnswerButton);


                    Q.setOnFocusChangeListener(newText);
                    A.setOnFocusChangeListener(newAnswer);
                    B.setOnClickListener(deleteClick);
                    I.setOnClickListener(addImageClick);
                    imgbut.setOnClickListener(deleteImageClick);
                    addAnswer.setOnClickListener(addAnswerClick);

                    A.setFilters(new InputFilter[]{inputFilter});

                    Q.setText(Question);

                    Scanner scanner = new Scanner(Answer);
                    int an = 0;

                    if (scanner.hasNextLine()) {
                        while (scanner.hasNextLine()) {
                            String nextLine = scanner.nextLine();
                            if (an != 0) {
                                addAnswer(RL.findViewById(R.id.LinearQA), nextLine);
                            } else {
                                A.setText(nextLine);
                                an++;
                            }
                        }
                    } else {
                        A.setText(Answer);
                    }

                    scanner.close();

                    if (!Image.equals("")) {
                        I.setEnabled(false);

                        img.setVisibility(View.VISIBLE);
                        img.setTag(Image);
                        imgbut.setVisibility(View.VISIBLE);
                        try {
                            File file = new File(getFilesDir(), Image);
                            FileInputStream inputStream = new FileInputStream(file);

                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            img.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        imgbut.setOnClickListener(deleteImageClick);
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parent.addView(child);
                        }
                    });

                    if (i == ItemsCount - 1) {
                        lastIndex = ID;
                    }
                }
            }
        });
        readFromDatabase.start();
    }

    //endregion
    //region addView
    void addView(ViewGroup viewGroup, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.addrepeatslistitem, viewGroup, false);
        RelativeLayout rel = item.findViewById(R.id.RelativeAddItem);

        lastIndex++;
        rel.setTag(lastIndex);

        //Finding views
        ImageButton delete = item.findViewById(R.id.deleteItem);
        ImageButton addImage = item.findViewById(R.id.addImage);
        ImageButton deleteImage = item.findViewById(R.id.deleteImage);
        ImageButton addAnswer = item.findViewById(R.id.addAnswerButton);
        LinearLayout linear = item.findViewById(R.id.LinearQA);
        EditText question = linear.findViewById(R.id.questionBox);
        EditText answer = linear.findViewById(R.id.answerBox);

        //Setting listeners for views
        delete.setOnClickListener(deleteClick);
        addImage.setOnClickListener(addImageClick);
        deleteImage.setOnClickListener(deleteImageClick);
        addAnswer.setOnClickListener(addAnswerClick);
        question.setOnFocusChangeListener(newText);
        answer.setOnFocusChangeListener(newAnswer);

        answer.setFilters(new InputFilter[]{inputFilter});

        //Adding item to root view
        viewGroup.addView(item);
    }
    //endregion

    //region clicks
    private View.OnClickListener deleteClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            ViewGroup rootView = (ViewGroup) parent.getParent();

            int index = Integer.parseInt(parent.getTag().toString());
            ImageView image = parent.findViewById(R.id.imageView);
            if (image.getVisibility() == View.VISIBLE) {
                String imageName = image.getTag().toString();

                File file = new File(getFilesDir(), imageName);
                boolean del = file.delete();

                DB.deleteImage(id, imageName);
            }

            if (rootView.getChildCount() != 1) {
                rootView.removeView(parent);
                DB.deleteItem(id, index);
            }
        }
    };

    private View.OnClickListener addImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            imagePreview = view.getParent();
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(photoPickerIntent, RequestCodes.PICK_IMAGE);
        }
    };

    private final View.OnClickListener deleteImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View pView = (View) view.getParent();

            ImageView img = pView.findViewById(R.id.imageView);
            ImageButton imgBut = pView.findViewById(R.id.deleteImage);
            ImageButton imgAdd = pView.findViewById(R.id.addImage);

            final String imageName = img.getTag().toString();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(getFilesDir(), imageName);
                    boolean del = file.delete();
                    DB.deleteImage(id, imageName);
                }
            });
            thread.start();

            img.setVisibility(View.GONE);
            img.setImageBitmap(null);
            img.setTag(null);
            imgBut.setVisibility(View.GONE);
            imgAdd.setEnabled(true);
        }
    };

    private View.OnClickListener addAnswerClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            addAnswer(view, "");
        }
    };
    //endregion

    //region focus
    View.OnFocusChangeListener newName = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {
                EditText projectname = (EditText) view;
                DB.setTableName(projectname.getText().toString(), id);
            }
        }
    };

    View.OnFocusChangeListener newText = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {

            if (!hasFocus) {
                EditText editText = (EditText) view;

                //Getting parent of EditText
                ViewGroup RelativeAddItem = (ViewGroup) view.getParent().getParent();

                String column = "";

                //Getting index of item in root view
                int index = Integer.parseInt(RelativeAddItem.getTag().toString());

                if (RelativeAddItem.findViewById(R.id.questionBox) == view) {
                    column = "question";
                }

                String question = RepeatsHelper.removeSpaces(editText.getText().toString());

                DB.InsertValueByID(id, index, column, question);

                editText.setText(question);
            }
        }
    };

    View.OnFocusChangeListener newAnswer = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            EditText editText = (EditText) view;
            String answer = editText.getText().toString();

            if (hasFocus) {
                editText.setTag(answer);
            } else {

                answer = RepeatsHelper.removeSpaces(editText.getText().toString());
                ViewGroup RelativeAddItem = (ViewGroup) view.getParent().getParent().getParent();

                if (RelativeAddItem.getTag() == null) {
                    RelativeAddItem = (ViewGroup) view.getParent().getParent();
                }

                int index = Integer.parseInt(RelativeAddItem.getTag().toString());

                String allAnswers = DB.getAnswers(id, index);

                String newAnswer = "";

                String oldAnswer = editText.getTag().toString();

                if (allAnswers.contains(oldAnswer) && !oldAnswer.equals("")) {
                    newAnswer = allAnswers.replace(oldAnswer, answer);
                } else if (!allAnswers.equals("")) {
                    newAnswer = allAnswers + RepeatsHelper.breakLine + answer;
                } else {
                    newAnswer = answer;
                }

                DB.InsertValueByID(id, index, "answer", newAnswer);

                editText.setText(answer);
            }
        }
    };

    //endregion
    //region addAnswer
    void addAnswer(View view, String text) {
        View pView = (View) view.getParent();
        final ViewGroup linearQA = pView.findViewById(R.id.LinearQA);
        LayoutInflater inflater = LayoutInflater.from(context);

        View answer = inflater.inflate(R.layout.answerbox, linearQA, false);
        EditText answerBoxPlus = answer.findViewById(R.id.answerBoxPlus);
        ImageButton delAnswer = answer.findViewById(R.id.delAnswer);

        answerBoxPlus.setFilters(new InputFilter[]{inputFilter});
        answerBoxPlus.setText(text);
        answerBoxPlus.setOnFocusChangeListener(newAnswer);

        delAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFocus();
                View p = (View) view.getParent();
                EditText answerBoxPlus = p.findViewById(R.id.answerBoxPlus);
                String answer = answerBoxPlus.getText().toString();

                ViewGroup linear = (ViewGroup) p.getParent();

                ViewGroup RelativeAddItem = (ViewGroup) linear.getParent();
                int index = Integer.parseInt(RelativeAddItem.getTag().toString());

                String allAnswers = DB.getAnswers(id, index);

                if (!answer.isEmpty()) {
                    String delAnswer = allAnswers.replace(RepeatsHelper.breakLine + answer, "");
                    DB.InsertValueByID(id, index, "answer", delAnswer);
                }

                if (allAnswers.contains(RepeatsHelper.breakLine + RepeatsHelper.breakLine)) {
                    String delAnswer = allAnswers.replace(RepeatsHelper.breakLine + RepeatsHelper.breakLine, RepeatsHelper.breakLine);
                    DB.InsertValueByID(id, index, "answer", delAnswer);
                }
                linear.removeView(p);
            }
        });

        linearQA.addView(answer);
    }

    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                final InputStream imageStream;
                try {
                    RelativeLayout rel = (RelativeLayout) imagePreview;
                    final ImageView imageView = rel.findViewById(R.id.imageView);

                    imageView.setVisibility(View.VISIBLE);
                    imageStream = getContentResolver().openInputStream(selectedImage);

                    //Creating and resizing image
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(imageStream, null, options);
                    options.inSampleSize = RepeatsHelper.calculateInSampleSize(options, 500, 500);
                    options.inJustDecodeBounds = false;
                    InputStream is = getContentResolver().openInputStream(selectedImage);
                    final Bitmap selected = BitmapFactory.decodeStream(is, null, options);

                    imageView.setImageBitmap(selected);

                    //Unlocking delete image button and locking add image button
                    final ImageButton imgbut = rel.findViewById(R.id.deleteImage);
                    final ImageButton addimg = rel.findViewById(R.id.addImage);
                    addimg.setEnabled(false);
                    imgbut.setVisibility(View.VISIBLE);

                    //Id of the image
                    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss");
                    String imageDate = simpleDate.format(new Date());
                    String ImageName = "I" + imageDate + ".png";
                    imageView.setTag(ImageName);

                    //Saving image
                    try {
                        File control = new File(context.getFilesDir(), ImageName);
                        boolean bool = control.createNewFile();

                        FileOutputStream out = new FileOutputStream(control);
                        selected.compress(Bitmap.CompressFormat.PNG, 100, out);

                        ViewGroup parent = (ViewGroup) rel.getParent();
                        int index = Integer.parseInt(rel.getTag().toString());

                        DB.InsertValueByID(id, index, "image", ImageName);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();

                    Toast.makeText(this, R.string.imageError, Toast.LENGTH_LONG).show();
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(photoPickerIntent, RequestCodes.PICK_IMAGE);
                }
            }
        }
    }

    void DeleteSet(String x) {
        DatabaseHelper DB = new DatabaseHelper(this);
        ArrayList<String> allImages = new ArrayList<>();
        if (!x.equals("FALSE")) {
            allImages = DB.getAllImages(x);
            DB.deleteOneFromList(x);
            DB.DeleteSet(x);
        }

        int count = allImages.size();

        if (count != 0) {
            for (int j = 0; j < count; j++) {
                String imgName = allImages.get(j);
                File file = new File(getFilesDir(), imgName);
                boolean bool = file.delete();
            }
        }
    }

    private InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            if (charSequence != null && "\\".contains(("" + charSequence))) {
                return "";
            }
            return null;
        }
    };

    void resetFocus() {
        View current = getCurrentFocus();
        if (current != null) {
            current.clearFocus();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        resetFocus();
        AddEditSetActivity.super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!deleted) {
            resetFocus();
        }

    }
}
