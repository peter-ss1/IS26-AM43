package it.polimi.ingsw.am43.client.view.gui.components;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Package-local cache for JavaFX images loaded from classpath resources.
 *
 * @param <K> the key type used to identify cached images
 */
final class ResourceImageCache<K> {
    private final Map<K, Image> images;

    ResourceImageCache() {
        this.images = new HashMap<>();
    }

    Image get(K key, Function<K, String> pathFactory) {
        if (this.images.containsKey(key)) {
            return this.images.get(key);
        }
        try (InputStream stream = this.getClass().getResourceAsStream(pathFactory.apply(key))) {
            if (stream == null) {
                this.images.put(key, null);
                return null;
            }
            Image image = new Image(stream);
            this.images.put(key, image);
            return image;
        } catch (Exception e) {
            this.images.put(key, null);
            return null;
        }
    }
}
