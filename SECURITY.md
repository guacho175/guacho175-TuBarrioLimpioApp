# Política de seguridad

## Reporte de vulnerabilidades

No publiques claves, tokens, datos personales ni detalles explotables en un issue público. Utiliza el canal privado de reporte de seguridad del repositorio en GitHub si está habilitado.

Incluye una descripción reproducible, alcance, impacto y versión afectada. No incluyas credenciales reales en capturas o registros.

## Gestión de secretos

- Configura `API_BASE_URL` y `MAPS_API_KEY` fuera de Git.
- Restringe las claves de Google Maps por aplicación Android, certificado y API.
- No compartas `.env`, `local.properties`, almacenes de firma ni APK firmados con credenciales reutilizables.
- Trata como comprometido cualquier secreto que haya sido enviado a un commit, aunque después se agregue a `.gitignore`.

## Alcance actual

Este es un proyecto académico en desarrollo. El backend y su infraestructura no están incluidos en este repositorio.
