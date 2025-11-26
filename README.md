Juego_Bolas - cómo ejecutar y depurar

Requisitos:
- JDK 11+ instalado y `javac`/`java` en tu PATH.
- Windows PowerShell (el script `run.ps1` está pensado para PowerShell).

Ejecutar desde PowerShell (rápido):
1. Abre PowerShell en la carpeta del proyecto (donde está `run.ps1`).
2. Ejecuta:

```powershell
./run.ps1
```

Esto compila todas las fuentes en `src/` y las coloca en `out/`, luego ejecuta `Main`.

Activar debug (mensajes de `util.Debug`):
- Para ver logs de debug, ejecuta Java con la propiedad `game.debug=true`:

```powershell
javac -d out (compilación usual...)
java -Dgame.debug=true -cp out Main
```

Alternativa en un paso usando PowerShell (ejecuta la compilación y luego ejecuta con debug):

```powershell
./run.ps1; java -Dgame.debug=true -cp out Main
```

Notas:
- El proyecto usa Swing, así que la interfaz se ejecuta en una ventana gráfica.
- La pantalla inicial muestra los controles. Pulsa INICIAR para arrancar la simulación.
- Solo puede existir una bola controlable a la vez. Si intentas crear otra verás un mensaje.

Si quieres, puedo:
- Añadir un archivo `.bat` para ejecutar en CMD.
- Integrar la ejecución dentro de IntelliJ (configuración de Run/Debug).
- Añadir más opciones al script (limpiar `out/`, especificar clase main, ejecutar con opciones de JVM).
