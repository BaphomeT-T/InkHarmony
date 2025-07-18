-- database: ../database/InkHarmony.sqlite
INSERT INTO Usuario (nombre_usuario, apellido_usuario, correo, contraseña, tipo_usuario, id_foto_Perfil, estado_cuenta) VALUES
    ('Jonas', 'Garcia', 'jonas@gmail.com', '$2a$10$wFBV8MmGWPzIwsnhJ3/w0eWNici2F8dXx1RUM7F0TxTAa1T2CWfPK', 'ADMINISTRADOR',  '1', 'activo');
SELECT * FROM Usuario;
UPDATE Usuario
SET  estado_cuenta= "activo"
WHERE correo = "jonas@gmail.com";






