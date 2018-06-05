package in.co.jaypatel.flickr.model.event;

import in.co.jaypatel.flickr.model.PhotoInfoModel;

/**
 * Created by Jay on 5.06.2018.
 */
public class DetailEvent
        extends BaseServiceEvent<PhotoInfoModel> {

    public DetailEvent(PhotoInfoModel item, Throwable exception) {
        super(item, exception);
    }

}