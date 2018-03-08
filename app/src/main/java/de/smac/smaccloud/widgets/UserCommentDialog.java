package de.smac.smaccloud.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import de.smac.smaccloud.R;
import de.smac.smaccloud.activity.DocumentViewerActivity;
import de.smac.smaccloud.activity.ImageViewerActivity;
import de.smac.smaccloud.activity.MediaActivity;
import de.smac.smaccloud.activity.VideoViewerActivity;
import de.smac.smaccloud.adapter.CommentAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.UserComment;

public class UserCommentDialog extends AlertDialog
{

    public EditText edtMediaComment;
    public PreferenceHelper prefManager;
    public Activity activity;
    public View content;
    public Context context;
    public LinearLayout parentLayout;
    TextView txtmediaid;
    private RecyclerView listComments;
    private Button btn_send;
    private ImageView imgBack;
    private ArrayList<UserComment> usersComments;

    public UserCommentDialog(final Activity activity, final Media media)
    {


        super(activity);
        this.activity = activity;
        prefManager = new PreferenceHelper(activity);
        LayoutInflater li = LayoutInflater.from(activity);
        content = li.inflate(R.layout.dialog_user_comment, null);
        setView(content);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        parentLayout = (LinearLayout) content.findViewById(R.id.parentLayout);
        listComments = (RecyclerView) content.findViewById(R.id.listComments);
        txtmediaid = (TextView) content.findViewById(R.id.txt_media_name);
        txtmediaid.setText(media.name);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        listComments.setLayoutManager(layoutManager);
        LinearLayout.LayoutParams linearLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Helper.getDeviceHeight(activity) / 2);
        listComments.setLayoutParams(linearLayout);
        btn_send = (Button) content.findViewById(R.id.btnSend);
        edtMediaComment = (EditText) content.findViewById(R.id.edtMediaComment);
        imgBack = (ImageView) content.findViewById(R.id.img_back);

        imgBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        usersComments = new ArrayList<>();
        try
        {
            DataHelper.getCommentsOnMedia(activity, media.id, usersComments, true);
            CommentAdapter commentAdapter = new CommentAdapter(activity, usersComments);
            listComments.setAdapter(commentAdapter);
            commentAdapter.notifyDataSetChanged();
        }
        catch (ParseException ex)
        {
            ex.printStackTrace();
        }

        btn_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                {
                    if (prefManager.isDemoLogin())
                    {
                        Helper.demoUserDialog(activity);

                    }
                    else
                    {

                        if (media.isDownloaded == 1)
                        {
                            dismiss();
                            Helper.showSimpleDialog(activity, activity.getString(R.string.download_file_first));

                        }
                        else
                        {
                            String comment = edtMediaComment.getText().toString().trim();
                            if ((comment.length() > 0) && !(comment.startsWith(" ")))
                            {
                                if (Helper.isNetworkAvailable(activity))
                                {
                                    if (activity.getClass().equals(MediaActivity.class))
                                    {
                                        ((MediaActivity) activity).mediaFragment.callCommentService(comment);
                                    }
                                    else if (activity.getClass().equals(ImageViewerActivity.class))
                                    {
                                        ((ImageViewerActivity) activity).callCommentService(comment);
                                    }
                                    else if (activity.getClass().equals(DocumentViewerActivity.class))
                                    {
                                        ((DocumentViewerActivity) activity).callCommentService(comment);
                                    }
                                    else if (activity.getClass().equals(VideoViewerActivity.class))
                                    {
                                        ((VideoViewerActivity) activity).callCommentService(comment);
                                    }
                                }
                                else
                                {
                                    UserComment userComment = new UserComment();
                                    userComment.isSynced = 1;
                                    userComment.fileId = media.id;
                                    userComment.userId = PreferenceHelper.getUserContext(activity);
                                    userComment.fileId = media.id;
                                    userComment.comment = comment;
                                    userComment.insertDate = new Date();
                                    userComment.addOfflineComments(activity);
                                    activity.notifySimple(comment);
                                    edtMediaComment.setText("");
                                    dismiss();
                                }
                            }
                            else
                            {
                                activity.notifySimple(activity.getString(R.string.msg_please_enter_comment));
                                edtMediaComment.setFocusable(true);
                                edtMediaComment.setError(activity.getString(R.string.msg_please_enter_comment));
                            }
                        }
                    }
                }
            }
        });

        /*txt_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });*/
        setupUI(activity, parentLayout);

    }


    public void setupUI(final android.app.Activity activity, View view)
    {
        if (!(view instanceof EditText))
        {
            view.setOnTouchListener(new View.OnTouchListener()
            {
                public boolean onTouch(View v, MotionEvent event)
                {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(parentLayout.getWindowToken(), 0);
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
            {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(activity, innerView);
            }
        }
    }


}
