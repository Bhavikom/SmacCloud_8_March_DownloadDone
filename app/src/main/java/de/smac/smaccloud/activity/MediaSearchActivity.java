package de.smac.smaccloud.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import de.smac.smaccloud.R;
import de.smac.smaccloud.adapter.MediaSearchAdapter;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.NetworkRequest;
import de.smac.smaccloud.base.NetworkResponse;
import de.smac.smaccloud.base.NetworkService;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.fragment.MediaFragment;
import de.smac.smaccloud.fragment.ShowdownloadProcessFragment;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.InterfaceStopDownload;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.RecentItem;
import de.smac.smaccloud.widgets.CircularTextView;
import de.smac.smaccloud.widgets.NonScrollListView;

import static de.smac.smaccloud.base.Helper.selectedChannelsList;
import static de.smac.smaccloud.base.Helper.selectedMediaTypeList;
import static de.smac.smaccloud.base.NetworkService.KEY_AUTHORIZATION;

public class MediaSearchActivity extends Activity implements View.OnClickListener, ShowdownloadProcessFragment.interfaceAsyncResponseDownloadProcess
{
    //public static ArrayList<Integer> selectedMediaTypeList;
    //public static ArrayList<String> selectedChannelsList;

    static MediaSearchAdapter searchAdapter;
    static EditText edt_search;

    static int selectedSortAttributeIndex;
    static CircularTextView txt_filter_total_items;
    public InterfaceStopDownload interfaceStopDownload;
    public FrameLayout layoutDynamicFrame;
    MenuInflater inflater;
    TextView btnSort;
    TextView btnFilter;
    NonScrollListView list_search_result;

    public static void updateSearchingList(Context context)
    {
        try
        {
            final ArrayList<Media> mediaFileList = new ArrayList<>();
            ArrayList<Media> allMediaList = new ArrayList<>();
            ArrayList<Media> mediaFileNameList = new ArrayList<>();
            DataHelper.getAllMediaList(context, mediaFileList);
            for (Media media : mediaFileList)
            {
                if (!media.type.equalsIgnoreCase("folder"))
                    allMediaList.add(media);

            }

            if (selectedChannelsList != null && !selectedChannelsList.isEmpty())
            {
                for (Media media : allMediaList)
                {
                    Channel channel = new Channel();
                    channel.id = DataHelper.getChannelId(context, media.id);
                    DataHelper.getChannel(context, channel);
                    if (selectedChannelsList.contains(channel.name))
                        mediaFileNameList.add(media);
                }
            }
            else
            {
                mediaFileNameList.addAll(allMediaList);
            }

            if (selectedMediaTypeList != null && !selectedMediaTypeList.isEmpty())
            {
                List<String> tempFileTypeSelectedList = new ArrayList<>();
                for (int selectedIndex : selectedMediaTypeList)
                {
                    String fileType = "";
                    if (selectedIndex == 0)
                        fileType = "image";
                    else if (selectedIndex == 1)
                        fileType = "pdf";
                    else if (selectedIndex == 2)
                        fileType = "audio";
                    else if (selectedIndex == 3)
                        fileType = "video";
                    tempFileTypeSelectedList.add(fileType.toLowerCase());
                }
                for (Iterator<Media> iterator = mediaFileNameList.iterator(); iterator.hasNext(); )
                {
                    Media media = iterator.next();
                    String mediaType = media.type;
                    if (!tempFileTypeSelectedList.contains(mediaType.split("/")[0]) && !tempFileTypeSelectedList.contains(mediaType.split("/")[1]))
                        iterator.remove();
                }
            }

            // Apply sorting
            switch (selectedSortAttributeIndex)
            {
                case 0:
                    Collections.sort(mediaFileNameList, new Comparator<Media>()
                    {
                        public int compare(Media m1, Media m2)
                        {
                            return m1.name.compareTo(m2.name);
                        }
                    });

                case 1:
                    Collections.sort(mediaFileNameList, new Comparator<Media>()
                    {
                        public int compare(Media m1, Media m2)
                        {
                            return m2.name.compareTo(m1.name);
                        }
                    });
                    break;
                case 2:
                    ArrayList<RecentItem> recentItems = new ArrayList<>();
                    DataHelper.getMostVisitedItems(context, recentItems);

                    int cnt = -1;
                    for (RecentItem item : recentItems)
                    {
                        int position = -1;
                        for (int i = 0; i < mediaFileNameList.size(); i++)
                        {
                            if (mediaFileNameList.get(i).id == item.id)
                            {
                                position = i;
                                cnt++;
                                break;
                            }
                        }
                        if (position != -1)
                        {
                            Collections.swap(mediaFileNameList, position, cnt);
                        }
                    }
                    break;
                case 3:
                    Collections.sort(mediaFileNameList, new Comparator<Media>()
                    {
                        public int compare(Media m1, Media m2)
                        {
                            return m2.insertDate.compareTo(m1.insertDate);
                        }
                    });
                    break;
            }


            searchAdapter.addMoreData(mediaFileNameList, edt_search.getText().toString());

            int totalFilters = selectedMediaTypeList.size() + selectedChannelsList.size();
            if (totalFilters > 0)
            {
                txt_filter_total_items.setText(String.valueOf(totalFilters));
                txt_filter_total_items.setVisibility(View.VISIBLE);
            }
            else
            {
                txt_filter_total_items.setText(String.valueOf(0));
                txt_filter_total_items.setVisibility(View.GONE);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(getString(R.string.label_search));
    }

    @Override
    protected void initializeComponents()
    {
        super.initializeComponents();
        selectedSortAttributeIndex = 0;
        //selectedMediaTypeList = new ArrayList<>();
        //selectedChannelsList = new ArrayList<>();

        edt_search = (EditText) findViewById(R.id.edt_search);
        edt_search.setTypeface(robotoLightTypeface);
        edt_search.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                searchAdapter.getFilter().filter(editable.toString());
            }
        });

        btnSort = (TextView) findViewById(R.id.btnSort);
        btnSort.setOnClickListener(this);

        btnFilter = (TextView) findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(this);

        txt_filter_total_items = (CircularTextView) findViewById(R.id.txt_filter_total_items);

        final ArrayList<Media> mediaFileList = new ArrayList<>();
        ArrayList<Media> allMediaList = new ArrayList<>();
        ArrayList<Media> mediaFileNameList = new ArrayList<>();
        try
        {
            DataHelper.getAllMediaList(this, mediaFileList);
            for (Media media : mediaFileList)
            {
                if (!media.type.equalsIgnoreCase("folder"))
                    allMediaList.add(media);

            }

            if (selectedChannelsList != null && !selectedChannelsList.isEmpty())
            {
                for (Media media : allMediaList)
                {
                    Channel channel = new Channel();
                    channel.id = DataHelper.getChannelId(this, media.id);
                    DataHelper.getChannel(this, channel);
                    if (selectedChannelsList.contains(channel.name))
                        mediaFileNameList.add(media);
                }
            }
            else
            {
                for (Media media : allMediaList)
                    mediaFileNameList.add(media);
            }
            Collections.sort(mediaFileNameList, new Comparator<Media>()
            {
                public int compare(Media m1, Media m2)
                {
                    return m1.name.compareTo(m2.name);
                }
            });

            list_search_result = (NonScrollListView) findViewById(R.id.list_search_result);
            searchAdapter = new MediaSearchAdapter(MediaSearchActivity.this, mediaFileNameList);
            list_search_result.setAdapter(searchAdapter);
            list_search_result.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (searchAdapter.resultArrayList.get(i).isDownloading == 1 || searchAdapter.resultArrayList.get(i).isDownloaded == 0)
                    {
                        //Helper.showToastMessage(MediaSearchActivity.this, getString(R.string.download_file_first));
                        try
                        {
                            downloadMediaFromURL(searchAdapter.resultArrayList.get(i));
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                    else
                    {
                        Helper.openFile(MediaSearchActivity.this, searchAdapter.resultArrayList.get(i));
                    }
                }
            });
            Helper.setupUI(this, findViewById(R.id.parentLayout), findViewById(R.id.parentLayout));
            layoutDynamicFrame = (FrameLayout) findViewById(R.id.layoutDynamicFrame);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        updateSearchingList(context);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnSort:
                final String[] sortAttributes = new String[]{"Ascending", "Descending", "Popularity", "Date & Time"};

                final BottomSheetDialog btmSheetSortDialog = new BottomSheetDialog(context);
                btmSheetSortDialog.setContentView(R.layout.search_bottomsheet_sort_list);
                ListView listViewSort = (ListView) btmSheetSortDialog.findViewById(R.id.listViewSort);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, sortAttributes)
                {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
                    {
                        View view = super.getView(position, convertView, parent);
                        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(android.R.id.text1);
                        if (selectedSortAttributeIndex == position)
                            checkedTextView.setChecked(true);
                        else
                            checkedTextView.setChecked(false);

                        return view;
                    }
                };

                listViewSort.setAdapter(arrayAdapter);
                listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        selectedSortAttributeIndex = position;
                        updateSearchingList(context);
                        btmSheetSortDialog.dismiss();
                    }
                });
                btmSheetSortDialog.show();
                break;

            case R.id.btnFilter:
                FilterDialog actionbarDialog = new FilterDialog();
                actionbarDialog.show(getSupportFragmentManager(), "filter_dialog");
                break;
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_search, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_cancel:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        layoutDynamicFrame.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed()
    {
        if (interfaceStopDownload != null)
        {
            interfaceStopDownload.stopDownload();
        }
        super.onBackPressed();
    }

    private void downloadMediaFromURL(final Media selectedMedia) throws ParseException, JSONException, UnsupportedEncodingException
    {
        NetworkRequest request;
        ArrayList<RequestParameter> parameters = new ArrayList<>();
        JSONObject payloadJson = new JSONObject();

        payloadJson.put("ChannelId", String.valueOf(DataHelper.getChannelId(this, selectedMedia.id)));
        payloadJson.put("UserId", String.valueOf(PreferenceHelper.getUserContext(this)));
        payloadJson.put("MediaId", String.valueOf(selectedMedia.id));
        payloadJson.put("VersionId", String.valueOf(selectedMedia.currentVersionId));

        NetworkService.RequestCompleteCallback callback;
        JSONObject requestJson = new JSONObject();
        requestJson.put("Action", DataProvider.Actions.GET_CHANNEL_MEDIA_CONTENT);
        requestJson.put("Payload", payloadJson);
        Log.e("JSON", requestJson.toString());
        request = new NetworkRequest(this);
        request.setBodyType(NetworkRequest.REQUEST_BODY_MULTIPART);
        request.setRequestType(NetworkRequest.REQUEST_TYPE_NORMAL);

        request.setRequestListener(new NetworkRequest.RequestListener()
        {
            @Override
            public void onRequestComplete(NetworkResponse networkResponse) throws JSONException
            {
                if (networkResponse.getStatusCode() == 200)
                {
                    JSONObject response = new JSONObject(networkResponse.getResponse());

                    if (response.optInt("Status") > 0)
                    {
                        if (response.optInt("Status") == 2113) // Status = 2113 means "USER_TOKEN_NOT_VALID"
                        {
                            NetworkRequest requestTokenNotValid = new NetworkRequest(MediaSearchActivity.this);
                            requestTokenNotValid.setBodyType(NetworkRequest.REQUEST_BODY_MULTIPART);
                            requestTokenNotValid.setRequestType(NetworkRequest.REQUEST_TYPE_NORMAL);
                            requestTokenNotValid.setRequestUrl(DataProvider.ENDPOINT_UPDATE_TOKEN);
                            //headerNameValuePairs.add(new BasicNameValuePair(KEY_LANGUAGE_HEADER_PARAM, Locale.getDefault().getLanguage()));
                            try
                            {
                                if (PreferenceHelper.getUserContext(MediaSearchActivity.this) != -1)
                                {
                                    int userId = PreferenceHelper.getUserContext(MediaSearchActivity.this);
                                    String token = PreferenceHelper.getToken(MediaSearchActivity.this) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
                                    ArrayList<BasicNameValuePair> headerNameValuePairs1 = new ArrayList<>();
                                    if (token != null && !token.isEmpty())
                                    {
                                        headerNameValuePairs1.add(new BasicNameValuePair(KEY_AUTHORIZATION, token));
                                        requestTokenNotValid.setHeaders(headerNameValuePairs1);
                                    }
                                }
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                            requestTokenNotValid.execute();
                            requestTokenNotValid.setRequestListener(new NetworkRequest.RequestListener()
                            {
                                @Override
                                public void onRequestComplete(NetworkResponse networkResponse)
                                {
                                    try
                                    {
                                        JSONObject objUpdateTokenResponse = new JSONObject(networkResponse.getResponse().toString());
                                        if (objUpdateTokenResponse.optInt("Status") > 0)
                                        {
                                            Toast.makeText(MediaSearchActivity.this, objUpdateTokenResponse.optString("Message"), Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            if (objUpdateTokenResponse.has("Payload"))
                                            {
                                                JSONObject objUpdateTokenPayload = objUpdateTokenResponse.getJSONObject("Payload");
                                                if (objUpdateTokenPayload.has("AccessToken") && !objUpdateTokenPayload.isNull("AccessToken"))
                                                {
                                                    PreferenceHelper.storeToken(MediaSearchActivity.this, objUpdateTokenPayload.optString("AccessToken"));
                                                    downloadMediaFromURL(selectedMedia);
                                                }
                                            }
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                    else
                    {
                        /* open new screen to show download process and then go to forward*/

                        ShowdownloadProcessFragment fragment = new ShowdownloadProcessFragment();
                        FragmentTransaction transaction = MediaSearchActivity.this.getSupportFragmentManager().beginTransaction();
                        Bundle arguments = new Bundle();
                        arguments.putString("url", response.optString("Payload"));
                        arguments.putParcelable(MediaFragment.EXTRA_MEDIA, selectedMedia);
                        Gson gson = new Gson();
                        String strMediaList = gson.toJson(Arrays.asList(selectedMedia));
                        arguments.putString("media_list", strMediaList);
                        fragment.setArguments(arguments);
                        transaction.replace(R.id.layoutDynamicFrame, fragment, fragment.getClass().getSimpleName());
                        transaction.addToBackStack(fragment.getClass().getSimpleName());
                        transaction.commit();
                        layoutDynamicFrame.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        request.setProgressMode(NetworkRequest.PROGRESS_MODE_DIALOG_SPINNER);
        request.setProgressMessage(getString(R.string.msg_please_wait));
        request.setRequestUrl(DataProvider.ENDPOINT_FILE);
        parameters = new ArrayList<>();
        parameters.add(RequestParameter.multiPart("Request", requestJson.toString()));
        request.setParameters(parameters);
        int userId = PreferenceHelper.getUserContext(MediaSearchActivity.this);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        String token = PreferenceHelper.getToken(MediaSearchActivity.this) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
        if (token != null && !token.isEmpty())
        {
            ArrayList<BasicNameValuePair> headerNameValuePairs = new ArrayList<>();
            headerNameValuePairs.add(new BasicNameValuePair(KEY_AUTHORIZATION, token));
            request.setHeaders(headerNameValuePairs);
        }
        request.execute();

        if (interfaceStopDownload != null)
        {
            interfaceStopDownload.stopDownload();
        }
    }

    @Override
    public void processFinish(String output)
    {
        layoutDynamicFrame.setVisibility(View.GONE);
    }

    public static class FilterDialog extends DialogFragment
    {
        View content;
        NonScrollListView list_file_types;
        NonScrollListView list_channel_names;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {
            content = inflater.inflate(R.layout.search_filter_dialog_layout, container, false);
            setupDialogToolbar();

            list_file_types = (NonScrollListView) content.findViewById(R.id.list_file_types);
            list_channel_names = (NonScrollListView) content.findViewById(R.id.list_channel_names);

            setupFileTypeList();
            setupChannelsList();

            return content;

        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_AppCompat_NoActionBar);
            return super.onCreateDialog(savedInstanceState);
        }

        public void setupDialogToolbar()
        {
            Toolbar toolbar = (Toolbar) content.findViewById(R.id.navigationBar);
            toolbar.setNavigationIcon(R.drawable.ic_back_white);
            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dismiss();
                }
            });
            toolbar.setTitle(getString(R.string.back));
        }

        public void setupFileTypeList()
        {
            final ArrayAdapter<String> fileTypesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_multichoice, new String[]{getString(R.string.image), getString(R.string.pdf), getString(R.string.audio), getString(R.string.video)})
            {
                @Override
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    //View v = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_multiple_choice, null);
                    //View v = super.getView(position, convertView, parent);
                    View v = LayoutInflater.from(getContext()).inflate(android.R.layout.select_dialog_multichoice, null);
                    TextView tv = (TextView) v.findViewById(android.R.id.text1);
                    //tv.setTextColor(Color.WHITE);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.title_medium));
                    tv.setTypeface(MediaSearchActivity.robotoLightTypeface);
                    tv.setSingleLine(true);
                    tv.setEllipsize(TextUtils.TruncateAt.END);
                    /*if (selectedMediaTypeList.contains(position))
                        tv.setChecked(true);
                    else
                        tv.setChecked(false);*/
                    return super.getView(position, v, parent);
                }
            };

            list_file_types.setAdapter(fileTypesAdapter);
            list_file_types.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            for (int i = 0; i < fileTypesAdapter.getCount(); i++)
            {
                if (selectedMediaTypeList.contains(i))
                    list_file_types.setItemChecked(i, true);
            }

            list_file_types.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    selectedMediaTypeList.clear();
                    int len = list_file_types.getCount();
                    SparseBooleanArray checked = list_file_types.getCheckedItemPositions();
                    for (int j = 0; j < len; j++)
                    {
                        if (checked.get(j))
                        {
                            selectedMediaTypeList.add(j);
                        }
                    }
                }
            });
        }

        public void setupChannelsList()
        {
            final ArrayAdapter<String> channelNamesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice, DataHelper.getChannelNames(getContext()))
            {
                @Override
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    //View v = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_multiple_choice, null);
                    View v = LayoutInflater.from(getContext()).inflate(android.R.layout.select_dialog_multichoice, null);
                    TextView tv = (TextView) v.findViewById(android.R.id.text1);
                    //tv.setTextColor(Color.WHITE);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.title_small));
                    tv.setTypeface(MediaSearchActivity.robotoLightTypeface);
                    tv.setSingleLine(true);
                    tv.setEllipsize(TextUtils.TruncateAt.END);
                    return super.getView(position, v, parent);
                }
            };
            list_channel_names.setAdapter(channelNamesAdapter);
            list_channel_names.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            for (int i = 0; i < channelNamesAdapter.getCount(); i++)
            {
                if (selectedChannelsList.contains(channelNamesAdapter.getItem(i)))
                    list_channel_names.setItemChecked(i, true);
            }
            list_channel_names.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    selectedChannelsList.clear();
                    int len = list_channel_names.getCount();
                    SparseBooleanArray checked = list_channel_names.getCheckedItemPositions();
                    for (int j = 0; j < len; j++)
                    {
                        if (checked.get(j))
                        {
                            String item = channelNamesAdapter.getItem(j);
                            selectedChannelsList.add(item);
                        }
                    }
                }
            });
        }

        @Override
        public void onDismiss(DialogInterface dialog)
        {
            updateSearchingList(getContext());
            super.onDismiss(dialog);
        }
    }
}
