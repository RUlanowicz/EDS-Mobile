package eds.com.eds_mobile;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import eds.com.eds_mobile.fragment.SubmitReportFragment;
import eds.com.eds_mobile.model.Image;
import eds.com.eds_mobile.model.Report;
import io.realm.Realm;


public class SubmitActivity extends ActionBarActivity implements SubmitReportFragment.OnFragmentInteractionListener{
    String currentDescription;
    double currentLat, currentLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        Bundle bundle = getIntent().getExtras();
        String currentPhotoPath = bundle.getString("filePath");
        File currentPhoto = new File(currentPhotoPath);
        currentDescription = bundle.getString("description");
        currentLat = bundle.getDouble("lat");
        currentLon = bundle.getDouble("lon");

        SubmitReportFragment fragment = SubmitReportFragment.newInstance(currentPhoto,currentDescription,currentLat,currentLon);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.submit_fragment_container,fragment).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit, menu);
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
    public void onSubmitReport(String currentDescription, double currentLat, double currentLon, String filePath) {
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();
        Report report = realm.createObject(Report.class);
        Image image = realm.createObject(Image.class);
        image.setAbsolutePath(filePath);
        report.getImages().add(image);
        report.setReportType(this.currentDescription);
        report.setDescription(currentDescription);
        report.setLat(this.currentLat);
        report.setLon(this.currentLon);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        report.setUserName(preferences.getString("name", null));
        report.setZipCode(preferences.getString("zip", null));
        report.setEmailAddress(preferences.getString("email", null));
        realm.commitTransaction();
        finish();
        //TODO upload image to S3/put URL into object and save
    }
}
