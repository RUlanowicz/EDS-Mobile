package eds.com.eds_mobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import eds.com.eds_mobile.R;
import eds.com.eds_mobile.model.Report;

/**
 * Created by ulanowicz on 2/22/15.
 */
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private ArrayList<Report> mListings;
    private Context mContext;

    public ReportAdapter(ArrayList<Report> reports, Context context) {
        this.mListings = reports;
        this.mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.report_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Report report = mListings.get(position);
        holder.title.setText(report.getReportType());
        holder.coordinates.setText(report.getLat()+", "+report.getLon());
        Picasso.with(mContext).load(report.getImages().get(0).getAbsolutePath()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return mListings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView coordinates;

        public ViewHolder (View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.listing_image);
            title = (TextView) itemView.findViewById(R.id.listing_name);
            coordinates = (TextView) itemView.findViewById(R.id.listing_coords);
        }
    }
}
