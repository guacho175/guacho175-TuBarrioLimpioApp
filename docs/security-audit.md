# Auditoría de seguridad para publicación

Fecha: 2026-07-24
Alcance: estado actual, historial Git completo disponible localmente, dependencias Gradle, configuración Android, código Kotlin y documentación.

## Resumen ejecutivo

El repositorio remoto es público. Se encontró una clave de Google Maps versionada desde el commit inicial y presente en los seis commits originales. La clave fue retirada del código, el historial completo fue reescrito y la rama pública fue reemplazada. Un clon nuevo del remoto obtuvo 0 hallazgos con Gitleaks.

La clave debe considerarse comprometida y rotarse antes de reescribir el historial. Dado que el repositorio solo tiene una rama, seis commits, cero forks, cero estrellas, cero etiquetas y cero releases, la recomendación actual es conservar el repositorio y sanear su historial. Si se prefiere mantener el historial original como archivo privado, la alternativa es privatizarlo y publicar una copia nueva con historial limpio.

## Stack y estructura

- Aplicación Android nativa en Kotlin.
- Gradle con Kotlin DSL y catálogo de versiones.
- Actividades y layouts XML, con componentes Compose residuales.
- Retrofit/OkHttp/Gson, coroutines, Glide, Google Maps/Location y Lottie.
- Seis commits alcanzables en el repositorio local al momento de la auditoría.

## Hallazgos

### Crítico — clave de Google Maps en el historial

- Archivo: `app/src/main/AndroidManifest.xml`.
- Primera aparición confirmada: commit `49155b88` (línea histórica 82).
- Detección: regla `gcp-api-key` de Gitleaks.
- Estado local: retirada, sustituida por `MAPS_API_KEY` y eliminada del historial reescrito.
- Estado remoto: la rama pública fue reemplazada con el historial saneado y verificada desde un clon nuevo.
- Riesgo: la clave debe considerarse comprometida aunque tenga restricciones.
- Acción obligatoria: restringir, rotar y posteriormente eliminar la clave antigua.

### Alto — token de sesión sin cifrado

El token se persiste en `SharedPreferences` privadas. Deshabilitar backups reduce exposición, pero no protege un dispositivo comprometido. La migración a una solución respaldada por Android Keystore requiere cambios coordinados en el manejo de sesión y queda pendiente antes de considerar la app apta para producción.

### Alto — logging HTTP sensible

El interceptor estaba configurado en nivel `BODY` para todos los builds, lo que podía registrar contraseñas, tokens y datos enviados. Se limitó a builds debug, nivel `BASIC`, y se configuró la redacción del encabezado `Authorization`.

### Medio — backups de aplicación habilitados

La aplicación manejaba tokens con `android:allowBackup="true"` y reglas de ejemplo. Se deshabilitaron backups para impedir que preferencias de autenticación entren en copias de seguridad.

### Medio — configuración dependiente del entorno hardcodeada

La URL de API y el origen de imágenes estaban compilados directamente. Ahora se inyectan mediante `API_BASE_URL`, con HTTPS y `/` final validados durante la configuración Gradle.

### Medio — distribución pública no autorizada en README

El README enlazaba una APK de evaluación alojada en una ubicación personal de SharePoint. Se retiró el enlace para no exponer identidad, recursos privados o un binario desactualizado.

### Bajo — permiso obsoleto y superficie de manifiesto

Se retiró `READ_EXTERNAL_STORAGE`, innecesario con el selector de contenido usado, y una actividad declarada con una clase inexistente. También se deshabilitó tráfico HTTP en texto claro.

### Bajo — higiene del repositorio

Se eliminó el archivo accidental `tatus`, se amplió `.gitignore` según el stack y se retiró una traza directa de excepción. Persisten componentes de plantilla Compose y cobertura de pruebas mínima.

### Informativo — API histórica no resoluble

El dominio histórico configurado por la aplicación devolvió `SERVFAIL` en una consulta DNS pública realizada el 2026-07-24 y no pudo resolverse desde el entorno de auditoría. Esto indica que la API no era accesible por ese dominio durante la comprobación, pero no confirma que la infraestructura, base de datos o copias de seguridad hayan sido eliminadas. La baja debe verificarse directamente con el proveedor donde se alojó el backend.

## Validación del saneamiento local

Después de corregir la herencia del tema específico de Android 10, se ejecutaron las siguientes comprobaciones:

```powershell
.\gradlew.bat test lint assembleDebug
```

- Pruebas unitarias debug y release: correctas.
- Compilación `assembleDebug`: correcta.
- Android lint: 0 errores y 142 advertencias.
- Búsqueda de claves con patrón de Google en archivos rastreados del árbol de trabajo: 0 hallazgos.
- Gitleaks sobre el historial local reescrito: 0 hallazgos.
- Gitleaks sobre un clon nuevo del historial remoto: 0 hallazgos.

Las pruebas existentes son únicamente las de plantilla y no validan los flujos funcionales de la aplicación. Las advertencias de lint deben revisarse como deuda técnica, aunque no bloquean el build actual.

## Estado de la clave comprometida

La clave no aparece en ninguno de los proyectos accesibles desde la cuenta disponible durante el saneamiento. No fue posible recuperar el proyecto propietario para revocarla o revisar su uso. La aplicación y su backend están descontinuados y no se creó una clave de reemplazo.

La cadena debe seguir tratándose como comprometida. Si el proyecto de Google Cloud vuelve a estar accesible, se debe eliminar la clave antigua; no debe reutilizarse aunque el historial Git ya esté limpio.

## Limpieza del historial

Se creó un bundle local completo de respaldo y se reescribieron todos los commits con `git-filter-repo`, sustituyendo cualquier cadena con formato de clave de Google por un marcador inerte. El historial reescrito fue escaneado con Gitleaks 8.30.1 y obtuvo 0 hallazgos.

La publicación requiere reemplazar la rama remota mediante un push forzado. Los clones antiguos deben descartarse porque conservan los objetos Git originales. Agregar `.env` o el manifiesto a `.gitignore` por sí solo no habría borrado el contenido de commits existentes.

## Recomendación de publicación

Opción aplicada: conservar la URL actual y reescribir los seis commits originales. La ausencia de forks, etiquetas, releases y actividad pública reduce el costo de invalidar el historial. El push forzado fue autorizado y debe verificarse con un clon nuevo y un escaneo completo.

Alternativa conservadora: cambiar el repositorio actual a privado y crear uno público nuevo desde el árbol saneado, con un único commit inicial. Esta opción evita un push forzado, pero exige mantener privado o eliminar el repositorio histórico para que la clave no siga accesible.

## Checklist previo a publicación

- [ ] Clave histórica revocada; no fue posible recuperar el proyecto propietario.
- [x] Estrategia confirmada: reescritura del repositorio actual.
- [x] Gitleaks sin hallazgos en estado actual e historial local publicable.
- [x] Historial público reemplazado y verificado desde un clon nuevo.
- [ ] Restricciones de la nueva clave verificadas en Google Cloud.
- [ ] Migración del token a almacenamiento respaldado por Android Keystore evaluada.
- [x] Tests, lint y `assembleDebug` ejecutados localmente.
- [x] `.env` y archivos de firma ausentes del índice.
- [x] APK, logs, rutas locales y datos personales ausentes.
- [ ] Derechos de publicación de código y recursos confirmados.
- [ ] Licencia elegida conscientemente si se desea permitir reutilización.
