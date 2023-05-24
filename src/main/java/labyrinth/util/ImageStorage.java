package labyrinth.util;

import javafx.scene.image.Image;

/**
 * Provides a generic interface for image storage.
 *
 * @param <T> the type of the key
 */
public interface ImageStorage<T> {

    Image get(T key);

}
