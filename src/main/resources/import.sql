-- Precarga de catálogo de lotes de fermentos (FermentCraft)
-- Fechas de expiración futuras respecto a la fecha de creación del script.

INSERT INTO product_batches (id, product_name, category, available_quantity, unit_price, expiration_date, active)
VALUES (1, 'Kombucha Original 750ml', 'KOMBUCHA', 120, 45.00, '2026-12-31', true);

INSERT INTO product_batches (id, product_name, category, available_quantity, unit_price, expiration_date, active)
VALUES (2, 'Kéfir de Agua con Frutos Rojos', 'KEFIR_AGUA', 80, 38.50, '2026-11-15', true);

INSERT INTO product_batches (id, product_name, category, available_quantity, unit_price, expiration_date, active)
VALUES (3, 'Kéfir de Leche Natural 500ml', 'KEFIR_LECHE', 60, 42.00, '2026-10-20', true);

INSERT INTO product_batches (id, product_name, category, available_quantity, unit_price, expiration_date, active)
VALUES (4, 'Chucrut Artesanal 400g', 'VEGETALES_FERMENTADOS', 45, 30.00, '2027-01-10', true);

INSERT INTO product_batches (id, product_name, category, available_quantity, unit_price, expiration_date, active)
VALUES (5, 'Kombucha de Jengibre y Limón 750ml', 'KOMBUCHA', 90, 47.50, '2026-12-01', true);

-- Ajuste de la secuencia de identidad para evitar conflictos con inserciones futuras
ALTER SEQUENCE IF EXISTS product_batches_id_seq RESTART WITH 6;