# Tu Barrio Limpio App

Aplicación Android nativa desarrollada como proyecto académico para registrar y consultar denuncias ciudadanas de microbasurales. La app consume una API REST externa y permite adjuntar evidencia fotográfica y ubicación.

> **Estado:** proyecto descontinuado, conservado como pieza de portafolio y referencia técnica. El backend original ya no está disponible y la aplicación no se distribuye como producto operativo.

## Alcance del repositorio

Este repositorio contiene únicamente el cliente Android. Su objetivo actual es documentar la solución implementada, su arquitectura y las mejoras de seguridad aplicadas después del desarrollo académico.

- El código compila con valores de configuración no funcionales y seguros.
- Para probar los flujos conectados se necesita una API compatible propia.
- El mapa requiere una clave nueva de Google Maps restringida a la aplicación.
- No se ofrece soporte, servicio alojado ni APK de producción.

## Funcionalidades implementadas

- Registro e inicio de sesión.
- Consulta del perfil del usuario.
- Listado y detalle de denuncias propias.
- Registro de denuncias con descripción, dirección, coordenadas e imagen.
- Visualización de denuncias en un mapa.
- Listado y marcado de notificaciones.
- Captura de imágenes con cámara o selección desde la galería.

## Arquitectura y stack

El proyecto separa modelos, acceso a red, repositorios y pantallas. Las pantallas XML y actividades Android consumen la API mediante Retrofit; algunas operaciones de registro y denuncia utilizan `ViewModel`.

- Kotlin y Android SDK (mínimo 24, objetivo 35).
- Gradle 8.7 con Kotlin DSL.
- AndroidX, Material Components y layouts XML.
- Retrofit, OkHttp y Gson.
- Kotlin Coroutines.
- Google Maps y Play Services Location.
- Glide y Lottie.

```text
app/src/main/
├── java/com/example/tubarriolimpioapp/
│   ├── data/          # Modelos, API y repositorios
│   ├── ui/            # Actividades, loaders, adapters y tema
│   └── utils/         # Validadores y utilidades
├── res/               # Layouts, drawables, menús y valores
└── AndroidManifest.xml
```

## Requisitos

- JDK 17.
- Android Studio o Android SDK con la plataforma 35.
- Un emulador o dispositivo con Android 7.0 (API 24) o superior.
- Acceso a una instancia compatible de la API REST.
- Una clave de Google Maps SDK for Android restringida al paquete y certificado de firma de la app.

## Configuración local

1. Clona el repositorio.
2. Copia `.env.example` como `.env`.
3. Reemplaza los valores ficticios:

```env
API_BASE_URL=https://api.example.com/api/
MAPS_API_KEY=replace_with_restricted_google_maps_key
```

`API_BASE_URL` debe usar HTTPS y terminar en `/`. Ambas variables son necesarias para utilizar todas las funciones; sin ellas el proyecto compila con valores seguros no funcionales. Gradle también acepta las variables desde el entorno del sistema o propiedades `-P`, con ese orden de prioridad.

El archivo `.env` es local y está ignorado por Git. Una clave de Google Maps incluida en una APK puede extraerse, por lo que debe restringirse en Google Cloud por nombre de paquete, huella del certificado de firma y API permitida.

### Firma de la versión release

La configuración de firma se carga fuera del repositorio desde:

```text
%USERPROFILE%\.android\keystores\tubarriolimpio\signing.properties
```

El almacén de firma y sus contraseñas no deben copiarse al repositorio. Para conservar la posibilidad de publicar actualizaciones compatibles, guarda una copia segura del almacén y su configuración en un gestor de contraseñas o respaldo cifrado.

## Desarrollo, pruebas y build

En Windows:

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
```

En macOS o Linux:

```bash
./gradlew test
./gradlew lint
./gradlew assembleDebug
```

El APK de depuración se genera bajo `app/build/outputs/apk/debug/`. Las pruebas instrumentadas requieren un dispositivo o emulador:

```powershell
.\gradlew.bat connectedAndroidTest
```

## Endpoints consumidos

La app espera endpoints relativos a `API_BASE_URL` para:

- `POST usuarios/login/`
- `POST usuarios/registro/`
- `GET usuarios/me/`
- `GET denuncias/mis/`
- `POST denuncias/`
- `GET denuncias/notificaciones/`
- `PATCH denuncias/notificaciones/{id}/`

Los contratos detallados están representados por `ApiService.kt` y los modelos en `data/model`.

## Seguridad

- No se deben versionar claves, tokens, archivos `.env` ni almacenes de firma.
- El tráfico HTTP en texto claro está deshabilitado.
- El logging de red solo está activo en debug, usa nivel básico y redacta `Authorization`.
- Las copias de seguridad de datos de la aplicación están deshabilitadas.

Consulta [SECURITY.md](SECURITY.md) y [el informe de saneamiento](docs/security-audit.md) antes de publicar o distribuir el proyecto.

## Limitaciones conocidas

- El token de sesión todavía se almacena en `SharedPreferences` privadas sin cifrado; debe migrarse a almacenamiento respaldado por Android Keystore antes de un uso productivo.
- La API y su disponibilidad se administran fuera de este repositorio.
- Las pruebas existentes cubren únicamente la plantilla base; faltan pruebas funcionales de autenticación, red y formularios.
- La configuración release no habilita aún minificación u ofuscación.

## Posibles mejoras si el proyecto se reactiva

- Migrar el token de sesión a almacenamiento cifrado mediante Android Keystore.
- Ampliar pruebas unitarias e instrumentadas.
- Centralizar el manejo de sesión, errores y ciclo de vida de corrutinas.
- Habilitar endurecimiento del build release y reglas R8 verificadas.

## Autoría y licencia

Proyecto académico de [guacho175](https://github.com/guacho175), preservado con fines de portafolio.

Este repositorio no incluye actualmente una licencia. Mientras no se agregue una, no se conceden permisos de reutilización, modificación o redistribución.
