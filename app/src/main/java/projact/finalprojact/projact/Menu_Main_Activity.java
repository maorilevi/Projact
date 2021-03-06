package projact.finalprojact.projact;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;

import java.util.List;

public class Menu_Main_Activity extends Activity {
    private static int RESULT_LOAD_IMG = 1;
    protected static String imgDecodableString;
    public static String DadId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //parse parse parse parse parse parse parse parse

        Parse.enableLocalDatastore(this);

        Parse.initialize(this,Parse_Keys.APPLICATION_ID,Parse_Keys.CLIENT_KEY);
        PushService.setDefaultPushCallback(this, SplachScreen.class);
        //Automat login by user name and password are saved in app preferneces
        final SharedPreferences prefernces = PreferenceManager.getDefaultSharedPreferences(Menu_Main_Activity.this);
        final String username=prefernces.getString(getString(R.string.user_name), "");
        final String userpass=prefernces.getString(getString(R.string.user_password), "");
        //checking if the values are empty if thai not so checking matches in parse
        if(username.isEmpty()||userpass.isEmpty()){
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            login signupFragment = new login();
            transaction.add(R.id.fragment_placeholder, signupFragment);
            transaction.commit();
        }
        else{//checking user password and user name in parse
            signup.InSignUp=false;
            ParseQuery<ParseObject> dadtable=ParseQuery.getQuery("USER");
            dadtable.whereEqualTo("User_Name", username);
            dadtable.whereEqualTo("Password", userpass);
            dadtable.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if(e==null){
                        if(parseObjects.size()>0) {
                            if(parseObjects.get(0).getString("Parent_OR_kid").matches("p")) {
                                //to parent screen
                                Fragment newfragment;
                                newfragment = new Menu_Parent();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_placeholder, newfragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }else{//to kid screen
                                Fragment newfragment;
                                newfragment = new Menu_Kid();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_placeholder, newfragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        }
                    }
                    else{
                        Toast.makeText(getApplication(), "Connection problem", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public void onClick(View v) {

        Fragment newfragment;//creating acopy to hold the fragment
        if(v ==findViewById(R.id.sig_up_btn))
        {
            newfragment=new signup();
        }
        else
        {
            newfragment=new login();
        }
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, newfragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //*function are lanche you to location screen from menu*//
    public void tolocation(View view)
    {
        Intent intent=new Intent(this,MapsActivity.class);
        startActivity(intent);
    }
    public void toSchedule(View view){
        Intent intent=new Intent(this,Schedule_Activity.class);
        startActivity(intent);
    }
    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                if(signup.InSignUp){//in case its to add sign up screen
                    ImageView imageView = (ImageView) findViewById(R.id.SignUp_User_Image);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                }else{//in case its to add kid screen
                    ImageView imageView = (ImageView) findViewById(R.id.kid_Image);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
}












