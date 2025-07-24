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

