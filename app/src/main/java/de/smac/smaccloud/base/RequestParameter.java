package de.smac.smaccloud.base;

import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * This class is use to generation request parameters
 */
@SuppressWarnings("unused")
public class RequestParameter {

    public static final String TYPE_JSON_OBJECT = "JsonObject";
    public static final String TYPE_JSON_ARRAY = "JsonArray";
    public static final String TYPE_MULTIPART = "type_multipart";
    public static final String TYPE_URL_ENCODED = "type_encoded";
    public static final String TYPE_BARE = "type_bare";

    private String name;
    private String value;
    private String type;
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private AbstractContentBody bodyPart;

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public JSONArray getJsonArray()
    {
        return jsonArray;
    }

    public JSONObject getJsonObject()
    {
        return jsonObject;
    }

    public AbstractContentBody getBodyPart()
    {
        return bodyPart;
    }

    public String getType()
    {
        return type;
    }

    private RequestParameter()
    {
    }

    public static RequestParameter jsonObject(String name, JSONObject value)
    {
        RequestParameter requestParameter = new RequestParameter();
        requestParameter.name = name;
        requestParameter.jsonObject = value;
        requestParameter.type = TYPE_JSON_OBJECT;
        return requestParameter;
    }

    public static RequestParameter jsonArray(String name, JSONArray value)
    {
        RequestParameter requestParameter = new RequestParameter();
        requestParameter.name = name;
        requestParameter.jsonArray = value;
        requestParameter.type = TYPE_JSON_ARRAY;
        return requestParameter;
    }

    public static RequestParameter multiPart(String name, String value) throws UnsupportedEncodingException
    {
        RequestParameter requestParameter = new RequestParameter();
        requestParameter.name = name;
        requestParameter.bodyPart = new StringBody(value,Charset.forName("UTF-8"));
        requestParameter.type = TYPE_MULTIPART;
        return requestParameter;
    }

    public static RequestParameter multiPartFile(String name, File file)
    {
        RequestParameter requestParameter = new RequestParameter();
        requestParameter.name = name;
        requestParameter.bodyPart = new FileBody(file);
        requestParameter.type = TYPE_MULTIPART;
        return requestParameter;
    }

    public static RequestParameter urlEncoded(String name, String value)
    {
        RequestParameter requestParameter = new RequestParameter();
        requestParameter.name = name;
        requestParameter.value = value;
        requestParameter.type = TYPE_URL_ENCODED;
        return requestParameter;
    }

    public static RequestParameter bare(String bareBody)
    {
        RequestParameter requestParameter = new RequestParameter();
        requestParameter.value = bareBody;
        requestParameter.type = TYPE_BARE;
        return requestParameter;
    }
}