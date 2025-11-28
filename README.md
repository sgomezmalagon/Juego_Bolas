# Juego_Bolas

Juego_Bolas es un proyecto educativo en Java que simula bolas rebotando en pantalla usando la arquitectura MVC (Modelo-Vista-Controlador).
Este README explica qué hace el proyecto, cómo ejecutar, y cómo publicar el repositorio en GitHub.

Resumen rápido
- Ventana principal con área de juego (izquierda) y menú lateral (derecha).
- Bolas que se generan y rebotan con velocidad en píxeles/milisegundo.
- Hay una "habitación" central que solo admite una bola a la vez (sincronización con notify/notifyAll).
- Se puede crear una bola controlable (máx. 1) que se maneja con WASD y turbo con SPACE.
- Recursos de imagen en `src/imagenes/` (asteroid1, asteroid2, fondo).

Requisitos
- JDK 11+.
- PowerShell si quieres usar los scripts incluidos.
- `javac` y `java` en el PATH.

Estructura del proyecto
- src/
  - controller/   -> Controladores del juego
  - model/        -> Clases del modelo (Ball, PlayerBall, Room, GameModel...)
  - view/         -> Componentes Swing (GamePanel, MainFrame, SidePanel...)
  - imagenes/     -> Imágenes usadas por el juego
  - util/         -> Utilidades (Debug.java, etc.)
- run.ps1         -> Script para compilar y ejecutar en PowerShell
- publish.ps1     -> Script (creado por mí) para inicializar y hacer push a GitHub
- .gitignore      -> Ignora binarios y carpetas de salida

Cómo compilar y ejecutar (rápido)
1. Abrir PowerShell en la raíz del proyecto.
2. Ejecutar:

```powershell
./run.ps1
```

El script compila las fuentes y ejecuta la clase `Main`.

Ejecución manual (opcional)
- Compilar todas las fuentes en `src` y poner las clases en `out`:

```powershell
# Compilar manual (PowerShell)
Get-ChildItem -Recurse -Filter "*.java" -Path src | ForEach-Object { $_.FullName } | ForEach-Object { $files += "`"$_`"" }; javac -d out @($files)
```

- Ejecutar:

```powershell
java -cp out Main
```

Cómo publicar el proyecto en GitHub (pasos)
1. Asegúrate de tener `git` instalado y configurado.
2. Si ya no existe, añade un remoto con la URL de tu repo (ejemplo):

```powershell
# Reemplaza la URL por la tuya si es diferente
git remote add origin https://github.com/sgomezmalagon/Juego_Bolas.git
git branch -M main
git add .
git commit -m "Initial commit: Juego_Bolas"
git push -u origin main
```

> Si prefieres, ejecuta el script `publish.ps1` que incluye estos pasos y te pedirá credenciales si son necesarias.

`.gitignore` recomendado
- `out/`
- `*.class`
- `.idea/`
- `*.iml`
- `*.log`

Archivos añadidos (por mí)
- `publish.ps1` : script para inicializar repo y empujar a GitHub.
- `CONTRIBUTING.md` : guía corta para contribuir.
- `.gitignore` : reglas para ignorar ficheros generados.

Controles del juego
- WASD: mover la bola controlable.
- SPACE: turbo mientras se mantiene.
- Menú: generar bolas, crear bola jugable (solo 1) y eliminar todas.

Depuración
- Para ver logs (util.Debug) ejecuta con la propiedad `game.debug`:

```powershell
java -Dgame.debug=true -cp out Main
```

Notas del autor
- Código escrito con estilo de un estudiante de 2º DAM: claro, con comentarios útiles pero no excesivos.
- Si necesitas que reduzca o aumente el nivel de comentario dímelo.

Si quieres que yo haga el push por ti, explícame si puedes darme acceso remoto (no puedo usar tus credenciales). En su lugar, puedo generar los comandos y un script `publish.ps1` que tú ejecutes.

---
(README actualizado por petición del usuario)
