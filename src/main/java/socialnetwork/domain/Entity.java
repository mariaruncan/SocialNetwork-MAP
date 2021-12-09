package socialnetwork.domain;

import java.io.Serializable;

/***
 * entitate c implementeaza Serializable
 * @param <ID>
 */
public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;
    private ID id;

    /***
     * returneaza id
     * @return id
     */
    public ID getId() {
        return id;
    }

    /***
     * seteaza id
     * @param id
     */
    public void setId(ID id) {
        this.id = id;
    }
}