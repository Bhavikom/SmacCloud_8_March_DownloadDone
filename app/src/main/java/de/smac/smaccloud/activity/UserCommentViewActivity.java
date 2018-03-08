package de.smac.smaccloud.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.CommentAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.MediaFragment;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Announcement;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.User;
import de.smac.smaccloud.model.UserComment;
import de.smac.smaccloud.service.FCMMessagingService;

import static de.smac.smaccloud.activity.MediaActivity.REQUEST_COMMENT;
import static de.smac.smaccloud.base.Helper.LOCALIZATION_TYPE_ERROR_CODE;
import static de.smac.smaccloud.service.FCMMessagingService.KEY_DATA_DATA_CONTENT;

/**
 * View comments of file
 */
public class UserCommentViewActivity extends Activity
{

    public LinearLayout linearCommentBar;
    public RelativeLayout parentLayout;
    PreferenceHelper prefManager;
    ScrollView scrollComment;
    LinearLayout commentLinearLayout;
    private RecyclerView listComments;
    private CommentAdapter commentAdapter;
    private ArrayList<UserComment> usersComments;
    private Media media;
    private Channel channel;
    private User user;
    private ImageView btnSend;
    private EditText edtMediaComment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_user);
        Helper.retainOrientation(UserCommentViewActivity.this);
        user = new User();
        prefManager = new PreferenceHelper(context);
        user.id = PreferenceHelper.getUserContext(context);
        media = this.getIntent().getParcelableExtra(MediaFragment.EXTRA_MEDIA);
        channel = this.getIntent().getParcelableExtra(MediaFragment.EXTRA_CHANNEL);

        edtMediaComment.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable arg0)
            {
                if (edtMediaComment.getText().length() > 0)
                {
                    btnSend.setImageResource(R.drawable.ic_fill_send);
                }
                else
                {
                    btnSend.setImageResource(R.drawable.ic_send);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
        });
        try
        {
            if (usersComments != null)
                usersComments.clear();
            DataHelper.getCommentsOnMedia(context, media.id, usersComments, false);
            commentAdapter = new CommentAdapter(UserCommentViewActivity.this, usersComments);
            listComments.setAdapter(commentAdapter);
            commentAdapter.notifyDataSetChanged();
            listComments.scrollToPosition(usersComments.size() - 1);

            addUserCommentInLinearLayout(usersComments);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        FCMMessagingService.commentPushReceiveListener = new FCMMessagingService.CommentPushReceiveListener()
        {
            @Override
            public void onCommentPushReceived()
            {
                updateCommentList();
            }
        };
        clearFileCommentNotification();
    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        listComments = (RecyclerView) findViewById(R.id.listComments);
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        edtMediaComment = (EditText) findViewById(R.id.edtMediaComment);
        btnSend = (ImageView) findViewById(R.id.btnSend);
        btnSend.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)));
        usersComments = new ArrayList<>();
        user = new User();
        user.id = PreferenceHelper.getUserContext(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        listComments.setLayoutManager(layoutManager);
        linearCommentBar = (LinearLayout) findViewById(R.id.linearCommentBar);
        linearCommentBar.setVisibility(View.VISIBLE);

        scrollComment = (ScrollView) findViewById(R.id.scrollComment);
        commentLinearLayout = (LinearLayout) findViewById(R.id.commentLinearLayout);
        applyThemeColor();

        btnSend.setOnTouchListener(null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void applyThemeColor()
    {
        updateParentThemeColor();
        if (getSupportActionBar() != null)
        {
            if (media != null)
            {

                getSupportActionBar().setTitle(media.name);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
            }
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_material_vector);
            upArrow.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));

        }
        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);
        Helper.setupUI(this, parentLayout, parentLayout);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Update UI when screen rotate
        if (commentAdapter != null)
        {
            commentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();
        btnSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (prefManager.isDemoLogin())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.disable_comment_title));
                    builder.setMessage(getString(R.string.disable_comment_message));
                    builder.setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog dialog = builder.create();

                    dialog.show();
                }
                else
                {
                    if (media.isDownloaded == 0)
                    {
                        Helper.showSimpleDialog(context, getString(R.string.download_file_first));

                    }
                    else
                    {
                        String comment = edtMediaComment.getText().toString().trim();
                        if ((comment.length() > 0) && !(comment.startsWith(" ")))
                        {
                            if (Helper.isNetworkAvailable(context))
                            {
                                Helper.IS_DIALOG_SHOW = false;
                                postNetworkRequest(REQUEST_COMMENT, DataProvider.ENDPOINT_FILE, DataProvider.Actions.MEDIA_COMMENT,
                                        RequestParameter.urlEncoded("ChannelId", String.valueOf(DataHelper.getChannelId(context, media.id))),
                                        RequestParameter.urlEncoded("UserId", String.valueOf(PreferenceHelper.getUserContext(context))),
                                        RequestParameter.urlEncoded("MediaId", String.valueOf(media.id)),
                                        RequestParameter.urlEncoded("Org_Id", String.valueOf(PreferenceHelper.getOrganizationId(context))),
                                        RequestParameter.urlEncoded("Comment", comment));

                            }
                            else
                            {
                                Helper.storeCommentOffline(context, media, comment);
                                edtMediaComment.setText("");
                                updateCommentList();
                            }
                        }
                        else
                        {
                            notifySimple(getString(R.string.msg_please_enter_comment));
                            edtMediaComment.setFocusable(true);
                            notifySimple(getString(R.string.msg_please_enter_comment));
                            //edtMediaComment.setError(getString(R.string.msg_please_enter_comment));
                        }
                    }
                }

            }
        });
    }

    @Override
    protected void onDestroy()
    {
        clearFileCommentNotification();
        super.onDestroy();
    }

    private void clearFileCommentNotification()
    {
        // Clear notification related to this file
        try
        {
            ArrayList<Announcement> announcements = new ArrayList<>();
            DataHelper.getAnnouncementData(context, announcements);
            for (Announcement announcement : announcements)
            {
                if (announcement.type.equalsIgnoreCase(FCMMessagingService.PUSH_TYPE_ADD_COMMENT))
                {
                    JSONObject jsonAnnouncementValue = new JSONObject(announcement.value);
                    if (jsonAnnouncementValue.has(KEY_DATA_DATA_CONTENT))
                    {
                        UserComment commentData = new UserComment();
                        UserComment.parseFromJSon(new JSONObject(jsonAnnouncementValue.getString(KEY_DATA_DATA_CONTENT)), commentData);
                        if (commentData.fileId == media.id)
                        {
                            announcement.isRead = true;
                            DataHelper.updateAnnouncement(context, announcement);
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public void updateCommentList()
    {
        try
        {
            usersComments.clear();
            DataHelper.getCommentsOnMedia(context, media.id, usersComments, false);
            listComments.scrollToPosition(usersComments.size() - 1);
            commentAdapter.notifyDataSetChanged();
            addUserCommentInLinearLayout(usersComments);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onNetworkResponse(int requestCode, boolean status, String response)
    {
        super.onNetworkResponse(requestCode, status, response);
        if (requestCode == REQUEST_COMMENT)
        {
            if (status)
            {
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    int requestStatus = responseJson.optInt("Status");
                    if (requestStatus > 0)
                    {
                        Helper.storeCommentOffline(context, media, edtMediaComment.getText().toString());
                        if (responseJson.has("Message") && !responseJson.isNull("Message") && !responseJson.optString("Message").equalsIgnoreCase("null"))
                            notifySimple(DataHelper.getLocalizationMessageFromCode(context, String.valueOf(requestStatus), LOCALIZATION_TYPE_ERROR_CODE));
                        else
                        {
                            // TODO: 16-Jun-17 Custom message
                            notifySimple(getString(R.string.msg_please_try_again_later));
                        }
                    }
                    else
                    {
                        JSONObject userCommentJson = responseJson.optJSONObject("Payload");
                        UserComment userComment = new UserComment();
                        if (userCommentJson != null)
                        {
                            UserComment.parseFromJSon(userCommentJson, userComment);
                            if (userComment.userId > 0)
                                userComment.add(context);


                        }
                        else if (responseJson.has("Message") && !responseJson.isNull("Message") && !responseJson.optString("Message").equalsIgnoreCase("null"))
                        {

                        }
                        edtMediaComment.setText("");
                        updateCommentList();

                    }
                }
                catch (JSONException | ParseException e)
                {
                    notifySimple(getString(R.string.msg_invalid_response_from_server));
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
            }
        }
        Helper.IS_DIALOG_SHOW = true;

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        applyThemeColor();
    }

    public void addUserCommentInLinearLayout(ArrayList<UserComment> usersCommentsList)
    {
        ArrayList<String> dateLabelList = new ArrayList<>();
        commentLinearLayout.removeAllViews();
        for (int i = 0; i < usersCommentsList.size(); i++)
        {
            int position = i;
            View layoutView = View.inflate(this, R.layout.partial_comment_user, null);
            CommentHolder commentHolder = new CommentHolder(layoutView);
            UserComment userComment = usersCommentsList.get(i);
            commentHolder.labelUserName.setText(userComment.user.name);
            commentHolder.labelComment.setText(userComment.comment);
            Helper.setupTypeface(commentHolder.linearParent, Helper.robotoRegularTypeface);
            Helper.setupTypeface(layoutView, Helper.robotoRegularTypeface);
            commentHolder.linearChild.setLayoutParams(new LinearLayout.LayoutParams(Helper.getDeviceWidth(this) / 2, ViewGroup.LayoutParams.WRAP_CONTENT));
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
                    //commentHolder.labelInsertDate.setText(strDate);

                    String onlyTime = new SimpleDateFormat("h:mm a").format(userComment.insertDate);
                    commentHolder.labelInsertDate.setText(onlyTime);


                    if (position == usersCommentsList.size() - 1)
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
                            commentHolder.textDateLabel.setText(getString(R.string.label_today));
                        }
                        else if (strDateLabel.equalsIgnoreCase(strYesterday))
                        {
                            commentHolder.textDateLabel.setText(getString(R.string.label_yesterday));
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
            if (String.valueOf(PreferenceHelper.getUserContext(this)).equalsIgnoreCase(String.valueOf(userComment.userId)))
            {
                isMyMessage = true;
            }
            else
            {
                isMyMessage = false;
            }
        /* custom function to set alignment of view based on message type */
            setAlignment(commentHolder, isMyMessage);
            commentLinearLayout.addView(layoutView);
        }
        scrollComment.fullScroll(View.FOCUS_DOWN);
        scrollComment.post(new Runnable()
        {
            @Override
            public void run()
            {
                scrollComment.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void setAlignment(CommentHolder holder, boolean isMe)
    {
        if (isMe)
        {
            holder.imgUserRight.setVisibility(View.VISIBLE);
            holder.imgUserLeft.setVisibility(View.GONE);
            holder.linearChild.setBackgroundResource(R.drawable.in_message_bg);
            Helper.setupTypeface(holder.linearParent, Helper.robotoRegularTypeface);
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.in_message_bg, null);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, getResources().getColor(R.color.sender_bubble_Color));


            //holder.linearChild.setBackgroundColor(Color.parseColor(PreferenceHelper.getAppBackColor(context)));
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.linearChild.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;

            // layoutParams.width=100;

            if (Helper.isTablet(this))
            {
                layoutParams.width = Helper.getDeviceWidth(this) / 3;
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
            holder.imgUserRight.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)));
        }
        else
        {
            holder.imgUserRight.setVisibility(View.GONE);
            holder.imgUserLeft.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)));
            holder.imgUserLeft.setVisibility(View.VISIBLE);

            holder.linearChild.setBackgroundResource(R.drawable.out_message_bg);
            Drawable drawable1 = ResourcesCompat.getDrawable(getResources(), R.drawable.out_message_bg, null);
            drawable1 = DrawableCompat.wrap(drawable1);
            DrawableCompat.setTint(drawable1, getResources().getColor(R.color.receiver_bubble_Color));

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.linearChild.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            if (Helper.isTablet(this))
            {
                layoutParams.width = Helper.getDeviceWidth(this) / 3;
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

    @Override
    public void onPause()
    {
        super.onPause();
        Helper.IS_DIALOG_SHOW = true;
    }

    class CommentHolder
    {
        public LinearLayout linearChild;
        TextView textDateLabel;
        View divider_view;
        TextView labelUserName, labelInsertDate, labelComment;
        LinearLayout linearParent;
        ImageView imgUserRight, imgUserLeft;

        public CommentHolder(View itemView)
        {
            textDateLabel = (TextView) itemView.findViewById(R.id.textDateLabel);
            divider_view = itemView.findViewById(R.id.divider_view);
            labelUserName = (TextView) itemView.findViewById(R.id.labelUserName);
            imgUserLeft = (ImageView) itemView.findViewById(R.id.img_user_left);
            imgUserRight = (ImageView) itemView.findViewById(R.id.img_user_right);
            labelInsertDate = (TextView) itemView.findViewById(R.id.labelInsertDate);
            labelComment = (TextView) itemView.findViewById(R.id.labelComment);
            linearParent = (LinearLayout) itemView.findViewById(R.id.parentLayout);
            linearChild = (LinearLayout) itemView.findViewById(R.id.linearContentBackground);

        }
    }
}
