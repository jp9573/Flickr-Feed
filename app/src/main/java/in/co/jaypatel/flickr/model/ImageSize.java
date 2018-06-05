package in.co.jaypatel.flickr.model;

/**
 * Created by Jay on 5.06.2018.
 */
public enum ImageSize {

    MEDIUM,
    LARGE;

    public String getValue() {
        if (this == MEDIUM) return "z";
        return "h";
    }

}