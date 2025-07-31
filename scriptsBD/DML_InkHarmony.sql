-- database: ../database/InkHarmony.sqlite

<<<<<<< HEAD
-- INSERT INTO Genero (nombre_genero) VALUES
--                                        ('GAME_OST'),
--                                        ('ROCK'),
--                                        ('ROCK_ALTERNATIVO'),
--                                        ('VALS'),
--                                        ('TANGO'),
--                                        ('REGIONAL_MEXICANA'),
--                                        ('RAP'),
--                                        ('CLASICA'),
--                                        ('MOVIE_OST'),
--                                        ('JAZZ'),
--                                        ('COUNTRY'),
--                                        ('POP'),
--                                        ('REGGAETON'),
--                                        ('TRAP_LATINO'),
--                                        ('TRAP'),
--                                        ('K_POP'),
--                                        ('BOLERO'),
--                                        ('PASILLO');
INSERT INTO Usuario (nombre_usuario, apellido_usuario, correo, contraseña, tipo_usuario, id_foto_Perfil, estado_cuenta) VALUES
('Jonas', 'Garcia', 'jonas@gmail.com', '$2a$10$wFBV8MmGWPzIwsnhJ3/w0eWNici2F8dXx1RUM7F0TxTAa1T2CWfPK', 'ADMINISTRADOR',  '1', 'activo');
SELECT * FROM Usuario;
UPDATE Usuario
                    SET  estado_cuenta= "activo"
                    WHERE correo = "jonas@gmail.com";








=======
>>>>>>> adcb48c002832e4c9e16b0560b19b794d3ea2ec7
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
<<<<<<< HEAD

INSERT INTO Artista (nombre, biografia) VALUES
                                            ('Fleetwood_Mac', 'Banda de rock'),
                                            ('Weezer', 'Banda de rock alternativo'),
                                            ('Maluma', 'Cantante y compositor colombiano'),
                                            ('Karol_G', 'Cantante y compositora colombiana'),
                                            ('Twice', 'Grupo femenino de K-pop'),
                                            ('Julio_Jaramillo', 'Cantante ecuatoriano'),
                                            ('Christian_Nodal', 'Cantante y compositor');

INSERT INTO Artista_Genero(id_artista, id_genero) VALUES
                                                      (1,2),
                                                      (2,3),
                                                      (3,13),
                                                      (4,13),
                                                      (5,16),
                                                      (6,17),
                                                      (7,6);
=======
INSERT INTO Usuario (nombre_usuario, apellido_usuario, correo, contraseña, tipo_usuario, id_foto_Perfil, estado_cuenta) VALUES
    ('Jonas', 'Garcia', 'jonas@gmail.com', '$2a$10$wFBV8MmGWPzIwsnhJ3/w0eWNici2F8dXx1RUM7F0TxTAa1T2CWfPK', 'ADMINISTRADOR',  '1', 'activo');
SELECT * FROM Usuario;
UPDATE Usuario
SET  estado_cuenta= "activo"
WHERE correo = "jonas@gmail.com";





>>>>>>> adcb48c002832e4c9e16b0560b19b794d3ea2ec7

