package in.co.jaypatel.flickr.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jay on 5.06.2018.
 */
public class PhotoInfoModel
        extends BaseModel {

    private static final int BUFFER = 1000;

    public long id;
    private long dateuploaded;
    public int views;
    public ContentModel title;
    public ContentModel description;
    public OwnerModel owner;
    //public ContentModel comments;

    public String getFormattedDate() {
        return SimpleDateFormat.getDateInstance().format(new Date(dateuploaded * BUFFER));
    }

}