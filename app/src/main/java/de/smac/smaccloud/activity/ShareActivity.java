package de.smac.smaccloud.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;
import com.pchmn.materialchips.views.ChipsInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.MediaFragment;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.User;

/**
 * Share file via email
 */
public class ShareActivity extends Activity implements View.OnClickListener
{
    public static final int PERMISSION_REQUEST_CONTACT = 101;
    public static final int REQUEST_CODE_SHARE = 1001;
    public static final int REQUEST_CODE_MEDIA_ATTACHMENT = 1101;
    public static final int REQUEST_CODE_SIGNATURE_EDIT = 1102;
    public static final int SEND_TYPE_LINK = 0;
    public static final String KEY_SELECTED_MEDIA = "SelectedMedia";
    public static ArrayList<Media> selectedAttachmentList;
    public LinearLayout parentLayout;
    public PreferenceHelper prefManager;
    Media media;
    Channel channel;
    MenuInflater inflater;
    User user;
    LinearLayout linearAttachList;
    TextView txtAttachment;
    ChipsInputEditText chipsInputEditText;
    RecyclerView chipRecyclerView;
    ImageView imageViewAttach, imageViewSignature, imageViewEditSignature;
    private ChipsInput chips_input_email_to;
    private EditText textSubject, textEmailBody, textEmailFrom, textSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PreferenceHelper(context);
        setContentView(R.layout.activity_share_file);

        media = this.getIntent().getParcelableExtra(MediaFragment.EXTRA_MEDIA);
        channel = this.getIntent().getParcelableExtra(MediaFragment.EXTRA_CHANNEL);
        setTitle(getString(R.string.share_via) + " ".concat(getString(R.string.app_name)));
        user = new User();
        user.id = PreferenceHelper.getUserContext(context);
        try
        {
            DataHelper.getUser(context, user);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        textEmailFrom.setText(user.email.toString());

        selectedAttachmentList = new ArrayList<>();
        linearAttachList.addView(addSelectedMedia(media));
        selectedAttachmentList.add(media);
        updateAttachmentLabel();

        chipRecyclerView = (RecyclerView) chips_input_email_to.findViewById(R.id.chips_recycler);
        chipsInputEditText = (ChipsInputEditText) chipRecyclerView.getChildAt(chipRecyclerView.getChildCount() - 1);
        chipRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener()
        {
            @Override
            public void onChildViewAttachedToWindow(View view)
            {

                if (chipsInputEditText != null)
                {
                    Helper.setupTypeface(chipsInputEditText, Helper.robotoRegularTypeface);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view)
            {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_share_file, menu);
        applyThemeColor();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_delete:
                break;

            case android.R.id.home:
                showLeaveMessage();
                break;

            case R.id.action_send:
                List<String> enteredEmails = new ArrayList<>();
                chipRecyclerView = (RecyclerView) chips_input_email_to.findViewById(R.id.chips_recycler);
                ChipsInputEditText chipsInputEditText = (ChipsInputEditText) chipRecyclerView.getChildAt(chipRecyclerView.getChildCount() - 1);
                for (ChipInterface chipInterface : chips_input_email_to.getSelectedChipList())
                {
                    enteredEmails.add(chipInterface.getLabel());
                }
                if (chipsInputEditText != null && chipsInputEditText.getEditableText() != null && !chipsInputEditText.getEditableText().toString().isEmpty())
                {
                    String emailText = chipsInputEditText.getEditableText().toString();
                    if (Helper.isEmailValid(emailText))
                    {
                        chips_input_email_to.addChip(emailText, "");
                        enteredEmails.add(emailText);
                        chipsInputEditText.getEditableText().clear();
                    }
                    else
                    {
                        Helper.showMessage(this, false, getString(R.string.msg_invalid_email));
                        break;
                    }
                }
                boolean isAttachMediaDownloaded = true;
                for (int i = 0; i < selectedAttachmentList.size(); i++)
                {
                    if (selectedAttachmentList.get(i).isDownloaded != 1)
                    {
                        isAttachMediaDownloaded = false;
                        break;
                    }
                }
                Helper.hideSoftKeyboard(this);
                if (user.id == -1)
                {
                    Helper.showMessage(this, false, getString(R.string.msg_re_login));
                }
                else if (enteredEmails.isEmpty())
                {
                    Helper.showMessage(this, false, getString(R.string.msg_enter_to_address));
                    chips_input_email_to.requestFocus();
                }
                else if (!Helper.checkEmailAddresses(enteredEmails))
                {
                    Helper.showMessage(this, false, getString(R.string.msg_invalid_email));
                }
                else if (textSubject.getText().toString().trim().isEmpty())
                {
                    Helper.showMessage(this, false, getString(R.string.msg_enter_subject));
                    textSubject.requestFocus();
                }
                else if (selectedAttachmentList.isEmpty())
                {
                    Helper.showMessage(this, false, getString(R.string.msg_select_media_for_share));
                }
                else if (!isAttachMediaDownloaded)
                {
                    Helper.showMessage(this, false, getString(R.string.download_file_first));
                }
                else if (prefManager.isDemoLogin())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.disable_sharing_title));
                    builder.setMessage(getString(R.string.disable_sharing_message));
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

                    sendMail(SEND_TYPE_LINK);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        linearAttachList = (LinearLayout) findViewById(R.id.child_attachment_list);
        chips_input_email_to = (ChipsInput) findViewById(R.id.chips_input_email_to);

        chips_input_email_to.addChipsListener(new ChipsInput.ChipsListener()
        {
            @Override
            public void onChipAdded(ChipInterface chipInterface, int i)
            {

            }

            @Override
            public void onChipRemoved(ChipInterface chipInterface, int i)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence)
            {
                if (charSequence.toString().endsWith(" ") || charSequence.toString().endsWith(","))
                {
                    String strEmail = String.valueOf(charSequence.subSequence(0, charSequence.length() - 1));
                    if (Helper.isEmailValid(strEmail))
                    {
                        chips_input_email_to.addChip(strEmail, "");
                    }
                }
            }
        });

        /* End new lib */

        textSubject = (EditText) findViewById(R.id.textSubject);
        textEmailBody = (EditText) findViewById(R.id.textEmailbody);
        textSignature = (EditText) findViewById(R.id.txt_signature);
        textEmailFrom = (EditText) findViewById(R.id.textEmailFrom);

        txtAttachment = (TextView) findViewById(R.id.txtAttachment);


        imageViewAttach = (ImageView) findViewById(R.id.img_attach);
        imageViewAttach.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)));
        imageViewSignature = (ImageView) findViewById(R.id.img_signature);
        imageViewEditSignature = (ImageView) findViewById(R.id.img_edit_signature);


        LinearLayout.LayoutParams linearLayout = new LinearLayout.LayoutParams(Helper.getDeviceWidth(this) / 7, Helper.getDeviceHeight(this) / 7);
        linearLayout.gravity = Gravity.RIGHT;
        imageViewSignature.setLayoutParams(linearLayout);


        textSignature.setText(PreferenceHelper.getSignature(context));
        textSignature.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(PreferenceHelper.getSignatureImage(context)))
            setImage(PreferenceHelper.getSignatureImage(context));

        if (!PreferenceHelper.getSignature(context).isEmpty())
        {
            Spannable bodySpannableString = new SpannableString(PreferenceHelper.getEmailBody(context) + "\n\n\n" + PreferenceHelper.getSignature(context));
            bodySpannableString.setSpan(new ForegroundColorSpan(Color.GRAY), bodySpannableString.toString().indexOf(PreferenceHelper.getSignature(context)), bodySpannableString.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textEmailBody.setText(bodySpannableString);
        }
        else if (!PreferenceHelper.getEmailBody(context).isEmpty())
        {
            textEmailBody.setText(PreferenceHelper.getEmailBody(context) + "\n\n\n");
        }

        imageViewAttach.setOnClickListener(this);
        imageViewEditSignature.setOnClickListener(this);
        textEmailFrom.setEnabled(false);
        applyThemeColor();
    }

    public void applyThemeColor()
    {
        updateParentThemeColor();
        Helper.setupTypeface(findViewById(R.id.parentLayout), Helper.robotoRegularTypeface);
        Helper.setupUI(ShareActivity.this, parentLayout, parentLayout);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(PreferenceHelper.getAppBackColor(context))));
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_material_vector);
            upArrow.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            toolbar.setTitleTextColor(Color.parseColor(PreferenceHelper.getAppColor(context)));
        }
        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_send_white, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.parseColor(PreferenceHelper.getAppColor(context)));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        applyThemeColor();
    }

    public void setImage(String path)
    {
        Glide.with(this)
                .load(Uri.fromFile(new File(path)))
                //.placeholder(R.drawable.ic_loding)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewSignature);
    }

    @Override
    protected void bindEvents()
    {
        super.bindEvents();
    }

    @Override
    public void onBackPressed()
    {
        showLeaveMessage();
    }

    /**
     * Convert string to JSONObject for mail CC
     *
     * @param value
     * @return
     * @throws JSONException
     */
    public JSONObject toJsonCc(String value) throws JSONException
    {
        JSONObject userLike = new JSONObject();
        userLike.put("CC", value);
        return userLike;
    }

    /**
     * Convert string to JSONObject for mail BCC
     *
     * @param value
     * @return
     * @throws JSONException
     */
    public JSONObject toJsonBcc(String value) throws JSONException
    {
        JSONObject userLike = new JSONObject();
        userLike.put("Bcc", value);
        return userLike;
    }

    /**
     * Convert string to JSONObject for mail To
     *
     * @param value
     * @return
     * @throws JSONException
     */
    public JSONObject toJsonTo(String value) throws JSONException
    {
        JSONObject userLike = new JSONObject();
        userLike.put("To", value);
        return userLike;
    }

    public void showLeaveMessage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(R.string.msg_sure_want_to_leave);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
                finish();
            }
        });
        builder.setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    public void askForContactPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(ShareActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(ShareActivity.this,
                        Manifest.permission.READ_CONTACTS))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShareActivity.this);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                    PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                }
                else
                {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(ShareActivity.this, new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CONTACT:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(ShareActivity.this, "Permission granted for contacts", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                }
                else
                {
                    Toast.makeText(ShareActivity.this, "No permission for contacts", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.img_attach:
                Intent i = new Intent(getApplicationContext(), ShareAttachmentActivity.class);
                i.putExtra(KEY_SELECTED_MEDIA, selectedAttachmentList);
                startActivityForResult(i, REQUEST_CODE_MEDIA_ATTACHMENT);
                break;

            case R.id.img_edit_signature:
                Intent editSignature = new Intent(getApplicationContext(), SetSignatureActivity.class);
                textSignature.setText(PreferenceHelper.getSignature(context));
                setImage(PreferenceHelper.getSignatureImage(context));
                startActivityForResult(editSignature, REQUEST_CODE_SIGNATURE_EDIT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_MEDIA_ATTACHMENT)
        {
            selectedAttachmentList = data.getExtras().getParcelableArrayList(ShareActivity.KEY_SELECTED_MEDIA);
            linearAttachList.removeAllViews();
            if (selectedAttachmentList != null && !selectedAttachmentList.isEmpty())
            {
                for (Media media : selectedAttachmentList)
                {
                    linearAttachList.addView(addSelectedMedia(media));
                }
            }
            updateAttachmentLabel();
        }
        else if (requestCode == REQUEST_CODE_SIGNATURE_EDIT)
        {
            textSignature.setText(PreferenceHelper.getSignature(context));
            setImage(PreferenceHelper.getSignatureImage(context));


        }
    }

    public View addSelectedMedia(Media media)
    {
        final View singleItemView = View.inflate(context, R.layout.attach_media_item_single, null);
        FrameLayout attach_media_item_layout_content = (FrameLayout) singleItemView.findViewById(R.id.attach_media_item_layout_content);
        final ImageView img_media = (ImageView) singleItemView.findViewById(R.id.img_media);
        TextView txt_name = (TextView) singleItemView.findViewById(R.id.txt_name);
        ImageView img_close = (ImageView) singleItemView.findViewById(R.id.img_close);
        img_close.setColorFilter(Color.parseColor(PreferenceHelper.getAppColor(context)));
        Helper.setupTypeface(singleItemView, Helper.robotoRegularTypeface);
        final ProgressBar progressBar = (ProgressBar) singleItemView.findViewById(R.id.progressTemp);

        txt_name.setText(media.name);
        final Uri imageUri = Uri.parse(media.icon);
        Glide.with(context)
                .load(imageUri)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>()
                {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
                    {
                        img_media.setImageBitmap(resource);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable)
                    {
                        super.onLoadFailed(e, errorDrawable);
                        img_media.setImageBitmap(null);
                        progressBar.setVisibility(View.GONE);
                    }
                });

        img_close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Media removingMedia = (Media) singleItemView.getTag();
                linearAttachList.removeView(singleItemView);
                selectedAttachmentList.remove(removingMedia);
                updateAttachmentLabel();
            }
        });

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            img_media.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Helper.getDeviceHeight(ShareActivity.this) / 4.5)));
            img_media.setScaleType(ImageView.ScaleType.FIT_XY);
            attach_media_item_layout_content.setLayoutParams
                    (new LinearLayout.LayoutParams((int) (Helper.getDeviceHeight(ShareActivity.this) / 3.5),
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else
        {
            // Landscape Mode
            img_media.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Helper.getDeviceHeight(ShareActivity.this) / 3.5)));
            img_media.setScaleType(ImageView.ScaleType.FIT_XY);
            attach_media_item_layout_content.setLayoutParams
                    (new LinearLayout.LayoutParams((int) (Helper.getDeviceHeight(ShareActivity.this) / 2.5),
                            ViewGroup.LayoutParams.WRAP_CONTENT));

        }

        singleItemView.setTag(media);
        return singleItemView;

    }

    public void updateAttachmentLabel()
    {
        txtAttachment.setText(getString(R.string.label_attachment) + " (" + selectedAttachmentList.size() + ")");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    private void sendMail(int shareType)
    {
        try
        {
            JSONObject objJsonPayload = new JSONObject();
            objJsonPayload.putOpt("UserId", user.id);
            objJsonPayload.putOpt("Subject", textSubject.getText().toString());
            objJsonPayload.putOpt("Body", textEmailBody.getText().toString());
            objJsonPayload.putOpt("Signature", textSignature.getText().toString());
            // IsAttach 0 = URL
            // IsAttach 1 = Attach File
            objJsonPayload.putOpt("IsAttach", shareType);
            // For To Address
            JSONArray jsonArrayToAddress = new JSONArray();

            if (chips_input_email_to.getSelectedChipList() != null)
            {
                for (int i = 0; i < chips_input_email_to.getSelectedChipList().size(); i++)
                    jsonArrayToAddress.put(new JSONObject().put("Email", chips_input_email_to.getSelectedChipList().get(i).getLabel()));
            }
            objJsonPayload.put("ToAddress", jsonArrayToAddress);

            // For CC Address
            JSONArray jsonArrayCCAddress = new JSONArray();
            if (!PreferenceHelper.getEmailCcAddress(context).isEmpty())
            {
                List<String> ccemail = new Gson().fromJson(PreferenceHelper.getEmailCcAddress(context), List.class);
                for (int i = 0; i < ccemail.size(); i++)
                    jsonArrayCCAddress.put(new JSONObject().put("Email", ccemail.get(i)));
            }
            objJsonPayload.put("CCAddress", jsonArrayCCAddress);

            // For BCC Address
            JSONArray jsonArrayBCCAddress = new JSONArray();
            if (!PreferenceHelper.getEmailBccAddress(context).isEmpty())
            {
                List<String> bccemail = new Gson().fromJson(PreferenceHelper.getEmailBccAddress(context), List.class);
                for (int i = 0; i < bccemail.size(); i++)
                    jsonArrayBCCAddress.put(new JSONObject().put("Email", bccemail.get(i)));
            }
            objJsonPayload.put("BCCAddress", jsonArrayBCCAddress);


            // For attachment file(media)
            JSONObject jsonObjectAttachmentList = new JSONObject();
            JSONArray jsonArrayMedia = new JSONArray();
            for (Media media : selectedAttachmentList)
            {
                JSONObject objMedia = new JSONObject();
                objMedia.put("Id", media.id);
                objMedia.put("ChannelId", DataHelper.getChannelId(this, media.id));
                objMedia.put("ParentId", media.parentId);
                objMedia.put("VersionId", media.currentVersionId);
                jsonArrayMedia.put(objMedia);
            }
            jsonObjectAttachmentList.put("Media", jsonArrayMedia);
            objJsonPayload.put("AttachmentList", jsonObjectAttachmentList);


            // TODO: 29-Jun-17 Pass IsAttach value dynamic
            postNetworkRequest(REQUEST_CODE_SHARE, DataProvider.ENDPOINT_SHARE, DataProvider.Actions.SEND_MESSAGE,
                    RequestParameter.urlEncoded("UserId", String.valueOf(user.id)), RequestParameter.urlEncoded("Subject", textSubject.getText().toString()),
                    RequestParameter.urlEncoded("Body", textEmailBody.getText().toString()), RequestParameter.urlEncoded("Signature", textSignature.getText().toString()), RequestParameter.urlEncoded("Org_Id", PreferenceHelper.getOrganizationId(context)),
                    RequestParameter.urlEncoded("IsAttach", String.valueOf(shareType)), RequestParameter.jsonArray("ToAddress", jsonArrayToAddress),
                    RequestParameter.jsonArray("CCAddress", jsonArrayCCAddress), RequestParameter.jsonArray("BCCAddress", jsonArrayBCCAddress),
                    RequestParameter.jsonObject("AttachmentList", jsonObjectAttachmentList));
            Log.e("Requiest>>", String.valueOf(objJsonPayload));

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
        if (requestCode == REQUEST_CODE_SHARE)
        {
            Log.e("Share-Response>>", response);
            if (status)
            {
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    int requestStatus = responseJson.optInt("Status");
                    if (requestStatus == 0)
                    {
                        Helper.showShareSuccessDialog(this);
                    }
                    else
                    {
                        if (responseJson.has("Message") && !responseJson.isNull("Message"))
                            Helper.showMessage(this, false, responseJson.optString("Message"));
                    }
                }
                catch (Exception ex)
                {
                    Log.e("Share>>", ex.getMessage());
                }
            }
            else
            {
                notifySimple(getString(R.string.msg_cannot_complete_request));
            }
        }
    }
}
