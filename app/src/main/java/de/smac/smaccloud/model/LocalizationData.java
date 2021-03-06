package de.smac.smaccloud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LocalizationData implements Parcelable
{
    public static final Creator<LocalizationData> CREATOR = new Creator<LocalizationData>()
    {
        public LocalizationData createFromParcel(Parcel source)
        {
            return new LocalizationData(source);
        }

        public LocalizationData[] newArray(int size)
        {
            return new LocalizationData[size];
        }
    };
    private String __type = "";
    private String Code = "";
    private String DeleteDate = "";
    private String InsertDate = "";
    private String IsDeleted = "";
    private String Language = "";
    private String LocalizationId = "";
    private String Message = "";
    private String Type = "";
    private String UpdateDate = "";
    public LocalizationData()
    {

    }

    private LocalizationData(Parcel in)
    {
        this.__type = in.readString();
        this.Code = in.readString();
        this.DeleteDate = in.readString();
        this.InsertDate = in.readString();
        this.IsDeleted = in.readString();
        this.Language = in.readString();
        this.LocalizationId = in.readString();
        this.Message = in.readString();
        this.Type = in.readString();
        this.UpdateDate = in.readString();
    }

    public static void extractFromJson(String jsonString, ArrayList<LocalizationData> localizationDatas)
    {
        Gson gson = new Gson();
        Type typedValue = new TypeToken<ArrayList<LocalizationData>>()
        {
        }.getType();
        ArrayList<LocalizationData> arrayList = gson.fromJson(jsonString, typedValue);
        localizationDatas.addAll(arrayList);
    }

    public String get__type()
    {
        return __type;
    }

    public void set__type(String __type)
    {
        this.__type = __type;
    }

    public String getCode()
    {
        return Code;
    }

    public void setCode(String code)
    {
        this.Code = code;
    }

    public String getDeleteDate()
    {
        return DeleteDate;
    }

    public void setDeleteDate(String deleteDate)
    {
        this.DeleteDate = deleteDate;
    }

    public String getInsertDate()
    {
        return InsertDate;
    }

    public void setInsertDate(String insertDate)
    {
        this.InsertDate = insertDate;
    }

    public String getIsDeleted()
    {
        return IsDeleted;
    }

    public void setIsDeleted(String isDeleted)
    {
        this.IsDeleted = isDeleted;
    }

    public String getLanguage()
    {
        return Language;
    }

    public void setLanguage(String language)
    {
        this.Language = language;
    }

    public String getLocalizationId()
    {
        return LocalizationId;
    }

    public void setLocalizationId(String localizationId)
    {
        this.LocalizationId = localizationId;
    }

    public String getMessage()
    {
        return Message;
    }

    public void setMessage(String message)
    {
        this.Message = message;
    }

    public String getType()
    {
        return Type;
    }

    public void setType(String type)
    {
        this.Type = type;
    }

    public String getUpdateDate()
    {
        return UpdateDate;
    }

    public void setUpdateDate(String updateDate)
    {
        this.UpdateDate = updateDate;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.__type);
        dest.writeString(this.Code);
        dest.writeString(this.DeleteDate);
        dest.writeString(this.InsertDate);
        dest.writeString(this.IsDeleted);
        dest.writeString(this.Language);
        dest.writeString(this.LocalizationId);
        dest.writeString(this.Message);
        dest.writeString(this.Type);
        dest.writeString(this.UpdateDate);

    }
}
