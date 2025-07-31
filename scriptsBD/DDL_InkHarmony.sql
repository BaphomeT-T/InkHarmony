-- database: ../database/InkHarmony.sqlite
-- Borrar tablas en orden seguro
DROP TABLE IF EXISTS Reproduccion;
DROP TABLE IF EXISTS Playlist_Cancion;
DROP TABLE IF EXISTS Cancion_Genero;
DROP TABLE IF EXISTS Cancion_Artista;
DROP TABLE IF EXISTS Artista_Genero;
DROP TABLE IF EXISTS Playlist;
DROP TABLE IF EXISTS Cancion;
DROP TABLE IF EXISTS Artista;
-- DROP TABLE IF EXISTS Genero;
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

-- Tabla Genero
-- CREATE TABLE Genero (
--                         id_genero INTEGER PRIMARY KEY AUTOINCREMENT,
--                         nombre_genero VARCHAR(20) NOT NULL
-- );

-- Tabla Artista
CREATE TABLE Artista (
                         id_artista INTEGER PRIMARY KEY AUTOINCREMENT,
                         nombre VARCHAR(20) NOT NULL,
                         nacionalidad VARCHAR(20),
                         biografia VARCHAR(20),
                         fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
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
                         fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
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

-- Tabla Playlist
CREATE TABLE Playlist (
                          id_playlist INTEGER PRIMARY KEY AUTOINCREMENT,
                          id_usuario INTEGER,
                          nombre VARCHAR(20) NOT NULL,
                          fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          descripcion VARCHAR(20),
                          FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);

-- Tabla Playlist_Cancion (N:M)
CREATE TABLE Playlist_Cancion (
                                  id_playlist INTEGER NOT NULL,
                                  id_cancion INTEGER NOT NULL,
                                  PRIMARY KEY (id_playlist, id_cancion),
                                  FOREIGN KEY (id_playlist) REFERENCES Playlist(id_playlist),
                                  FOREIGN KEY (id_cancion) REFERENCES Cancion(id_cancion)
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