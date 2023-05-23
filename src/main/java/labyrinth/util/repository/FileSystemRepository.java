package labyrinth.util.repository;

import lombok.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a repository to obtain a set of objects. Data is read from a
 * JSON file that contains an array of JSON objects. The implementation uses a hashcode-based set.
 *
 * @param <T> the type of the elements
 */
public abstract class FileSystemRepository<T>
        extends JacksonJsonRepository {

    private Set<T> elements;
    private final Class<T> elementsClass;

    protected FileSystemRepository(
            @NonNull final Class<T> elementClass) {

        this.elementsClass = elementClass;
        this.elements = new HashSet<>();
    }

    /**
     * {@return the list of objects}
     */
    public Set<T> getAll() {
        return new HashSet<>(elements);
    }

    /**
     * Adds an element to the repository if it does net exist.
     *
     * @param element the element to be added
     * @return the view of the updated repository
     */
    public Set<T> addOne(
            @NonNull final T element) {

        elements.add(element);
        return getAll();
    }

    /**
     * Adds each element to the repository which does not exist.
     *
     * @param elements the elements to be added
     * @return the view of the updated repository
     */
    public Set<T> addMany(
            @NonNull final Collection<T> elements) {

        elements.forEach(this::addOne);
        return getAll();
    }

    /**
     * Replaces an element of the repository if it already exists. Otherwise adds an element to the repository.
     *
     * @param element the element to be replaced
     * @return the view of the updated repository
     */
    public Set<T> replaceOne(
            @NonNull final T element) {

        elements.remove(element);
        elements.add(element);
        return getAll();
    }

    /**
     * Replaces each element of the repository which already exists and adds each element to the repository which does not exist.
     *
     * @param elements the elements to be replaced
     * @return the view of the updated repository
     */
    public Set<T> replaceMany(
            @NonNull final Collection<T> elements) {

        this.elements.removeAll(elements);
        this.elements.addAll(elements);
        return getAll();
    }

    /**
     * Clears the repository.
     *
     * @return the view of the updated repository
     */
    public Set<T> clear() {
        this.elements.clear();
        return getAll();
    }

    /**
     * Clears the repository, then re-initializes it with the set of elements.
     *
     * @param elements the elements to be added
     * @return the view of the updated repository
     */
    public Set<T> replaceAll(
            @NonNull final Collection<T> elements) {

        clear();
        this.elements.addAll(elements);
        return getAll();
    }

    /**
     * Initializes the repository from a file.
     *
     * @param file the file which contains the JSON document
     * @return the view of the updated repository
     * @throws IOException if any I/O error occurs
     */
    public Set<T> loadFromFile(
            @NonNull final File file) throws IOException {

        elements = readSet(new FileInputStream(file), this.elementsClass);
        return elements;
    }

    /**
     * Saves the repository to a file.
     *
     * @param file the file which will contain the JSON document
     * @throws IOException if any I/O error occurs
     */
    public void saveToFile(
            @NonNull final File file) throws IOException {

        writeSet(new FileOutputStream(file), elements);
    }
}