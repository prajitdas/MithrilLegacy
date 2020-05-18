package edu.umbc.ebiquity.mithril.ui.adapters;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.Violation;
import edu.umbc.ebiquity.mithril.ui.ViolationFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Violation} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ViolationRecyclerViewAdapter extends RecyclerView.Adapter<ViolationRecyclerViewAdapter.ViewHolder> {

    private final List<Violation> mValues;
    private final OnListFragmentInteractionListener mListener;
    private View view;

    public ViolationRecyclerViewAdapter(List<Violation> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_violation, parent, false);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Resources res = view.getContext().getResources();
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            drawable = res.getDrawable(R.drawable.ic_launcher, view.getContext().getTheme());
        else
            drawable = res.getDrawable(R.drawable.ic_launcher);

        holder.mItem = mValues.get(position);
        holder.mViolatingAppIcon.setImageDrawable(drawable);
        holder.mViolationText.setText(mValues.get(position).getViolationDescription());
        holder.mContextImage.setImageDrawable(drawable);
        holder.mResponseYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "Yes!!!", Toast.LENGTH_LONG).show();
            }
        });
        holder.mResponseNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "No!!", Toast.LENGTH_LONG).show();
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mViolatingAppIcon;
        public final TextView mViolationText;
        public final ImageView mContextImage;
        public final Button mResponseYesButton;
        public final Button mResponseNoButton;

        public Violation mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mViolatingAppIcon = (ImageView) view.findViewById(R.id.violatingAppIcon);
            mViolationText = (TextView) view.findViewById(R.id.violationText);
            mContextImage = (ImageView) view.findViewById(R.id.contextImage);
            mResponseYesButton = (Button) view.findViewById(R.id.mResponseYesButton);
            mResponseNoButton = (Button) view.findViewById(R.id.responseNoButton);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.toString() + "'";
        }
    }
}
