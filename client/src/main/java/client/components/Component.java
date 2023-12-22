package client.components;

import commons.Model;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public interface Component<T> {
    /**
     * Updates overview
     * Compares old object to new
     * and renders the changes on the existing layout
     * without creating a new one
     * @param updatedObject changed object
     */
    void updateOverview(T updatedObject);

    /**
     * Creates new layout to display
     * @param newObject object to be displayed
     */
    void createOverview(T newObject);
    /**
     * Delta function used for rendering new changes to the front-end design
     * It takes oldList and newList and splits the changes into three categories:
     * deleted changes
     * added changes
     * modified changes
     * This simplifies the process of rendering new Objects, because removed and added changes
     * are easy to display.
     * @param oldList old items
     * @param newList new items
     * @return Map with three entries: removed, modified and added
     * The key is String the value if HasMap of type T
     * @param <D> type of the object
     */
    default <D extends Model> Map<String, List<D>> getDelta(List<D> oldList,
                                                            List<D> newList) {
        List<D> added = new ArrayList<>();
        List<D> removed = new ArrayList<>();
        List<D> modified = new ArrayList<>();
        BiPredicate<List<D>, D> predicate = (lists, taskList) -> {
            for (D element : lists) {
                if (element.getId().equals(taskList.getId()))
                    return true;
            }
            return false;
        };

        // Find added elements
        for (D element : newList) {
            if (!predicate.test(oldList, element)) {
                added.add(element);
            }
        }

        // Find removed elements
        for (D element : oldList) {
            if (!predicate.test(newList, element)) {
                removed.add(element);
            }
        }

        for (D el : newList) {
            if(!added.contains(el) && !removed.contains(el) && !predicate.test(modified, el)){
                modified.add(el);
            }
        }

        for (D el : oldList) {
            if(!added.contains(el) && !removed.contains(el) && !predicate.test(modified, el)){
                modified.add(el);
            }
        }

        Map<String, List<D>> delta = new HashMap<>();
        delta.put("a", added);
        delta.put("r", removed);
        delta.put("m", modified);

        return delta;
    }

    /**
     * Loads a node from a given fxml file
     * @param url the url of the fxml file
     * @return the node that was loaded
     * @param <M> the type of node that will be loaded
     */
    default <M> M loadNode(String url) {
        FXMLLoader loader = new FXMLLoader();
        URL fxmlLocation = getClass().getResource(url);
        loader.setLocation(fxmlLocation);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        M node;
        try {
            node = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return node;
    }
}
