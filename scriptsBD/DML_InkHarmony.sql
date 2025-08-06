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
                                        ('PASILLO'),
                                        ('METAL SINFONICO'),
                                        ('J POP'),
                                        ('POWER METAL')
                                        ;
INSERT INTO Usuario (nombre_usuario, apellido_usuario, correo, contraseña, tipo_usuario, id_foto_Perfil, estado_cuenta) VALUES
('Jonas', 'Garcia', 'jonas@gmail.com', '$2a$10$wFBV8MmGWPzIwsnhJ3/w0eWNici2F8dXx1RUM7F0TxTAa1T2CWfPK', 'ADMINISTRADOR',  '1', 'activo');
SELECT * FROM Usuario;
UPDATE Usuario
                    SET  estado_cuenta= "activo"
                    WHERE correo = "jonas@gmail.com";


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

insert into Genero (nombre_genero) values('METAL_SINFONICO'),
                                        ('J_POP'),
                                        ('POWER_METAL');
DELETE FROM Genero WHERE nombre_genero = 'METAL SINFONICO';
DELETE FROM Genero WHERE nombre_genero = 'J POP';
DELETE FROM Genero WHERE nombre_genero = 'POWER METAL';