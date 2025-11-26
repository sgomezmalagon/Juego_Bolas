Coloca aquí imágenes por defecto para cargar desde el classpath al inicio.

Nombres sugeridos (puedes usar otros si cambias el código):
- fondo.png  -> Imagen de fondo por defecto
- bola.png   -> Textura por defecto de las bolas

Cómo funciona:
- Main.java llama a imagenes.ImageAssets.loadDefaults() al arrancar.
- Este método intenta leer /imagenes/fondo.png y /imagenes/bola.png del classpath.
- Si existen, las guarda en ImageManager para que GamePanel las use.
- Si no existen, no pasa nada: el juego pinta fondo blanco y bolas de colores.

