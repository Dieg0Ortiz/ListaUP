# ListaUP

App de la Universidad Politécnica de Chiapas para pasar lista y gestionar permisos de inasistencia. Hay un solo login: los alumnos entran con su matrícula y los docentes con su usuario. Cada uno ve su propia app.

Hecha con **Kotlin Multiplatform + Compose Multiplatform**, así que el mismo código genera:

| Plataforma | Estado |
|---|---|
| Android | Compila y está probada en emulador contra la API real (MySQL en Docker) |
| Web (Wasm y JS) | Compila; no se ha probado a fondo |
| iOS | Sin compilar ni probar (requiere Mac con Xcode); ver [pendientes](#pendientes) |

---

## Qué hace

### Alumno
- **Inicio:** saludo, próxima clase y últimos permisos.
- **Materias:** lista con buscador, y detalle con docente, aula, grupo y horario.
- **Horario semanal** por día.
- **Permisos:** solicitar por días completos o por horas, con motivo y comprobante PDF opcional (máx. 1 MB). Se puede seguir cada solicitud en una línea de tiempo: tutor → Dirección → resultado.
- **Notificaciones** de cambios en sus permisos.
- **Perfil** y cierre de sesión.

### Docente
- **Inicio:** asistencia promedio de la semana con barras por día y comparación con la semana pasada, más sus próximas clases.
- **Pase de lista:** presente, retardo o falta por alumno, con buscador. Confirma antes de salir si hay marcas sin enviar y evita el doble envío. Los alumnos sin marcar se registran como ausentes, avisando antes.
- **Alertas:** alumnos que alcanzan el límite de faltas, y solicitudes de sus tutorados pendientes de revisar.
- **Permisos de tutorados:** aprobar (pasa a Dirección) o rechazar con motivo.
- **Reportes:** asistencia acumulada por grupo, promedio general ponderado por alumno y alumnos en riesgo.
- **Ajustes de asistencia:** límite de faltas para alertas (1 a 10). Se guarda en el teléfono.

### Cómo se decide si es alumno o docente
La API de alumnos acepta cualquier usuario, así que la app deduce el rol por el formato ([LoginUseCase.kt](composeApp/src/commonMain/kotlin/com/example/myapplication/domain/usecase/LoginUseCase.kt)):
- Si son solo dígitos, va al login de alumno.
- Si tiene letras, va al login de docente.
- Si el primero rechaza las credenciales, se intenta el otro.
- Si escriben el correo completo, se quita el `@dominio`.

---

## Pantallas

| Ruta (`Screen`) | Archivo | Quién |
|---|---|---|
| `Login` | `ui/login/LoginScreen.kt` | Todos |
| `StudentDashboard` | `ui/dashboard/student/StudentDashboardScreen.kt` | Alumno |
| `Subjects` / `SubjectDetail` | `ui/subjects/` | Alumno |
| `Schedule` | `ui/schedule/ScheduleScreen.kt` | Alumno |
| `ProfessorDashboard` | `ui/dashboard/professor/ProfessorDashboardScreen.kt` | Docente |
| `ProfessorClasses` | `ui/dashboard/professor/ProfessorClassesScreen.kt` | Docente |
| `Attendance` | `ui/attendance/AttendanceScreen.kt` | Docente |
| `Alerts` | `ui/alerts/AlertsScreen.kt` | Docente |
| `Reports` | `ui/reports/ReportsScreen.kt` | Docente |
| `AttendanceSettings` | `ui/settings/AttendanceSettingsScreen.kt` | Docente |
| `Permissions` / `PermissionRequest` / `PermissionDetail` | `ui/permissions/` | Ambos |
| `Notifications` | `ui/notifications/NotificationsScreen.kt` | Ambos |
| `Profile` | `ui/profile/ProfileScreen.kt` | Ambos |

Todas las pantallas que cargan datos tienen estado de carga, de vacío y de error con reintento, y se actualizan deslizando hacia abajo. Los componentes compartidos están en `ui/design/`: botones, tarjetas, íconos y `StateViews.kt`.

**Regla de diseño:** nada de textos de relleno. No van subtítulos bajo los títulos, explicaciones del tipo "aparecerá aquí cuando…", eslóganes ni logos inventados. Solo títulos, datos, acciones, errores y restricciones reales. El único logo es el oficial de la UP Chiapas (`composeResources/drawable/logo_upchiapas.png`), y no se le cambia el color.

---

## Arquitectura

Arquitectura en capas con inyección de dependencias (Koin):

```
composeApp/src/commonMain/kotlin/com/example/myapplication/
├── core/            Configuración (AppConfig), fechas, mensajes de error, almacenamiento clave-valor, navegación
├── data/
│   ├── local/       SessionManager: cookie, usuario y ajustes persistidos
│   ├── remote/      ApiClient (Ktor), ApiService (todas las llamadas) y DTOs
│   └── repository/  Implementaciones: Auth, Attendance, Permission, Notification, Stats, Alert
├── domain/          Modelos, interfaces de repositorio y casos de uso (login, alertas)
├── di/              Módulos de Koin
└── presentation/    Tema, pantallas (ui/) y ViewModels (StateFlow)
```

Código por plataforma (`expect`/`actual`):

| Qué | Android | iOS | Web |
|---|---|---|---|
| URL de la API (`apiBaseUrl`) | BuildConfig por tipo de build | fija, `localhost` ⚠️ | |
| Modo debug (`isDebugBuild`) | ✔ | ✔ | siempre `false` |
| Almacenamiento | SharedPreferences | NSUserDefaults | en memoria |
| Selector de PDF | ✔ | no implementado | no implementado |
| Botón atrás (`PlatformBackHandler`) | ✔ | sin equivalente | sin equivalente |

**Librerías:** Compose Multiplatform 1.10, Material 3, Ktor 3.1, kotlinx-serialization, kotlinx-datetime 0.7, Koin y Navigation Compose con rutas tipadas.

---

## Backend

La app consume la API **Asiste** (FastAPI + MySQL), que mantiene otro equipo en su propio repositorio.

| Endpoint | Uso |
|---|---|
| `POST /siu/servicio/logingappalumnos` | Login de alumno |
| `POST /siu/servicio/logingapp` | Login de docente |
| `POST /siu/servicio/lista_grupos_v_alumno` | Materias del alumno |
| `POST /siu/servicio/lista_horario_alumno` | Horario del alumno |
| `POST /siu/servicio/lista_grupo_docente` | Grupos del docente |
| `POST /siu/servicio/lista_alumnos_grupo` | Alumnos de un grupo |
| `POST /api/v1/asistencia/registrar` | Enviar pase de lista |
| `GET /api/v1/asistencia/resumen` | Resumen por sesión (inicio y reportes) |
| `GET /api/v1/asistencia/alertas` | Alumnos en riesgo |
| `GET/POST /api/v1/permisos…` | Listar, ver, solicitar (multipart con PDF), revisión del tutor y comprobante |
| `GET /api/v1/docente/{id}/tutorados` | Tutorados del docente |
| `GET/POST /api/v1/notificaciones/{alumno\|docente}/{id}…` | Notificaciones y marcar como leídas |

### Configuración

| Qué | Dónde | Valor actual |
|---|---|---|
| URL debug (Android) | `composeApp/build.gradle.kts` | `http://10.0.2.2:8000`; se cambia con `-PlistaupDebugApiUrl=…` |
| URL release (Android) | `composeApp/build.gradle.kts` | `https://platinum.upchiapas.edu.mx`; se cambia con `-PlistaupApiUrl=…` |
| Periodo | `core/AppConfig.kt` → `PERIODO_ID` | `20261` (fijo) ⚠️ |
| Correo de soporte | `core/AppConfig.kt` → `SUPPORT_EMAIL` | `soporte@upchiapas.edu.mx` (por confirmar) |
| Límite de faltas por defecto | `core/AppConfig.kt` | 3 |

---

## Cómo correrla

Requisitos: JDK 17, Android SDK y, para el backend local, Docker.

```shell
# Android (debug)
./gradlew :composeApp:assembleDebug

# Pruebas unitarias
./gradlew :composeApp:testDebugUnitTest

# Web
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

**En Windows con OneDrive:** compilar dentro de una carpeta sincronizada con OneDrive falla con `Unable to delete directory`. Hay que copiar el proyecto fuera de OneDrive para compilar. El proyecto usa AGP 8.11, así que hace falta Gradle 8.x; Gradle 9 no funciona.

### Backend local con datos reales

En la carpeta `api/` del repositorio de Asiste:

```shell
docker compose up -d --build
docker exec asiste_python_api python scripts/manage_db.py --seed
```

El emulador de Android llega a la PC por `10.0.2.2:8000`, que es la URL de debug por defecto.

**Usuarios de prueba (seed):** alumnos `221045` a `221050`, docentes `profesor01` a `profesor03`. Por ahora la API acepta cualquier contraseña.

**Acceso demo sin backend** (solo en builds debug):
- `demo` / `demo` entra como alumno.
- `demodocente` / `demo` entra como docente.

---

## Pruebas

Las pruebas están en `composeApp/src/commonTest`; hoy son 16 y todas pasan:
- `ScheduleParsingTest`: lectura de horarios (`"Lunes 08:00-10:00, …"`), días con y sin acento, formatos AM/PM.
- `LoginUseCaseTest`: detección de alumno o docente, uso del correo completo, segundo intento y errores de red.

Además se probaron a mano en emulador, contra MySQL en Docker:
- Login de alumno y de docente.
- Solicitud de permiso por horas.
- Rechazo del tutor con motivo y notificación al alumno.
- Pase de lista.
- Estadísticas semanales y reportes.
- Persistencia de la sesión.
- Error sin conexión.

---

## Pendientes

### Bloqueantes para producción

**API Asiste** (los corrige su equipo):
- [ ] **Autenticación.** El login acepta cualquier contraseña y, si el usuario no existe, devuelve uno de prueba. Las rutas `/api/v1/*` no piden sesión: cualquiera con la URL puede registrar asistencias o aprobar permisos.
- [ ] **Esquema de base de datos.** `init.sql` define `student_permissions`, `permission_target_students`, `student_notifications` y `teacher_notifications` distinto que los modelos de SQLAlchemy. Con la base real, los permisos y las notificaciones dan error 500.
- [ ] **Dependencias.** A `requirements.txt` le faltan `sqlalchemy[asyncio]` (greenlet) y `jinja2`; sin ellas el contenedor no arranca.
- [ ] **Alertas fijas en el código.** Las alertas devuelven datos de ejemplo en lugar de calcularlos.
- [ ] **Endpoint de asistencia del alumno.** No existe una ruta para que el alumno consulte su propia asistencia.
- [ ] **Notificaciones en inglés.** Llegan con el estado en inglés ("REJECTED"); la app lo traduce mientras tanto.
- [ ] **Endpoints `/api/v1` en producción.** Hay que confirmar que `https://platinum.upchiapas.edu.mx` los expone.

**App:**
- [ ] **Identificador.** Cambiar `com.example.myapplication` en Android y en iOS (`iosApp/Configuration/Config.xcconfig`) por uno real, por ejemplo `mx.edu.upchiapas.listaup`. Google Play no acepta `com.example`.
- [ ] **Ícono de la app.** Sigue siendo el de Android por defecto.
- [ ] **Firma de release.** Falta el keystore de firma.
- [ ] **Periodo automático.** `PERIODO_ID` está fijo; debe venir de la API o calcularse con la fecha.
- [ ] **Correo de soporte.** Confirmar el correo real.
- [ ] **Aviso de privacidad.** Lo exigen la LFPDPPP y Google Play.

### Importantes
- [ ] **Pase de lista sin conexión.** Guardar el borrador en el teléfono y reenviarlo al recuperar la red.
- [ ] **Sesión vencida.** Cuando el servidor responda 401, cerrar sesión y llevar al login.
- [ ] **Notificaciones push** con FCM; hoy solo se ven al abrir la app.
- [ ] **Reporte de fallos** con Crashlytics o Sentry.
- [ ] **Pantalla para Dirección** (o confirmar que la cubre el panel web de Asiste).

### iOS
- [ ] **URL de la API.** `apiBaseUrl` está fija en `http://localhost:8000` (`Platform.ios.kt`); separar debug y release como en Android.
- [ ] **Selector de PDF.** Implementarlo; hoy la opción de adjuntar está oculta.
- [ ] **Regresar en cada pantalla.** iOS no tiene botón atrás: revisar que cada pantalla tenga su flecha y que la confirmación de salir del pase de lista funcione.
- [ ] **Prueba completa.** Compilar y probar en una Mac, o configurar CI con macOS.

### Mejoras
- [ ] Exportar reportes a PDF o Excel.
- [ ] Accesibilidad: letra grande, modo oscuro y revisión con lector de pantalla.
- [ ] Más pruebas (ViewModels y repositorios) y CI que las corra en cada cambio.
