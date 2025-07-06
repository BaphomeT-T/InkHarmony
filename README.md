## Estructura del proyecto y descripción de carpetas

- **.vscode, bin, lib:** Carpetas para configuración del IDE, archivos compilados o bibliotecas externas.

- **database:** Contiene la base de datos.

- **design:** Incluye diagramas de clases, casos de uso o cualquier documento de diseño del software.

- **scriptsBD:** Guarda los scripts SQL de la base de datos. Usamos archivos **DDL** para la creación de las tablas y estructuras (definición del esquema) y **DML** para la inserción de datos iniciales o de prueba.

- **src:** Contiene todo el código fuente Java, dividido en capas siguiendo el diseño en módulos:
  - **BusinessLogic:** Incluye la lógica de negocio y los servicios. Aquí se implementan las reglas y operaciones principales de la aplicación (por ejemplo, validar acciones del usuario, orquestar DAOs).
  - **DataAccessComponent:** Maneja la interacción con la base de datos. Incluye:
    - **DAO:** Interfaces y clases que implementan el acceso a datos, consultas y actualizaciones en la base.
    - **DTO:** Clases que representan las entidades de datos para transferir información entre capas.
  - **UserInterface:** Gestiona la interacción con el usuario. Se divide en:
    - **CustomerControl:** Controladores que coordinan entre la lógica de negocio y la interfaz gráfica.
    - **GUI:** Vistas o pantallas gráficas.
    - **Resources:** Archivos estáticos como imágenes, iconos o configuraciones que usa la interfaz.

- **App.java:** Clase principal que contiene el método `main`, inicia la aplicación y ensambla sus componentes.

- **README.md:** Documento de ayuda y guía del proyecto.
