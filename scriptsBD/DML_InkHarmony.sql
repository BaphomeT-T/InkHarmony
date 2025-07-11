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
INSERT INTO Usuario (
    nombre_usuario, apellido_usuario, correo, contraseña,
    id_foto_Perfil, estado_cuenta, tipo_usuario
) VALUES (
    'Jonas', 'Garcia', 'jonas@gmail.com', '$2a$10$Eeq00NcIkFIZGTbgL8p/wetV5sO84ogE8htlZJaC.ztFXV3ib1eAu4',
    '1', 'activo', 'ADMINISTRADOR'
);

SELECT * FROM Usuario