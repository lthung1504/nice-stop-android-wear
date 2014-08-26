package vn.siliconstraits.nicestop.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import harmony.android.library.model.VenueMobile;
import vn.siliconstraits.nicestop.R;

/**
 * Created by HarmonyLee on 8/22/14.
 */
public class VenueAdapter extends BaseAdapter {
    protected static final String TAG = VenueAdapter.class.getSimpleName();
    private List<VenueMobile> mItems;
    private Context mContext;

    public VenueAdapter(Context context, List<VenueMobile> items) {
        mItems = items;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final VenueHolder holder;
        final VenueMobile item;
        if (convertView == null) {
            holder = new VenueHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.adapter_venue, parent, false);

            holder.ivIcon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
            holder.tvName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.tvAddress = (TextView) convertView.findViewById(R.id.textViewAddress);

            convertView.setTag(holder);
        } else {
            holder = (VenueHolder) convertView.getTag();
        }

        if (mItems != null) {
            item = mItems.get(position);
            if (item != null) {

                // TODO: need to replace with image
                // get link image category
//                if (item.getCategories() != null && item.getCategories().size() > 0) {
//                    Venue.Category.Icon icon = item.getCategories().get(0).getIcon();
//                    String urlImageCategory = String.format("%sbg_64%s", icon.getPrefix(), icon.getSuffix());
//
//
////                    Ion.with(holder.ivIcon).placeholder(R.drawable.venue_default).load(urlImageCategory);
//                }

                // show info venue
                holder.tvName.setText(item.getName());
                if (item.getAddress() != null) {
                    holder.tvAddress.setVisibility(View.VISIBLE);
                    holder.tvAddress.setText(item.getAddress());
                } else
                    holder.tvAddress.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class VenueHolder {
        public ImageView ivIcon;
        public TextView tvName;
        public TextView tvAddress;
    }
}
