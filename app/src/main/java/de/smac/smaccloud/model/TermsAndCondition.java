package de.smac.smaccloud.model;

/**
 * Created by S Soft on 1/23/2018.
 */

public class TermsAndCondition
{
    private String title;
    private String desc;

    public TermsAndCondition(String title, String desc)
    {
        this.title = title;
        this.desc = desc;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }


}
