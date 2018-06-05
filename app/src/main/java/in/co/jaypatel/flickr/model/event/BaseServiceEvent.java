package in.co.jaypatel.flickr.model.event;

/**
 * Created by Jay on 5.06.2018.
 */
public class BaseServiceEvent<T> {

    public final T item;
    public final Throwable exception;

    BaseServiceEvent(T item, Throwable exception) {
        this.item = item;
        this.exception = exception;
    }

}