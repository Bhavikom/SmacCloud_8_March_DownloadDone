package de.smac.smaccloud.widgets;

import android.app.AlertDialog;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.smac.smaccloud.R;
import de.smac.smaccloud.base.Activity;
import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.model.Channel;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.MediaAllDownload;

/**
 * Show media search dialog
 */
public class SearchDialog extends AlertDialog
{
    Activity activity;
    View content;
    LinearLayout parentLayout;
    TextView txt_search_label;
    ImageView img_close_dialog;
    EditText edt_search;
    TextView label_file_types;
    TextView label_channels;
    TextView txt_file_types;
    TextView txt_channel_names;
    TextView txt_configure_filter;

    NonScrollListView list_list_search_filter;
    NonScrollListView list_file_types;
    NonScrollListView list_channel_names;

    ScrollView scrollView;
    NonScrollListView list_search_result;

    List<String> fileTypeSelectedList = new ArrayList<String>();
    List<String> channelNameSelectedList = new ArrayList<String>();
    SearchAdapter searchAdapter;
    private ArrayList<MediaAllDownload> arraylistDownloadList = new ArrayList<MediaAllDownload>();
    private ArrayList<String> searchAllData;

    public SearchDialog(final Activity activity)
    {
        super(activity, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);//@android:style/Theme.Holo.Light.NoActionBar.Fullscreen);
        this.activity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater li = LayoutInflater.from(activity);
        content = li.inflate(R.layout.dialog_channel_search, null);
        setView(content);

        parentLayout = (LinearLayout) content.findViewById(R.id.parentLayout);
        txt_search_label = (TextView) content.findViewById(R.id.txt_search_label);
        txt_search_label.setTypeface(activity.robotoLightTypeface);
        img_close_dialog = (ImageView) content.findViewById(R.id.img_close_dialog);
        img_close_dialog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });

        edt_search = (EditText) content.findViewById(R.id.edt_search);
        edt_search.setTypeface(activity.robotoLightTypeface);
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

        label_file_types = (TextView) content.findViewById(R.id.label_file_types);
        label_file_types.setText(activity.getString(R.string.label_file_types) + ":");
        label_channels = (TextView) content.findViewById(R.id.label_channels);
        label_channels.setText(activity.getString(R.string.label_channels) + ":");
        txt_file_types = (TextView) content.findViewById(R.id.txt_file_types);
        txt_channel_names = (TextView) content.findViewById(R.id.txt_channel_names);
        txt_configure_filter = (TextView) content.findViewById(R.id.txt_configure_filter);
        Helper.setupTypeface(parentLayout, Helper.robotoRegularTypeface);
        /*txt_configure_filter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.dialog_search_confirure_filter, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setView(popupView);

                ImageView btn_back = (ImageView) popupView.findViewById(R.id.btn_back);
                TextView txtTitle = (TextView) popupView.findViewById(R.id.txtTitle);
                txtTitle.setTypeface(activity.robotoLightTypeface, Typeface.BOLD);
                ListView list_search_filter = (ListView) popupView.findViewById(R.id.list_search_filter);

                final AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
                wmlp.x = 100;   //x position
                wmlp.y = 500;   //y position
                dialog.show();
                dialog.getWindow().setLayout(Helper.getDeviceWidth(activity) / 2, ViewGroup.LayoutParams.WRAP_CONTENT);

                btn_back.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        dialog.dismiss();
                    }
                });


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, new String[]{"File types", "Channels"})
                {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent)
                    {
                        //super.getView(position, convertView, parent);
                        View v = LayoutInflater.from(activity).inflate(android.R.layout.simple_list_item_1, null);
                        TextView tv = (TextView) v.findViewById(android.R.id.text1);
                        tv.setTextColor(Color.WHITE);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.title_small));
                        tv.setTypeface(activity.robotoLightTypeface);
                        tv.setSingleLine(true);
                        tv.setEllipsize(TextUtils.TruncateAt.END);
                        return super.getView(position, v, parent);
                    }
                };
                list_search_filter.setAdapter(adapter);
                list_search_filter.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        if (position == 0)
                        {
                            *//**
     * FILE TYPES
     *//*

                            final View popupView = layoutInflater.inflate(R.layout.dialog_search_confirure_filter, null);
                            final AlertDialog.Builder builderFileTypes = new AlertDialog.Builder(activity);
                            builderFileTypes.setView(popupView);
                            ImageView btn_back = (ImageView) popupView.findViewById(R.id.btn_back);
                            TextView txtTitle = (TextView) popupView.findViewById(R.id.txtTitle);
                            txtTitle.setText(activity.getString(R.string.label_file_types));
                            txtTitle.setTypeface(activity.robotoLightTypeface, Typeface.BOLD);
                            final ListView list_file_types = (ListView) popupView.findViewById(R.id.list_search_filter);

                            final AlertDialog dialogFileTypes = builderFileTypes.create();
                            dialogFileTypes.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogFileTypes.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            WindowManager.LayoutParams wmlp = dialogFileTypes.getWindow().getAttributes();
                            wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
                            wmlp.x = 100;   //x position
                            wmlp.y = 500;   //y position
                            dialogFileTypes.show();
                            dialogFileTypes.getWindow().setLayout(Helper.getDeviceWidth(activity) / 2, ViewGroup.LayoutParams.WRAP_CONTENT);

                            btn_back.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    dialogFileTypes.dismiss();
                                }
                            });


                            final ArrayAdapter<String> fileTypesAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_multiple_choice, new String[]{activity.getString(R.string.image), activity.getString(R.string.pdf), activity.getString(R.string.audio), activity.getString(R.string.video)})
                            {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent)
                                {
                                    View v = LayoutInflater.from(activity).inflate(android.R.layout.simple_list_item_multiple_choice, null);
                                    TextView tv = (TextView) v.findViewById(android.R.id.text1);
                                    tv.setTextColor(Color.WHITE);
                                    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.title_small));
                                    tv.setTypeface(activity.robotoLightTypeface);
                                    tv.setSingleLine(true);
                                    tv.setEllipsize(TextUtils.TruncateAt.END);
                                    return super.getView(position, v, parent);
                                }
                            };
                            list_file_types.setAdapter(fileTypesAdapter);
                            list_file_types.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                            for (int i = 0; i < fileTypesAdapter.getCount(); i++)
                            {
                                if (fileTypeSelectedList.contains(fileTypesAdapter.getItem(i)))
                                    list_file_types.setItemChecked(i, true);
                            }
                            list_file_types.setOnItemClickListener(new AdapterView.OnItemClickListener()
                            {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                                {
                                    fileTypeSelectedList.clear();
                                    int len = list_file_types.getCount();
                                    SparseBooleanArray checked = list_file_types.getCheckedItemPositions();
                                    for (int j = 0; j < len; j++)
                                    {
                                        if (checked.get(j))
                                        {
                                            String item = fileTypesAdapter.getItem(j);
                                            fileTypeSelectedList.add(item);
                                        }
                                    }
                                    if (fileTypeSelectedList.isEmpty())
                                        txt_file_types.setText(activity.getString(R.string.label_all));
                                    else
                                        txt_file_types.setText(TextUtils.join(", ", fileTypeSelectedList));

                                    updateSearchingList();
                                }
                            });

                        }
                        else if (position == 1)
                        {

                            *//**
     * CHANNEL NAMES
     *//*

                            final View popupView = layoutInflater.inflate(R.layout.dialog_search_confirure_filter, null);
                            final AlertDialog.Builder builderFileTypes = new AlertDialog.Builder(activity);
                            builderFileTypes.setView(popupView);
                            ImageView btn_back = (ImageView) popupView.findViewById(R.id.btn_back);
                            TextView txtTitle = (TextView) popupView.findViewById(R.id.txtTitle);
                            txtTitle.setText(activity.getString(R.string.label_channels));
                            txtTitle.setTypeface(activity.robotoLightTypeface, Typeface.BOLD);
                            final ListView list_channel_names = (ListView) popupView.findViewById(R.id.list_search_filter);

                            final AlertDialog dialogChannelNames = builderFileTypes.create();
                            dialogChannelNames.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogChannelNames.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            WindowManager.LayoutParams wmlp = dialogChannelNames.getWindow().getAttributes();
                            wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
                            wmlp.x = 100;   //x position
                            wmlp.y = 500;   //y position
                            dialogChannelNames.show();
                            dialogChannelNames.getWindow().setLayout(Helper.getDeviceWidth(activity) / 2, ViewGroup.LayoutParams.WRAP_CONTENT);

                            btn_back.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    dialogChannelNames.dismiss();
                                }
                            });


                            final ArrayAdapter<String> channelNamesAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_multiple_choice, DataHelper.getChannelNames(activity))
                            {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent)
                                {
                                    View v = LayoutInflater.from(activity).inflate(android.R.layout.simple_list_item_multiple_choice, null);
                                    TextView tv = (TextView) v.findViewById(android.R.id.text1);
                                    tv.setTextColor(Color.WHITE);
                                    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.title_small));
                                    tv.setTypeface(activity.robotoLightTypeface);
                                    tv.setSingleLine(true);
                                    tv.setEllipsize(TextUtils.TruncateAt.END);
                                    return super.getView(position, v, parent);
                                }
                            };
                            list_channel_names.setAdapter(channelNamesAdapter);
                            list_channel_names.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                            for (int i = 0; i < channelNamesAdapter.getCount(); i++)
                            {
                                if (channelNameSelectedList.contains(channelNamesAdapter.getItem(i)))
                                    list_channel_names.setItemChecked(i, true);
                            }
                            list_channel_names.setOnItemClickListener(new AdapterView.OnItemClickListener()
                            {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                                {
                                    channelNameSelectedList.clear();
                                    int len = list_channel_names.getCount();
                                    SparseBooleanArray checked = list_channel_names.getCheckedItemPositions();
                                    for (int j = 0; j < len; j++)
                                    {
                                        if (checked.get(j))
                                        {
                                            String item = channelNamesAdapter.getItem(j);
                                            channelNameSelectedList.add(item);
                                        }
                                    }
                                    if (channelNameSelectedList.isEmpty())
                                        txt_channel_names.setText(activity.getString(R.string.label_all));
                                    else
                                        txt_channel_names.setText(TextUtils.join(", ", channelNameSelectedList));

                                    updateSearchingList();
                                }
                            });

                        }
                    }
                });
            }
        });*/

        txt_configure_filter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (list_list_search_filter.getVisibility() != View.VISIBLE)
                {
                    list_list_search_filter.setVisibility(View.VISIBLE);
                }
                else
                {
                    list_list_search_filter.setVisibility(View.GONE);
                    list_file_types.setVisibility(View.GONE);
                    list_channel_names.setVisibility(View.GONE);
                }
            }
        });

        list_list_search_filter = (NonScrollListView) content.findViewById(R.id.list_list_search_filter);

        // Add title header
        View listSearchFilterView = View.inflate(activity, R.layout.header_filter_option_title, null);
        TextView text_title = (TextView) listSearchFilterView.findViewById(R.id.text_title);
        text_title.setText(activity.getString(R.string.label_select_filter));
        ImageView btn_back = (ImageView) listSearchFilterView.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                list_list_search_filter.setVisibility(View.GONE);
            }
        });
        list_list_search_filter.addHeaderView(listSearchFilterView);

        list_list_search_filter.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // position 0 = header view

                if (position == 1)
                {
                    /*FILE TYPES*/
                    final ArrayAdapter<String> fileTypesAdapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_multichoice, new String[]{activity.getString(R.string.image), activity.getString(R.string.pdf), activity.getString(R.string.audio), activity.getString(R.string.video)})
                    {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent)
                        {
                            View v = LayoutInflater.from(activity).inflate(android.R.layout.select_dialog_multichoice, null);
                            TextView tv = (TextView) v.findViewById(android.R.id.text1);
                            //tv.setTextColor(Color.WHITE);
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.title_small));
                            tv.setTypeface(activity.robotoLightTypeface);
                            tv.setSingleLine(true);
                            tv.setEllipsize(TextUtils.TruncateAt.END);
                            return super.getView(position, v, parent);
                        }
                    };
                    list_file_types.setAdapter(fileTypesAdapter);
                    list_file_types.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    for (int i = 0; i < fileTypesAdapter.getCount(); i++)
                    {
                        if (fileTypeSelectedList.contains(fileTypesAdapter.getItem(i)))
                            list_file_types.setItemChecked(i + 1, true);
                    }
                    list_file_types.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                        {
                            fileTypeSelectedList.clear();
                            int len = list_file_types.getCount();
                            SparseBooleanArray checked = list_file_types.getCheckedItemPositions();
                            for (int j = 1; j <= len; j++)
                            {
                                if (checked.get(j))
                                {
                                    String item = fileTypesAdapter.getItem(j - 1);
                                    fileTypeSelectedList.add(item);
                                }
                            }
                            if (fileTypeSelectedList.isEmpty())
                                txt_file_types.setText(activity.getString(R.string.label_all));
                            else
                                txt_file_types.setText(TextUtils.join(", ", fileTypeSelectedList));

                            updateSearchingList();
                        }
                    });

                    // Add title header
                    View listFileTypeView = View.inflate(activity, R.layout.header_filter_option_title, null);
                    TextView text_title = (TextView) listFileTypeView.findViewById(R.id.text_title);
                    text_title.setText(activity.getString(R.string.label_select_file_type));
                    ImageView btn_back = (ImageView) listFileTypeView.findViewById(R.id.btn_back);
                    btn_back.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            list_list_search_filter.setVisibility(View.VISIBLE);
                            list_file_types.setVisibility(View.GONE);
                        }
                    });
                    if (list_file_types.getHeaderViewsCount() == 0)
                        list_file_types.addHeaderView(listFileTypeView);

                    list_list_search_filter.setVisibility(View.GONE);
                    list_file_types.setVisibility(View.VISIBLE);
                }
                else if (position == 2)
                {
                    /*CHANNEL NAMES*/
                    final ArrayAdapter<String> channelNamesAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_multiple_choice, DataHelper.getChannelNames(activity))
                    {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent)
                        {
                            View v = LayoutInflater.from(activity).inflate(android.R.layout.simple_list_item_multiple_choice, null);
                            TextView tv = (TextView) v.findViewById(android.R.id.text1);
                            //tv.setTextColor(Color.WHITE);
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.title_small));
                            tv.setTypeface(activity.robotoLightTypeface);
                            tv.setSingleLine(true);
                            tv.setEllipsize(TextUtils.TruncateAt.END);
                            return super.getView(position, v, parent);
                        }
                    };
                    list_channel_names.setAdapter(channelNamesAdapter);
                    list_channel_names.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    for (int i = 0; i < channelNamesAdapter.getCount(); i++)
                    {
                        if (channelNameSelectedList.contains(channelNamesAdapter.getItem(i)))
                            list_channel_names.setItemChecked(i, true);
                    }
                    list_channel_names.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                        {
                            channelNameSelectedList.clear();
                            int len = list_channel_names.getCount();
                            SparseBooleanArray checked = list_channel_names.getCheckedItemPositions();
                            for (int j = 1; j <= len; j++)
                            {
                                if (checked.get(j))
                                {
                                    String item = channelNamesAdapter.getItem(j - 1);
                                    channelNameSelectedList.add(item);
                                }
                            }
                            if (channelNameSelectedList.isEmpty())
                                txt_channel_names.setText(activity.getString(R.string.label_all));
                            else
                                txt_channel_names.setText(TextUtils.join(", ", channelNameSelectedList));

                            updateSearchingList();
                        }
                    });

                    // Add title header
                    View listChannelsView = View.inflate(activity, R.layout.header_filter_option_title, null);
                    TextView text_title = (TextView) listChannelsView.findViewById(R.id.text_title);
                    text_title.setText(activity.getString(R.string.label_select_channel));
                    ImageView btn_back = (ImageView) listChannelsView.findViewById(R.id.btn_back);
                    btn_back.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            list_list_search_filter.setVisibility(View.VISIBLE);
                            list_channel_names.setVisibility(View.GONE);
                        }
                    });
                    if (list_channel_names.getHeaderViewsCount() == 0)
                        list_channel_names.addHeaderView(listChannelsView);

                    list_list_search_filter.setVisibility(View.GONE);
                    list_channel_names.setVisibility(View.VISIBLE);
                }
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, new String[]{activity.getString(R.string.label_file_types), activity.getString(R.string.channels)})
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                //super.getView(position, convertView, parent);
                View v = LayoutInflater.from(activity).inflate(android.R.layout.simple_list_item_1, null);
                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.title_small));
                tv.setTypeface(activity.robotoLightTypeface);
                tv.setSingleLine(true);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                return super.getView(position, v, parent);
            }
        };
        list_list_search_filter.setAdapter(adapter);
        list_file_types = (NonScrollListView) content.findViewById(R.id.list_file_types);
        list_channel_names = (NonScrollListView) content.findViewById(R.id.list_channel_names);

        list_search_result = (NonScrollListView) content.findViewById(R.id.list_search_result);
        scrollView = (ScrollView) content.findViewById(R.id.scrollView);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Helper.getDeviceHeight(activity) / 1.5)));
        //list_search_result.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (Helper.getDeviceHeight(activity) / 1.5)));


        searchAllData = new ArrayList<>();
        final ArrayList<Media> mediaFileList = new ArrayList<>();
        ArrayList<Media> allMediaList = new ArrayList<>();
        ArrayList<Media> mediaFileNameList = new ArrayList<>();
        try
        {
            DataHelper.getAllMediaList(activity, mediaFileList);
            for (Media media : mediaFileList)
            {
                if (!media.type.equalsIgnoreCase("folder"))
                    allMediaList.add(media);

            }

            if (channelNameSelectedList != null && !channelNameSelectedList.isEmpty())
            {
                for (Media media : allMediaList)
                {
                    Channel channel = new Channel();
                    channel.id = DataHelper.getChannelId(activity, media.id);
                    DataHelper.getChannel(activity, channel);
                    if (channelNameSelectedList.contains(channel.name))
                        mediaFileNameList.add(media);
                }
            }
            else
            {
                for (Media media : allMediaList)
                    mediaFileNameList.add(media);
            }

            searchAdapter = new SearchAdapter(mediaFileNameList);
            list_search_result.setAdapter(searchAdapter);
            list_search_result.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    if (searchAdapter.resultArrayList.get(i).isDownloading == 1 || searchAdapter.resultArrayList.get(i).isDownloaded == 0)
                        Helper.showToastMessage(activity, activity.getString(R.string.download_file_first));
                    else
                        Helper.openFile(activity, searchAdapter.resultArrayList.get(i));
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        setupUI(activity, parentLayout);
    }

    public void updateSearchingList()
    {
        try
        {
            final ArrayList<Media> mediaFileList = new ArrayList<>();
            ArrayList<Media> allMediaList = new ArrayList<>();
            ArrayList<Media> mediaFileNameList = new ArrayList<>();
            DataHelper.getAllMediaList(activity, mediaFileList);
            for (Media media : mediaFileList)
            {
                if (!media.type.equalsIgnoreCase("folder"))
                    allMediaList.add(media);

            }

            if (channelNameSelectedList != null && !channelNameSelectedList.isEmpty())
            {
                for (Media media : allMediaList)
                {
                    Channel channel = new Channel();
                    channel.id = DataHelper.getChannelId(activity, media.id);
                    DataHelper.getChannel(activity, channel);
                    if (channelNameSelectedList.contains(channel.name))
                        mediaFileNameList.add(media);
                }
            }
            else
            {
                mediaFileNameList.addAll(allMediaList);
            }

            if (fileTypeSelectedList != null && !fileTypeSelectedList.isEmpty())
            {
                List<String> tempFileTypeSelectedList = new ArrayList<>();
                for (String fileName : fileTypeSelectedList)
                {
                    if (fileName.equalsIgnoreCase(activity.getString(R.string.image)))
                        fileName = "image";
                    else if (fileName.equalsIgnoreCase(activity.getString(R.string.pdf)))
                        fileName = "pdf";
                    else if (fileName.equalsIgnoreCase(activity.getString(R.string.audio)))
                        fileName = "audio";
                    else if (fileName.equalsIgnoreCase(activity.getString(R.string.video)))
                        fileName = "video";
                    tempFileTypeSelectedList.add(fileName.toLowerCase());
                }
                for (Iterator<Media> iterator = mediaFileNameList.iterator(); iterator.hasNext(); )
                {
                    Media media = iterator.next();
                    String mediaType = media.type;
                    // Store selected file types in english language for compare with media.type object in list of medias
                    /*if (PreferenceHelper.getSelectedLanguage(activity).equalsIgnoreCase("de"))
                    {
                        if (mediaType.equalsIgnoreCase(activity.getString(R.string.image)))
                        {
                            mediaType = "image"; // Helper.getStringByLocal(activity, R.string.image, "en-en");
                        }
                        else if (mediaType.equalsIgnoreCase(activity.getString(R.string.pdf)))
                        {
                            mediaType = "pdf"; // Helper.getStringByLocal(activity, R.string.pdf, "en-en");
                        }
                        else if (mediaType.equalsIgnoreCase(activity.getString(R.string.audio)))
                        {
                            mediaType = Helper.getStringByLocal(activity, R.string.audio, "en-en");
                        }
                        else if (mediaType.equalsIgnoreCase(activity.getString(R.string.video)))
                        {
                            mediaType = "video"; // Helper.getStringByLocal(activity, R.string.video, "en-en");
                        }
                    }*/
                    if (!tempFileTypeSelectedList.contains(mediaType.split("/")[0]) && !tempFileTypeSelectedList.contains(mediaType.split("/")[1]))
                        iterator.remove();
                }
            }

            searchAdapter.addMoreData(mediaFileNameList);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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

    private class SearchAdapter extends BaseAdapter implements Filterable
    {

        LayoutInflater inflater;
        private ArrayList<String> data;
        private ArrayList<Media> typeAheadData;
        private ArrayList<Media> resultArrayList;

        public SearchAdapter(ArrayList<Media> allData)
        {
            inflater = LayoutInflater.from(activity);
            data = new ArrayList<>();
            resultArrayList = new ArrayList<>();
            typeAheadData = new ArrayList<>();
            typeAheadData.addAll(allData);
        }

        public void addMoreData(ArrayList<Media> allData)
        {
            typeAheadData.clear();
            typeAheadData.addAll(allData);
            getFilter().filter(edt_search.getText().toString());
            notifyDataSetChanged();
        }


        @Override
        public Filter getFilter()
        {
            Filter filter = new Filter()
            {
                @Override
                protected FilterResults performFiltering(CharSequence constraint)
                {
                    FilterResults filterResults = new FilterResults();
                    if (!TextUtils.isEmpty(constraint))
                    {
                        // Retrieve the autocomplete results.
                        List<String> searchData = new ArrayList<>();
                        resultArrayList.clear();
                        for (Media media : typeAheadData)
                        {

                            //for (String str :typeAheadData)
                            {
                                if (media.name.toLowerCase().contains(constraint.toString().toLowerCase()))
                                {
                                    searchData.add(media.name);
                                    resultArrayList.add(media);
                                }
                            }
                        }

                        // Assign the data to the FilterResults
                        filterResults.values = searchData;
                        filterResults.count = searchData.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results)
                {
                    if (results.values != null)
                        data = (ArrayList<String>) results.values;
                    else data.clear();
                    notifyDataSetChanged();

                }
            };
            return filter;
        }

        @Override
        public int getCount()
        {
            return data.size();
        }

        @Override
        public Object getItem(int position)
        {
            return data.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            MyViewHolder mViewHolder;

            if (convertView == null)
            {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                mViewHolder = new MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            }
            else
            {
                mViewHolder = (MyViewHolder) convertView.getTag();
            }

            String currentListData = (String) getItem(position);

            mViewHolder.textView.setText(currentListData);

            return convertView;
        }


        private class MyViewHolder
        {
            TextView textView;

            public MyViewHolder(View convertView)
            {
                textView = (TextView) convertView.findViewById(android.R.id.text1);
                textView.setTypeface(activity.robotoLightTypeface);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.title_very_small));
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.END);
            }
        }
    }

}
