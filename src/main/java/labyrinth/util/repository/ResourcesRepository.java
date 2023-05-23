package labyrinth.util.repository;

import lombok.NonNull;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * Represents a repository to obtain a list of objects. Data is read from a
 * static JSON file that contains an array of JSON objects. The file is loaded
 * via the classloader.
 *
 * @param <T> the type of the elements
 */
public abstract class ResourcesRepository<T> extends JacksonJsonRepository {

    private final Set<T> elements;

    /**
     * Creates a {@code Repository} object to obtain a list of objects of
     * {@code elementClass}. The classloader of {@code elementClass} is used to
     * read JSON data.
     *
     * @param elementClass represents the type of the elements
     * @param resourceName the name of the resource that contains JSON data to
     *                     be read
     */
    protected ResourcesRepository(
            @NonNull final Class<T> elementClass,
            @NonNull final String resourceName) {
        try {
            elements = Collections.unmodifiableSet(
                    readSet(elementClass.getResourceAsStream(resourceName), elementClass)
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError("Failed to load resource " + resourceName, e); // Can't happen
        }
    }
    /**
     * {@return the set of objects}
     */
    public Set<T> getAll() {
        return elements;
    }
}