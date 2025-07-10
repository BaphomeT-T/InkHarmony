-- database: ../database/InkHarmony.sqlite

INSERT INTO Genero (nombre_genero) VALUES
                                       ('GAME_OST'),
                                       ('ROCK'),
                                       ('ROCK_ALTERNATIVO'),
                                       ('VALS'),
                                       ('TANGO'),
                                       ('REGIONAL_MEXICANA'),
                                       ('RAP'),
                                       ('CLASICA'),
                                       ('MOVIE_OST'),
                                       ('JAZZ'),
                                       ('COUNTRY'),
                                       ('POP'),
                                       ('REGGAETON'),
                                       ('TRAP_LATINO'),
                                       ('TRAP'),
                                       ('K_POP'),
                                       ('BOLERO'),
                                       ('PASILLO');
INSERT INTO Usuario (nombre_usuario, apellido_usuario, correo, contraseña, tipo_usuario, id_foto_Perfil, estado_cuenta,tipo_usuario) VALUES
('Jonas', 'Garcia', 'jonas@gmail.com', '1234', 'ADMIN',  '1', 'activo','ADMIN');
SELECT * FROM Usuario