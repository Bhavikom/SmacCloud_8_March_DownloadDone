package de.smac.smaccloud.adapter;


import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.UserComment;

/**
 * This class is user to bind file comments in RecyclerView
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<UserComment> userComments;
    private ArrayList<String> dateLabelList;


    public CommentAdapter(Activity activity, ArrayList<UserComment> userComments)
    {
        this.activity = activity;
        this.userComments = userComments;
        this.inflater = LayoutInflater.from(this.activity);
        this.dateLabelList = new ArrayList<>();
    }

    public void addMoreData(ArrayList<UserComment> userComments)
    {
        this.userComments = userComments;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View rootView = inflater.inflate(R.layout.partial_comment_user, parent, false);
        return new CommentHolder(rootView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        CommentHolder commentHolder = (CommentHolder) holder;
        UserComment userComment = userComments.get(position);
        commentHolder.labelUserName.setText(userComment.user.name);
        commentHolder.labelComment.setText(userComment.comment);
        commentHolder.linearChild.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(activity) / 2, ViewGroup.LayoutParams.WRAP_CONTENT));
        try
        {
            if (userComment.insertDate != null)
            {

                /* getting current date and time */
                Calendar c = Calendar.getInstance();
                //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String strDateCurrent = Helper.getDateFormatCurrentDateTime().format(c.getTime());
                Date dateCurrent = Helper.getDateFormatCurrentDateTime().parse(strDateCurrent);

                /* getting comment time from database and parse it */
                String strDate = Helper.getDateFormatCurrentDateTime().format(userComment.insertDate);
                Date date = Helper.getDateFormatCurrentDateTime().parse(strDate);

                /* get time difference between two milliseconds in string and showing in lable */
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(date.getTime(),
                        dateCurrent.getTime(), DateUtils.SECOND_IN_MILLIS);
                commentHolder.labelInsertDate.setText(strDate);

                if (position == userComments.size() - 1)
                {
                    CharSequence timeAgo2 = DateUtils.getRelativeTimeSpanString(date.getTime(),
                            dateCurrent.getTime(), DateUtils.SECOND_IN_MILLIS);
                    Log.e(" ^^^^^$%$%$ ", " comment and time : " + timeAgo2 + " : " + userComment.comment);
                }

                /* Show date label*/
                String strDateLabel = Helper.getDateForCommentDateLabel().format(userComment.insertDate);
                if (!dateLabelList.contains(strDateLabel))
                {
                    dateLabelList.add(strDateLabel);
                    Calendar calendar = Calendar.getInstance();
                    calendar.getTime();
                    String strToday = Helper.getDateForCommentDateLabel().format(calendar.getTime());
                    calendar.add(Calendar.DATE, -1);
                    String strYesterday = Helper.getDateForCommentDateLabel().format(calendar.getTime());
                    if (strDateLabel.equalsIgnoreCase(strToday))
                    {
                        commentHolder.textDateLabel.setText(activity.getString(R.string.label_today));
                    }
                    else if (strDateLabel.equalsIgnoreCase(strYesterday))
                    {
                        commentHolder.textDateLabel.setText(activity.getString(R.string.label_yesterday));
                    }
                    else
                    {
                        commentHolder.textDateLabel.setText(strDateLabel);
                    }

                    commentHolder.textDateLabel.setVisibility(View.VISIBLE);
                }
                else
                {
                    commentHolder.textDateLabel.setVisibility(View.GONE);
                }
                /* End show date label */
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (position == 0)
            commentHolder.divider_view.setVisibility(View.GONE);
        else
            commentHolder.divider_view.setVisibility(View.VISIBLE);


        boolean isMyMessage = false; // flag to decide that its my messege or not
        if (String.valueOf(PreferenceHelper.getUserContext(activity)).equalsIgnoreCase(String.valueOf(userComment.userId)))
        {
            isMyMessage = true;
        }
        else
        {
            isMyMessage = false;
        }
        /* custom function to set alignment of view based on message type */
        setAlignment(commentHolder, isMyMessage);


    }

    @Override
    public int getItemCount()
    {
        return userComments.size();
    }

    private void setAlignment(CommentHolder holder, boolean isMe)
    {
        if (isMe)
        {
            holder.imgUserRight.setVisibility(View.VISIBLE);
            holder.imgUserLeft.setVisibility(View.GONE);
            holder.linearChild.setBackgroundResource(R.drawable.in_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.linearChild.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            Helper.setupTypeface(holder.reletiveparentLayout,Helper.robotoRegularTypeface);


            // layoutParams.width=100;

            if (Helper.isTablet(activity))
            {
                layoutParams.width = Helper.getDeviceWidth(activity) / 3;
            }
            holder.linearChild.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.linearParent.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            //lp.leftMargin = 100;
            holder.linearParent.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.labelComment.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.labelComment.setLayoutParams(layoutParams);
            holder.labelUserName.setLayoutParams(layoutParams);
            holder.labelInsertDate.setLayoutParams(layoutParams);

        }
        else
        {
            holder.imgUserRight.setVisibility(View.GONE);
            holder.imgUserLeft.setVisibility(View.VISIBLE);

            holder.linearChild.setBackgroundResource(R.drawable.out_message_bg);
            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) holder.linearParent.getLayoutParams();


            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.linearChild.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            if (Helper.isTablet(activity))
            {
                layoutParams.width = Helper.getDeviceWidth(activity) / 3;
            }
            holder.linearChild.setLayoutParams(layoutParams);
            //  layoutParams.width=100;

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.linearParent.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            //lp.rightMargin = 100;
            holder.linearParent.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.labelComment.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.labelComment.setLayoutParams(layoutParams);
            holder.labelUserName.setLayoutParams(layoutParams);
            holder.labelInsertDate.setLayoutParams(layoutParams);

        }

    }


    class CommentHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout linearChild;
        TextView textDateLabel;
        View divider_view;
        TextView labelUserName, labelInsertDate, labelComment;
        LinearLayout linearParent;
        RelativeLayout reletiveparentLayout;
        ImageView imgUserRight, imgUserLeft;

        public CommentHolder(View itemView)
        {
            super(itemView);
            itemView.setId(R.id.itemView);
            textDateLabel = (TextView) itemView.findViewById(R.id.textDateLabel);
            divider_view = itemView.findViewById(R.id.divider_view);
            labelUserName = (TextView) itemView.findViewById(R.id.labelUserName);
            imgUserLeft = (ImageView) itemView.findViewById(R.id.img_user_left);
            imgUserRight = (ImageView) itemView.findViewById(R.id.img_user_right);
            labelInsertDate = (TextView) itemView.findViewById(R.id.labelInsertDate);
            labelComment = (TextView) itemView.findViewById(R.id.labelComment);
            linearParent = (LinearLayout) itemView.findViewById(R.id.parentLayout);
            linearChild = (LinearLayout) itemView.findViewById(R.id.linearContentBackground);
            reletiveparentLayout = (RelativeLayout) itemView.findViewById(R.id.reletiveparentLayout);

        }
    }


}
