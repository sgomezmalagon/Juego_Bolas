/*
 * ImageAssets: carga imágenes por defecto (fondo y asteroides)
 */
package imagenes;

import view.ImageManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

public class ImageAssets {
    // Llama a este método al iniciar la app para cargar imágenes por defecto del classpath
    public static void loadDefaults() {
        // 1) Fondo (orden de preferencia)
        BufferedImage bg = loadFromClasspath("/imagenes/fondo.png");
        if (bg == null) {
            bg = loadFromClasspath("/imagenes/Space-Background-Image-2.png");
            if (bg == null) {
                bg = loadFromFileIfExists("src/imagenes/Space-Background-Image-2.png");
                if (bg == null) {
                    bg = loadFromFileIfExists("src/imagenes/fondo.png");
                }
            }
        }
        if (bg != null) ImageManager.setBackground(bg);

        // 2) Texturas de bola por defecto: intentamos varias (asteroides)
        ImageManager.clearBallTextures();
        BufferedImage a1 = loadFromClasspath("/imagenes/asteroid1.png");
        if (a1 == null) a1 = loadFromFileIfExists("src/imagenes/asteroid1.png");
        if (a1 != null) ImageManager.addBallTexture(a1);

        BufferedImage a2 = loadFromClasspath("/imagenes/asteroid2.png");
        if (a2 == null) a2 = loadFromFileIfExists("src/imagenes/asteroid2.png");
        if (a2 != null) ImageManager.addBallTexture(a2);

        // Si no hay ninguna cargada, mantenemos compatibilidad con bola.png
        if (ImageManager.getBallTexturesCount() == 0) {
            BufferedImage ball = loadFromClasspath("/imagenes/bola.png");
            if (ball == null) ball = loadFromFileIfExists("src/imagenes/bola.png");
            if (ball != null) ImageManager.addBallTexture(ball);
        }
    }

    // Pequeña utilidad para leer una imagen del classpath
    private static BufferedImage loadFromClasspath(String path) {
        try (InputStream is = ImageAssets.class.getResourceAsStream(path)) {
            if (is == null) return null; // no está el recurso
            return ImageIO.read(is);
        } catch (Exception e) {
            return null; // si falla, devolvemos null y la vista usa fallback
        }
    }

    // Leer desde archivo si existe
    private static BufferedImage loadFromFileIfExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) return null;
            return ImageIO.read(f);
        } catch (Exception e) {
            return null;
        }
    }
}
