package eds.com.eds_mobile;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eds.com.eds_mobile.fragment.CreateReportFragment;
import eds.com.eds_mobile.fragment.SubmitReportFragment;
import eds.com.eds_mobile.model.Image;
import eds.com.eds_mobile.model.Report;
import io.realm.Realm;


public class MainActivity extends ActionBarActivity implements CreateReportFragment.OnFragmentInteractionListener, SubmitReportFragment.OnFragmentInteractionListener{

    GoogleMap mMap;
    private final int USER_RECOVERABLE_ERROR = 0;
    static final int REQUEST_IMAGE_CAPTURE_FIRST = 1;

    File currentPhoto;
    private String currentDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (resultCode == ConnectionResult.SUCCESS) {
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
                Realm realm = Realm.getInstance(this);
                realm.beginTransaction();
                Report report = realm.createObject(Report.class);
                report.setDateReported(new Date());
                report.setLat(0); //TODO get real lat
                report.setLon(0); //TODO get real lon
                report.setDescription(currentDescription);
                Image image = realm.createObject(Image.class);
                report.getImages().add(image);
                realm.commitTransaction();

//                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(currentPhoto.getAbsolutePath()), 2096, 1048);
                SubmitReportFragment fragment = SubmitReportFragment.newInstance(thumbnail,currentDescription);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
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
    public void setSelectedReportType(String reportType) {
        currentDescription = reportType;

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
    public void onSubmitReport() {

    }
}
