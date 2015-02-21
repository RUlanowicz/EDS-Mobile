package eds.com.eds_mobile.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eds.com.eds_mobile.R;

public class CreateReportFragment extends Fragment implements View.OnClickListener{

    @InjectView(R.id.action_a) FloatingActionButton actionA;
    @InjectView(R.id.action_b) FloatingActionButton actionB;
    @InjectView(R.id.action_c) FloatingActionButton actionC;
    @InjectView(R.id.action_d) FloatingActionButton actionD;
    @InjectView(R.id.action_e) FloatingActionButton actionE;
    GoogleMap mMap;

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
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMyLocationEnabled(true);
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
        if (v.getId() == actionA.getId()) {
            mListener.setSelectedReportType("Sewage");
        }
        else if (v.getId() == actionB.getId()) {
            //Acid Mine Drainage
            mListener.setSelectedReportType("Acid Mine Drainage");
        }
        else if (v.getId() == actionC.getId()) {
            //Erosion
            mListener.setSelectedReportType("Erosion");
        }
        else if (v.getId() == actionD.getId()) {
            //Illegal Dumping
            mListener.setSelectedReportType("Illegal Dumping");
        }
        else if (v.getId() == actionE.getId()) {
            //Clogged Inlet
            mListener.setSelectedReportType("Clogged Inlet");
        }
    }

    public interface OnFragmentInteractionListener {
        public void setSelectedReportType(String reportType);
    }

}
