package in.co.jaypatel.flickr.model.event;

import java.util.List;

import in.co.jaypatel.flickr.model.PhotoModel;

/**
 * Created by Jay on 5.06.2018.
 */
public class SearchEvent
        extends BaseServiceEvent<List<PhotoModel>> {

    public SearchEvent(List<PhotoModel> item, Throwable exception) {
        super(item, exception);
    }

}