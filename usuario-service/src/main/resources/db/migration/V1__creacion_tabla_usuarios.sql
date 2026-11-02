CREATE TABLE usuarios (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          apellido VARCHAR(100) NOT NULL,
                          rut VARCHAR(15) UNIQUE NOT NULL,
                          email VARCHAR(150) UNIQUE NOT NULL,
                          direccion VARCHAR(255) NOT NULL,
                          password VARCHAR(255) NOT NULL
);