
CREATE DATABASE IF NOT EXISTS despensa_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE despensa_db;

DROP TABLE IF EXISTS despensa;
DROP TABLE IF EXISTS recetas;
DROP TABLE IF EXISTS ingredientes;

CREATE TABLE ingredientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    categoria ENUM('PERECEDERO', 'NO_PERECEDERO') NOT NULL,
    unidad_medida VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE recetas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    instrucciones TEXT NOT NULL,
    tiempo_preparacion INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE despensa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_ingrediente INT NOT NULL,
    cantidad DOUBLE NOT NULL,
    fecha_caducidad DATE NULL,
    CONSTRAINT fk_despensa_ingrediente FOREIGN KEY (id_ingrediente)
        REFERENCES ingredientes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO ingredientes (id, nombre, categoria, unidad_medida) VALUES
(1, 'Leche entera', 'PERECEDERO', 'ml'),
(2, 'Huevos', 'PERECEDERO', 'piezas'),
(3, 'Harina de trigo', 'NO_PERECEDERO', 'g'),
(4, 'Azúcar', 'NO_PERECEDERO', 'g'),
(5, 'Tomates', 'PERECEDERO', 'piezas'),
(6, 'Queso mozzarella', 'PERECEDERO', 'g'),
(7, 'Arroz blanco', 'NO_PERECEDERO', 'g'),
(8, 'Pechuga de pollo', 'PERECEDERO', 'g'),
(9, 'Lentejas', 'NO_PERECEDERO', 'g'),
(10, 'Pasta espagueti', 'NO_PERECEDERO', 'g'),
(11, 'Cebolla blanca', 'PERECEDERO', 'piezas'),
(12, 'Zanahorias', 'PERECEDERO', 'piezas'),
(13, 'Aceite de oliva', 'NO_PERECEDERO', 'ml'),
(14, 'Ajo', 'PERECEDERO', 'piezas'),
(15, 'Avena en hojuelas', 'NO_PERECEDERO', 'g'),
(16, 'Plátano', 'PERECEDERO', 'piezas'),
(17, 'Espinacas frescas', 'PERECEDERO', 'g'),
(18, 'Carne molida de res', 'PERECEDERO', 'g');

INSERT INTO recetas (id, nombre, descripcion, instrucciones, tiempo_preparacion) VALUES
(1, 'Hot Cakes Caseros',
    'Esponjosos y deliciosos panqueques tradicionales para el desayuno familiar.',
    '1. En un tazón mezclar la harina de trigo y el azúcar.\n2. Agregar la leche entera y los huevos.\n3. Batir con batidor de globo hasta eliminar grumos.\n4. Calentar una sartén a fuego medio con un poco de mantequilla.\n5. Verter porciones de mezcla y voltear cuando salgan burbujas (2 min por lado).\n6. Servir calientes con miel o fruta.',
    15),
(2, 'Tortilla de Tomate y Queso',
    'Omelette jugoso y nutritivo relleno de trozos de tomate fresco y queso fundido.',
    '1. Batir los huevos en un plato hondo con sal y pimienta al gusto.\n2. Picar los tomates frescos en dados pequeños.\n3. Calentar una sartén con unas gotas de aceite.\n4. Verter los huevos batidos y cocinar a fuego bajo.\n5. Distribuir los tomates y el queso mozzarella sobre una mitad.\n6. Doblar la tortilla, dejar fundir el queso por 1 minuto y servir de inmediato.',
    10),
(3, 'Pollo con Arroz y Verduras',
    'Plato fuerte completo, nutritivo y balanceado de pechuga salteada con arroz.',
    '1. Cortar la pechuga de pollo en tiras o cubos medianos.\n2. Salpimentar el pollo y dorar en sartén con un toque de aceite durante 8 minutos.\n3. Añadir los tomates picados y saltear hasta que suelten sus jugos.\n4. Incorporar el arroz blanco cocido y mezclar a fuego medio por 5 minutos.\n5. Rectificar sazón y servir con cilantro fresco opcional.',
    35),
(4, 'Arroz con Leche Dulce',
    'Postre cremoso y reconfortante hecho con arroz suave, leche aromatizada y azúcar.',
    '1. Enjuagar el arroz y ponerlo a hervir a fuego medio.\n2. Cuando esté casi cocido, agregar la leche entera lentamente.\n3. Incorporar el azúcar y cocinar a fuego lento moviendo con cuchara de madera.\n4. Dejar espesar durante 15 a 20 minutos sin dejar de remover.\n5. Retirar del fuego, dejar templar y refrigerar antes de servir.',
    25),
(5, 'Sopa Casera de Lentejas',
    'Reconfortante sopa de lentejas cocinadas a fuego lento con zanahoria, cebolla y ajo fresco.',
    '1. Enjuagar las lentejas en agua fría.\n2. Picar finamente la cebolla blanca, el ajo y las zanahorias.\n3. Saltear las verduras en una olla con aceite de oliva por 4 minutos.\n4. Incorporar las lentejas y cubrir con suficiente agua.\n5. Cocinar a fuego medio durante 25 minutos hasta que estén tiernas.\n6. Servir caliente decorada con hierbas al gusto.',
    30),
(6, 'Pasta con Verduras Salteadas',
    'Espagueti al dente salteado con tomates frescos, ajo dorado y espinacas tiernas.',
    '1. Hervir agua con sal y cocinar la pasta espagueti de 8 a 10 minutos.\n2. En una sartén calentar aceite de oliva y dorar el ajo finamente picado.\n3. Añadir los tomates en cubos pequeños y saltear 3 minutos.\n4. Agregar las hojas de espinaca fresca hasta que reduzcan.\n5. Integrar la pasta escurrida a la sartén y revolver 1 minuto.\n6. Servir caliente.',
    20),
(7, 'Omelette de Espinaca y Queso',
    'Omelette esponjoso relleno de hojas tiernas de espinaca salteada y queso mozzarella fundido.',
    '1. Saltear brevemente las espinacas frescas con un poco de aceite de oliva.\n2. Batir los huevos en un plato hondo con sal y pimienta.\n3. Verter los huevos batidos en una sartén antiadherente a fuego bajo.\n4. Colocar las espinacas y el queso mozzarella sobre una mitad de la tortilla.\n5. Doblar la tortilla sobre el relleno y dejar fundir por 1 minuto.\n6. Servir caliente inmediatamente.',
    12),
(8, 'Batido de Avena y Plátano',
    'Bebida energética y cremosa preparada con hojuelas de avena, plátano maduro y leche.',
    '1. Colocar las hojuelas de avena en el vaso de la licuadora.\n2. Pelar y cortar el plátano en trozos.\n3. Verter la leche entera y añadir el azúcar.\n4. Licuar a velocidad alta durante 1 minuto hasta lograr una textura suave y homogénea.\n5. Servir de inmediato bien frío.',
    5),
(9, 'Pasta Boloñesa Casera',
    'Tradicional pasta con salsa de carne molida sazonada, tomate, cebolla blanca y ajo.',
    '1. Cocinar la pasta espagueti en agua hirviendo con sal hasta que esté al dente.\n2. En una sartén calentar el aceite de oliva y sofreír la cebolla blanca y el ajo.\n3. Agregar la carne molida de res y dorar completamente.\n4. Añadir los tomates picados y dejar reducir a fuego lento por 15 minutos.\n5. Servir la salsa boloñesa sobre la pasta y espolvorear queso mozzarella.',
    30);

INSERT INTO despensa (id_ingrediente, cantidad, fecha_caducidad) VALUES

(1, 1000.0, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(1, 1500.0, DATE_ADD(CURDATE(), INTERVAL 10 DAY)),

(2, 12.0, DATE_ADD(CURDATE(), INTERVAL 14 DAY)),

(3, 1000.0, NULL),

(4, 750.0, NULL),

(5, 5.0, DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(5, 8.0, DATE_ADD(CURDATE(), INTERVAL 12 DAY)),

(6, 400.0, DATE_ADD(CURDATE(), INTERVAL 9 DAY)),

(7, 1500.0, NULL),

(8, 800.0, DATE_ADD(CURDATE(), INTERVAL 8 DAY)),

(9, 1000.0, NULL),

(10, 800.0, NULL),

(11, 6.0, DATE_ADD(CURDATE(), INTERVAL 10 DAY)),

(12, 8.0, DATE_ADD(CURDATE(), INTERVAL 12 DAY)),

(13, 750.0, NULL),

(14, 6.0, DATE_ADD(CURDATE(), INTERVAL 20 DAY)),

(15, 600.0, NULL),

(16, 5.0, DATE_ADD(CURDATE(), INTERVAL 5 DAY)),

(17, 250.0, DATE_ADD(CURDATE(), INTERVAL 6 DAY)),

(18, 600.0, DATE_ADD(CURDATE(), INTERVAL 7 DAY));
