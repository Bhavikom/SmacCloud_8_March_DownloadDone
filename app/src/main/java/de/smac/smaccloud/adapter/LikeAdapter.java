package de.smac.smaccloud.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserLike;

/**
 * This class is user to show likes of file
 */

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.LikeHolder>
{
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<UserLike> userLikes;

    public LikeAdapter(Context context, ArrayList<UserLike> userLikes)
    {
        this.context = context;
        this.userLikes = userLikes;
        this.inflater = LayoutInflater.from(this.context);
    }

    @Override
    public LikeHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View rootView = inflater.inflate(R.layout.partial_like_users, parent, false);
        return new LikeHolder(rootView);
    }

    @Override
    public void onBindViewHolder(LikeHolder holder, int position)
    {
        final int finalPosition = position;
        User user = userLikes.get(position).user;
        holder.labelUserName.setText(user.name);
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a",Locale.getDefault());
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        //holder.labelInsertDate.setText(Helper.getDateFormate().format(user.insertDate));
        try
        {
            /* getting comment time from database and parse it */
            String strDate = Helper.getDateFormatCurrentDateTime().format(userLikes.get(position).insertDate);
            Date date = Helper.getDateFormatCurrentDateTime().parse(strDate);
            holder.labelInsertDate.setText(strDate);
        }
        catch (ParseException parseEx)
        {
            parseEx.printStackTrace();
        }
        holder.labelInsertDate.setTypeface(Helper.robotoLightTypeface);
        holder.labelUserName.setTypeface(Helper.robotoBoldTypeface);
    }

    @Override
    public int getItemCount()
    {
        return userLikes.size();
    }

    class LikeHolder extends RecyclerView.ViewHolder
    {

        TextView labelUserName, labelInsertDate;

        public LikeHolder(View itemView)
        {
            super(itemView);
            itemView.setId(R.id.itemView);
            labelUserName = (TextView) itemView.findViewById(R.id.labelUserName);
            labelInsertDate = (TextView) itemView.findViewById(R.id.labelInsertDate);
        }
    }
}
