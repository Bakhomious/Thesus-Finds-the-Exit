package labyrinth.util.javafx;

import javafx.scene.image.Image;

/**
 * Provides an image storage for ordinal keys.
 */
public class OrdinalImageStorage implements ImageStorage<Integer> {

    private Image[] images;

    /**
     * Creates an {@code OrdinalImageStorage} object.
     *
     * @param path the path of the directory containing the images
     * @param filenames the filenames of the images
     */
    public OrdinalImageStorage(String path, String... filenames) {
        images = new Image[filenames.length];
        for (var i = 0; i < filenames.length; i++) {
            var url = String.format("%s/%s", path, filenames[i]);
            try {
                images[i] = new Image(url);
            } catch (Exception e) {
                // Failed to load image
            }
        }
    }

    /**
     * @param key the key of the image
     * {@return the image associated with the given key}
     */
    @Override
    public Image get(Integer key) {
        return images[key];
    }

}
