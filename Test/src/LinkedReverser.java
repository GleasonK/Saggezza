/**
 * Created by Kevin Gleason on 5/20/2014.
 */
public interface LinkedReverser<T> {
    public LinkedReverser<T> add(T info);
    public LinkedReverser<T> remove();
    public LinkedReverser<T> remove(T info);
    public LinkedReverser<T> reverse();
    public String toString();
}
