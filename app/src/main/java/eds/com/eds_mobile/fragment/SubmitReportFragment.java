package eds.com.eds_mobile.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import eds.com.eds_mobile.R;

public class SubmitReportFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Bitmap mBitmap;
    private String mReportType;
    private double latitude, longitude;
    @InjectView(R.id.header_image) ImageView headerImage;
    @InjectView(R.id.description_text) EditText descriptionText;
    private static String ARG_PARAM1 = "ARG_PARAM1";
    private static String ARG_PARAM2 = "ARG_PARAM2";
    private static String ARG_PARAM3 = "ARG_PARAM3";
    private static String ARG_PARAM4 = "ARG_PARAM4";

    public static SubmitReportFragment newInstance(Bitmap firstThumbnail, String reportType, double lat, double lon) {
        SubmitReportFragment fragment = new SubmitReportFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, firstThumbnail);
        args.putString(ARG_PARAM2, reportType);
        args.putDouble(ARG_PARAM3, lat);
        args.putDouble(ARG_PARAM4,lon);
        fragment.setArguments(args);
        return fragment;
    }

    public SubmitReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBitmap = getArguments().getParcelable(ARG_PARAM1);
            mReportType = getArguments().getString(ARG_PARAM2);
            latitude = getArguments().getDouble(ARG_PARAM3);
            longitude = getArguments().getDouble(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_submit_report, container, false);
        ButterKnife.inject(this,view);
//        reportTypeInfo.setText(mReportType);
        headerImage.setImageBitmap(mBitmap);
        return view;
    }

    @OnClick(R.id.save_button)
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onSubmitReport(descriptionText.getText().toString(), latitude, longitude);
        }
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

    public interface OnFragmentInteractionListener {
        public void onSubmitReport(String description, double lat, double lon);
    }

}
