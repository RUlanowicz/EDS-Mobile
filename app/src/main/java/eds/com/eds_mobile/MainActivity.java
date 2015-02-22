package eds.com.eds_mobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import butterknife.ButterKnife;
import eds.com.eds_mobile.fragment.CreateReportFragment;
import eds.com.eds_mobile.fragment.SubmitReportFragment;
import eds.com.eds_mobile.model.Report;
import io.realm.Realm;


public class MainActivity extends ActionBarActivity implements CreateReportFragment.OnFragmentInteractionListener, SubmitReportFragment.OnFragmentInteractionListener{

    GoogleMap mMap;
    private final int USER_RECOVERABLE_ERROR = 0;
    static final int REQUEST_IMAGE_CAPTURE_FIRST = 1;
    static final int REQUEST_IMAGE_CAPTURE_SUBSEQUENT = 2;

    File currentPhoto;
    private String currentDescription;
    private double currentLat;
    private double currentLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (resultCode == ConnectionResult.SUCCESS) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (!preferences.contains("userKey")) {
                View popupView = View.inflate(this,R.layout.user_info_popup,null);
                ButterKnife.inject(this,popupView);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("User Information");
                builder.setView(popupView);


                final EditText userName = (EditText) popupView.findViewById(R.id.user_name);
                final EditText emailAddress = (EditText) popupView.findViewById(R.id.user_email);
                final EditText userZip = (EditText) popupView.findViewById(R.id.user_zip);

                builder.setCancelable(false);
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!userName.getText().toString().equals("") && !userZip.getText().toString().equals("") && !emailAddress.getText().toString().equals("")) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("name",userName.getText().toString());
                            editor.putString("email",emailAddress.getText().toString());
                            editor.putString("zip",userZip.getText().toString());
                            editor.putString("userKey",random());
                            editor.apply();
                        }
                    }
                });
                builder.create().show();
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,CreateReportFragment.newInstance());
            fragmentTransaction.commit();
        }
        else if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
            GooglePlayServicesUtil.getErrorDialog(resultCode,this,USER_RECOVERABLE_ERROR).show();
        }
        else {
            GooglePlayServicesUtil.showErrorNotification(resultCode,this);
        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == USER_RECOVERABLE_ERROR) {

        }

        if (requestCode == REQUEST_IMAGE_CAPTURE_FIRST && resultCode == RESULT_OK) {
            if (currentPhoto.exists()) {
//                Bitmap thumbnail = BitmapFactory.decodeFile(currentPhoto.getAbsolutePath());
//                thumbnail = Bitmap.createScaledBitmap(thumbnail,2096,2096,false);
                SubmitReportFragment fragment = SubmitReportFragment.newInstance(currentPhoto,currentDescription,currentLat,currentLon);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.fragment_container,fragment).commit();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",new Locale("US")).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    public void setSelectedReportType(String reportType, double lat, double lon) {
        currentDescription = reportType;
        currentLat = lat;
        currentLon = lon;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                currentPhoto = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("There was an issue creating creating a place to store your image, check your device storage");
                builder.create().show();
            }
            if (currentPhoto != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(currentPhoto));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_FIRST);
            }
        }
    }

    @Override
    public void onSubmitReport(String currentDescription, double currentLat, double currentLon) {
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();
        Report report = realm.createObject(Report.class);
        report.setReportType(this.currentDescription);
        report.setDescription(currentDescription);
        report.setLat(this.currentLat);
        report.setLon(this.currentLon);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        report.setUserName(preferences.getString("name",null));
        report.setZipCode(preferences.getString("zip",null));
        report.setEmailAddress(preferences.getString("email",null));
        realm.commitTransaction();
        //TODO upload image to S3/put URL into object and save
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,new CreateReportFragment()).commit();
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
