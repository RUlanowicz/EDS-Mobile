package eds.com.eds_mobile.fragment;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eds.com.eds_mobile.R;
import eds.com.eds_mobile.model.Report;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class CreateReportFragment extends Fragment implements View.OnClickListener {

    @InjectView(R.id.action_a) FloatingActionButton actionA;
    @InjectView(R.id.action_b) FloatingActionButton actionB;
    @InjectView(R.id.action_c) FloatingActionButton actionC;
    @InjectView(R.id.action_d) FloatingActionButton actionD;
    @InjectView(R.id.action_e) FloatingActionButton actionE;
    GoogleMap mMap;
    Marker mMarker;
    boolean updateAgain;

    private OnFragmentInteractionListener mListener;

    public static CreateReportFragment newInstance() {
//        CreateReportFragment fragment = new CreateReportFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return new CreateReportFragment();
    }

    public CreateReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateAgain = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_report, container, false);
        ButterKnife.inject(this,view);

        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_container, mapFragment);
        fragmentTransaction.commit();

        final GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (updateAgain) {
                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                    if (mMarker != null) {
                        mMarker.remove();
                    }
                    mMarker = mMap.addMarker(new MarkerOptions().position(loc).draggable(true));
                    if (mMap != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 12));
                    }
                    updateAgain = false;
                }
            }
        };

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMyLocationEnabled(true);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(40.393471, -80.026958),12);
                mMap.moveCamera(cameraUpdate);

                mMap.setOnMyLocationChangeListener(myLocationChangeListener);

                mMap.getUiSettings().setMapToolbarEnabled(false);

                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        updateAgain = true;
                        return false;
                    }
                });

                Realm realm = Realm.getInstance(getActivity());
                RealmResults<Report> reports = realm.allObjects(Report.class);
                MarkerOptions myMarkerOptions;
                for (Report report : reports) {
                    myMarkerOptions = new MarkerOptions().position(new LatLng(report.getLat(),report.getLon())).title(report.getReportType()).draggable(false);
                    mMap.addMarker(myMarkerOptions);
                }
            }
        });

        actionA.setOnClickListener(this);
        actionB.setOnClickListener(this);
        actionC.setOnClickListener(this);
        actionD.setOnClickListener(this);
        actionE.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        double lat, lon;
        lat = mMarker.getPosition().latitude;
        lon = mMarker.getPosition().longitude;
        if (v.getId() == actionA.getId()) {
            mListener.setSelectedReportType("Sewage",lat,lon);
        }
        else if (v.getId() == actionB.getId()) {
            //Acid Mine Drainage
            mListener.setSelectedReportType("Acid Mine Drainage",lat,lon);
        }
        else if (v.getId() == actionC.getId()) {
            //Erosion
            mListener.setSelectedReportType("Erosion",lat,lon);
        }
        else if (v.getId() == actionD.getId()) {
            //Illegal Dumping
            mListener.setSelectedReportType("Illegal Dumping",lat,lon);
        }
        else if (v.getId() == actionE.getId()) {
            //Clogged Inlet
            mListener.setSelectedReportType("Clogged Inlet",lat,lon);
        }
    }

    public interface OnFragmentInteractionListener {
        public void setSelectedReportType(String reportType, double lat, double lon);
    }

}
