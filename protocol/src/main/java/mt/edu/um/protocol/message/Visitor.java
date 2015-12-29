package mt.edu.um.protocol.message;

/**
 * Created by matthew on 28/12/2015.
 */
public interface Visitor {
    void visit(ConnectMessage connectMessage);
}
