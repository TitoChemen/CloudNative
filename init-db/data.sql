USE bd_producto;

-- Insertar Categorías
INSERT INTO categoria (id, nombre, slug) VALUES
                                             (1, 'Notebooks', 'notebooks'),
                                             (2, 'Periféricos', 'perifericos'),
                                             (3, 'Componentes', 'componentes')
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), slug=VALUES(slug);

-- Insertar Productos
INSERT INTO productos (codigo, nombre, especificaciones, precio, precio_referencial, descuento, imagen_url, badge, stock, categoria_id) VALUES
                                                                                                                                            ('NOTE-001', 'Notebook Gamer Lenovo® LOQ', 'Core i7 RTX 4060 16" WUXGA 16GB 1TB SSD', 1459990, 1899990, 23, '/assets/lenovo-loq.png', 'Oferta', 12, 1),
                                                                                                                                            ('NOTE-002', 'Notebook Gamer Gigabyte® AORUS', 'Ryzen 9 RTX 4070 16" 32GB 1TB SSD', 1819990, 1969000, 8, '/assets/notebook-gamer-gigabyte.png', 'Destacado', 8, 1),
                                                                                                                                            ('NOTE-003', 'Notebook Gamer MSI Katana 15 B12V', '15.6" FHD, i7 12650H, RTX 4070 8GB, RAM 16GB, SSD 512GB', 1799990, 1880990, 4, '/assets/msi-katana.png', 'Nuevo', 3, 1),
                                                                                                                                            ('NOTE-004', 'Notebook Gamer Lenovo Legion® Pro', 'Ryzen 7 RTX 4060 16GB 512GB SSD 165Hz', 1299990, 1599990, 18, '/assets/lenovo-loq.png', 'Más Vendido', 15, 1),
                                                                                                                                            ('PERI-001', 'Monitor Gamer Gigabyte® 27" QHD', 'IPS, 165Hz, 1ms, FreeSync Premium, HDR400', 289990, 349990, 17, '/assets/notebook-gamer-gigabyte.png', 'Oferta', 20, 2),
                                                                                                                                            ('COMP-001', 'Tarjeta de Video RTX 4070 Super 12GB', 'GDDR6X, Triple Fan, PCIe 4.0, DLSS 3.5', 699990, 789990, 11, '/assets/msi-katana.png', 'Stock Limitado', 2, 3)
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), precio=VALUES(precio), stock=VALUES(stock);