package de.smac.smaccloud.base;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 *  This class is used to get requested output from the server
 */

@SuppressWarnings("unused")
public class NetworkResponse implements Parcelable
{

    private int statusCode;
    private HashMap<String, String> headers;
    private String response;
    private byte[] rawResponse;

    public NetworkResponse()
    {
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public HashMap<String, String> getHeaders()
    {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers)
    {
        this.headers = headers;
    }

    public String getResponse()
    {
        return response;
    }

    public void setResponse(String response)
    {
        this.response = response;
    }

    public byte[] getRawResponse()
    {
        return rawResponse;
    }

    public void setRawResponse(byte[] rawResponse)
    {
        this.rawResponse = rawResponse;
    }

    @SuppressWarnings("unchecked")
    protected NetworkResponse(Parcel in) {
        statusCode = in.readInt();
        headers = (HashMap<String, String>) in.readSerializable();
        response = in.readString();
        in.readByteArray(rawResponse);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(statusCode);
        dest.writeSerializable(headers);
        dest.writeString(response);
        dest.writeByteArray(rawResponse);
    }

    public static final Creator<NetworkResponse> CREATOR = new Creator<NetworkResponse>() {
        @Override
        public NetworkResponse createFromParcel(Parcel in) {
            return new NetworkResponse(in);
        }

        @Override
        public NetworkResponse[] newArray(int size) {
            return new NetworkResponse[size];
        }
    };
}
