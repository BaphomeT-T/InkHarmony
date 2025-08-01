-- database: ../database/InkHarmony.sqlite
-- Borrar tablas en orden seguro
DROP TABLE IF EXISTS Reproduccion;
DROP TABLE IF EXISTS playlists;
DROP TABLE IF EXISTS playlist_elementos;
DROP TABLE IF EXISTS Cancion_Genero;
DROP TABLE IF EXISTS Cancion_Artista;
DROP TABLE IF EXISTS Artista_Genero;
DROP TABLE IF EXISTS Playlist;
DROP TABLE IF EXISTS Cancion;
DROP TABLE IF EXISTS Artista;
 DROP TABLE IF EXISTS Genero;
DROP TABLE IF EXISTS Usuario;


-- Tabla Usuario
CREATE TABLE Usuario (
                         id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                         nombre_usuario VARCHAR(20) NOT NULL,
                         apellido_usuario VARCHAR(20),
                         correo VARCHAR(20) UNIQUE NOT NULL,
                         contraseña VARCHAR(255) NOT NULL,
                         fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         id_foto_Perfil VARCHAR(20) NOT NULL,
                         estado_cuenta VARCHAR(20) NOT NULL,
                         preferencias_musicales JSON,
                         tipo_usuario VARCHAR(20) NOT NULL
);

--Tabla Genero
CREATE TABLE Genero (
                        id_genero INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre_genero VARCHAR(20) NOT NULL
);

-- Tabla Artista
    CREATE TABLE Artista (
                             id_artista INTEGER PRIMARY KEY AUTOINCREMENT,
                             nombre VARCHAR(50) NOT NULL UNIQUE,
                             biografia TEXT NOT NULL,
                             imagen BLOB -- o BLOB si quieres guardar bytes
    );


    -- Tabla Artista_Genero (N:M)
    CREATE TABLE Artista_Genero (
                                    id_artista INTEGER NOT NULL,
                                    id_genero INTEGER NOT NULL,
                                    PRIMARY KEY (id_artista, id_genero),
                                    FOREIGN KEY (id_artista) REFERENCES Artista(id_artista),
                                    FOREIGN KEY (id_genero) REFERENCES Genero(id_genero)
    );

-- Tabla Cancion
CREATE TABLE Cancion (
                         id_cancion INTEGER PRIMARY KEY AUTOINCREMENT,
                         anio INTEGER,
                         titulo VARCHAR(20) NOT NULL,
                         archivo_mp3 BLOB,
                         duracion REAL,
                         fecha_registro DATETIME NOT NULL ,
                         portada BLOB
);

-- Tabla Cancion_Genero (N:M)
CREATE TABLE Cancion_Genero (
                                id_cancion INTEGER NOT NULL,
                                id_genero INTEGER NOT NULL,
                                PRIMARY KEY (id_cancion, id_genero),
                                FOREIGN KEY (id_cancion) REFERENCES Cancion(id_cancion),
                                FOREIGN KEY (id_genero) REFERENCES Genero(id_genero)
);

-- Tabla Cancion_Artista (N:M)
CREATE TABLE Cancion_Artista (
                                 id_cancion INTEGER NOT NULL,
                                 id_artista INTEGER NOT NULL,
                                 PRIMARY KEY (id_cancion, id_artista),
                                 FOREIGN KEY (id_cancion) REFERENCES Cancion(id_cancion),
                                 FOREIGN KEY (id_artista) REFERENCES Artista(id_artista)
);

CREATE TABLE IF NOT EXISTS playlists (
                                         id_playlist INTEGER PRIMARY KEY AUTOINCREMENT,
                                         titulo_playlist TEXT NOT NULL,
                                         descripcion TEXT,
                                         fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                                         fecha_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                                         propietario_correo TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS playlist_elementos (
                                                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                  id_playlist INTEGER NOT NULL,
                                                  id_cancion INTEGER NOT NULL,
                                                  orden_elemento INTEGER DEFAULT 0,
                                                  fecha_agregado DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                  FOREIGN KEY (id_playlist) REFERENCES playlists(id_playlist) ON DELETE CASCADE
);


-- Tabla Reproduccion
CREATE TABLE Reproduccion (
                              id_reproduccion INTEGER PRIMARY KEY AUTOINCREMENT,
                              id_usuario INTEGER,
                              id_cancion INTEGER,
                              fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario),
                              FOREIGN KEY (id_cancion) REFERENCES Cancion(id_cancion)
);
