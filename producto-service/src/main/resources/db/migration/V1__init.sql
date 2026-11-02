CREATE TABLE IF NOT EXISTS categoria (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         nombre VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE
    );

CREATE TABLE IF NOT EXISTS productos (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         codigo VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    especificaciones VARCHAR(500),
    precio DOUBLE,
    precio_referencial DOUBLE,
    descuento INT,
    imagen_url VARCHAR(255),
    badge VARCHAR(100),
    stock INT,
    categoria_id BIGINT,
    CONSTRAINT fk_productos_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
    );