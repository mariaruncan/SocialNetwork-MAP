package socialnetwork.domain;

import java.util.Objects;


/**
 * Define a Tuple o generic type entities
 * @param <E1> - tuple first entity type
 * @param <E2> - tuple second entity type
 */
public class Tuple<E1, E2> {
    private E1 e1;
    private E2 e2;

    /***
     *
     * @param e1
     * @param e2
     */
    public Tuple(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /***
     * getter for left entity
     * @return e1
     */
    public E1 getLeft() {
        return e1;
    }

    /**
     *
     * @param e1
     */
    public void setLeft(E1 e1) {
        this.e1 = e1;
    }

    /**
     * getter for right entity
     * @return e2
     */
    public E2 getRight() {
        return e2;
    }

    /**
     *
     * @param e2
     */
    public void setRight(E2 e2) {
        this.e2 = e2;
    }

    /***
     *
     * @return string representing the tuple
     */
    @Override
    public String toString() {
        return e1 + ", " + e2;

    }

    /***
     * verify if 2 tuples are equal
     * @param obj
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        return this.e1.equals(((Tuple) obj).e1) && this.e2.equals(((Tuple) obj).e2);
    }

    /***
     * hashcode
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}