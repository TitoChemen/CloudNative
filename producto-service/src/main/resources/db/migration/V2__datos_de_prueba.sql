-- Insertar Categorías
INSERT INTO categoria (id, nombre, slug) VALUES
                                             (1, 'Notebooks', 'notebooks'),
                                             (2, 'Periféricos', 'perifericos'),
                                             (3, 'Componentes', 'componentes');

-- Insertar Productos
INSERT INTO productos (codigo, nombre, especificaciones, precio, precio_referencial, descuento, imagen_url, badge, stock, categoria_id) VALUES
                                                                                                                                            ('NOTE-001', 'Notebook Gamer Lenovo® LOQ', 'Core i7 RTX 4060 16" WUXGA 16GB 1TB SSD', 1459990, 1899990, 23, 'https://i.imgur.com/IwA5RR5.png', 'Oferta', 12, 1),
                                                                                                                                            ('NOTE-002', 'Notebook Gamer Gigabyte® AORUS', 'Ryzen 9 RTX 4070 16" 32GB 1TB SSD', 1819990, 1969000, 8, 'https://i.imgur.com/UkD6wX9.png', 'Destacado', 8, 1),
                                                                                                                                            ('NOTE-003', 'Notebook Gamer MSI Katana 15 B12V', '15.6" FHD, i7 12650H, RTX 4070 8GB, RAM 16GB, SSD 512GB', 1799990, 1880990, 4, 'https://i.imgur.com/uXr4mMp.png', 'Nuevo', 3, 1),
                                                                                                                                            ('NOTE-004', 'Notebook Gamer Lenovo Legion® Pro', 'Ryzen 7 RTX 4060 16GB 512GB SSD 165Hz', 1299990, 1599990, 18, 'https://i.imgur.com/nhvsJAg.png', 'Más Vendido', 15, 1),
                                                                                                                                            ('PERI-001', 'Monitor Gamer Gigabyte® 27" QHD', 'IPS, 165Hz, 1ms, FreeSync Premium, HDR400', 289990, 349990, 17, 'https://i.imgur.com/zXHwJpN.png', 'Oferta', 20, 2),
                                                                                                                                            ('COMP-001', 'Tarjeta de Video RTX 4070 Super 12GB', 'GDDR6X, Triple Fan, PCIe 4.0, DLSS 3.5', 699990, 789990, 11, 'https://i.imgur.com/Ri9A1vd.png', 'Stock Limitado', 2, 3);