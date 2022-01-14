package socialnetwork.domain.utils;

import java.util.ArrayList;
import java.util.List;

public interface Observable {

    List<Observer> observers = new ArrayList<>();

    default void update() {
        for (int i=0;i< observers.size();i++) {
            observers.get(i).update();
        }
    }

    default void addObserver( final Observer observer) {
        observers.add(observer);
    }

    default void removeObserver( final Observer observer) {
        observers.remove(observer);
    }
}