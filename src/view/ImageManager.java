package view;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Dimension;
import java.util.*;

// Clase sencilla para cargar y guardar imágenes usadas por la vista
// Comentado en plan estudiante: tiene métodos estáticos para guardar el fondo y la/s textura/s de bolas.
public class ImageManager {
    private static BufferedImage background; // imagen de fondo (original)

    // Ahora soportamos varias texturas de bolas (por ejemplo asteroid1/asteroid2)
    private static final List<BufferedImage> ballTextures = new ArrayList<>();

    // Cache: guardamos la versión escalada del fondo para el tamaño del panel actual
    private static Image cachedScaledBackground;
    private static Dimension cachedBgSize;

    // Cache: para cada radio, guardamos un array de imágenes escaladas por variante
    // clave: radio -> array de escalados (mismo orden que ballTextures)
    private static final Map<Integer, Image[]> ballScaledCache = new HashMap<>();

    // Cargar una imagen desde un archivo (se usa en controlador para cargar texturas)
    public static BufferedImage loadImage(File file) throws IOException {
        // Uso ImageIO porque es fácil y funciona con PNG/JPG
        return ImageIO.read(file);
    }

    // Fondo
    public static void setBackground(BufferedImage img) {
        // Guardamos la referencia y limpiamos cache para recalcular el escalado
        background = img;
        cachedScaledBackground = null;
        cachedBgSize = null;
    }
    public static BufferedImage getBackground() { return background; }

    // Texturas de bolas (múltiples)
    public static void clearBallTextures() {
        // Limpio las texturas y la cache de escalados
        ballTextures.clear();
        ballScaledCache.clear();
    }

    public static void addBallTexture(BufferedImage img) {
        if (img == null) return;
        // Añadimos la textura y forzamos recalculo de escalados
        ballTextures.add(img);
        ballScaledCache.clear(); // limpiar cache para recalcular escalados con el nuevo set
    }

    // Compatibilidad: si se usa un único set
    public static void setBallTexture(BufferedImage img) {
        clearBallTextures();
        addBallTexture(img);
    }

    // Compatibilidad: devolver la primera textura (si existe)
    public static BufferedImage getBallTexture() {
        return ballTextures.isEmpty() ? null : ballTextures.get(0);
    }

    public static int getBallTexturesCount() {
        return ballTextures.size();
    }

    // Obtener la textura de bola escalada para un radio y variante concreta
    public static Image getBallTextureForRadiusVariant(int radius, int variantIndex) {
        if (ballTextures.isEmpty()) return null;
        int count = ballTextures.size();
        int idx = ((variantIndex % count) + count) % count; // asegurar 0..count-1
        Image[] arr = ballScaledCache.get(radius);
        if (arr == null || arr.length != count) {
            // crear array y escalar todas las variantes a este radio una vez
            arr = new Image[count];
            int size = radius * 2;
            for (int i = 0; i < count; i++) {
                arr[i] = ballTextures.get(i).getScaledInstance(size, size, Image.SCALE_SMOOTH);
            }
            ballScaledCache.put(radius, arr);
        }
        return arr[idx];
    }

    // Fondo escalado cacheado
    public static Image getScaledBackground(int width, int height) {
        if (background == null) return null;
        if (cachedScaledBackground != null && cachedBgSize != null
                && cachedBgSize.width == width && cachedBgSize.height == height) {
            return cachedScaledBackground;
        }
        // Escalamos la imagen y guardamos en cache para reusar
        cachedScaledBackground = background.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        cachedBgSize = new Dimension(width, height);
        return cachedScaledBackground;
    }

    // Utilidad genérica: crear una imagen escalada (seguimos dejándola por compatibilidad)
    public static Image scale(Image img, int w, int h) {
        if (img == null) return null;
        return img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }
}
