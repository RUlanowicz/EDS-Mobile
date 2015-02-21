package eds.com.eds_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eds.com.eds_mobile.model.Report;
import io.realm.Realm;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    GoogleMap mMap;
    private final int USER_RECOVERABLE_ERROR = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @InjectView(R.id.action_a) FloatingActionButton actionA;
    @InjectView(R.id.action_b) FloatingActionButton actionB;
    @InjectView(R.id.action_c) FloatingActionButton actionC;
    @InjectView(R.id.action_d) FloatingActionButton actionD;
    @InjectView(R.id.action_e) FloatingActionButton actionE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (resultCode == ConnectionResult.SUCCESS) {
            SupportMapFragment mapFragment = new SupportMapFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mapFragment);
            fragmentTransaction.commit();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.setMyLocationEnabled(true);
                }
            });

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

        actionA.setOnClickListener(this);
        actionB.setOnClickListener(this);
        actionC.setOnClickListener(this);
        actionD.setOnClickListener(this);
        actionE.setOnClickListener(this);
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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onClick(View v) {
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();
        Report report = realm.createObject(Report.class);
        report.setDateReported(new Date());
        report.setLat(0); //TODO get real lat
        report.setLon(0); //TODO get real lon
        if (v.getId() == actionA.getId()) {
            report.setDescription("Sewage");
        }
        else if (v.getId() == actionB.getId()) {
            //Acid Mine Drainage
            report.setDescription("Acid Mine Drainage");
        }
        else if (v.getId() == actionC.getId()) {
            //Erosion
            report.setDescription("Erosion");
        }
        else if (v.getId() == actionD.getId()) {
            //Illegal Dumping
            report.setDescription("Illegal Dumping");
        }
        else if (v.getId() == actionE.getId()) {
            //Clogged Inlet
            report.setDescription("Clogged Inlet");
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
