CREATE TABLE sellers(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX sellers_updated_at_idx ON sellers(updated_at);

CREATE TABLE shops (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    seller_id BIGINT NOT NULL REFERENCES sellers(id),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX shops_seller_id_idx ON shops(seller_id);
CREATE INDEX shops_updated_at_idx ON shops(updated_at);

CREATE TABLE categories(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    parent_id BIGINT REFERENCES categories(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX categories_parent_id_idx ON categories(parent_id);
CREATE INDEX categories_updated_at_idx ON categories(updated_at);

CREATE TABLE attributes(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    short_name VARCHAR(255) NOT NULL,
    is_faceted BOOLEAN NOT NULL DEFAULT true,
    is_composite BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX attributes_category_id_idx ON attributes(category_id);
CREATE INDEX attributes_updated_at_idx ON attributes(updated_at);

CREATE TABLE category_attributes(
    category_id BIGINT REFERENCES categories(id) NOT NULL,
    attribute_id BIGINT REFERENCES attributes(id) NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT false,
    PRIMARY KEY(category_id, attribute_id)
);

CREATE TABLE composite_attribute_value(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    attribute_id BIGINT NOT NULL REFERENCES attributes(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE attribute_values(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    av_type VARCHAR(10) NOT NULL,
    attribute_id BIGINT NOT NULL REFERENCES attributes(id) ON DELETE CASCADE,
    composite_attribute_value_id BIGINT REFERENCES composite_attribute_value(id),
    string_value TEXT UNIQUE,
    string_ru_value TEXT,
    num_value NUMERIC,
    num_ru_value NUMERIC,
    num_unit VARCHAR(50),
    color_value VARCHAR(50),
    color_hex_code VARCHAR(12)
);

CREATE INDEX attribute_values_attribute_id_idx ON attribute_values(attribute_id);
CREATE UNIQUE INDEX attribute_values_num_value_num_ru_value_num_unit_idx ON attribute_values(num_value, num_ru_value, num_unit);


CREATE TABLE products(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    shop_id BIGINT REFERENCES shops(id),
    category_id BIGINT REFERENCES categories(id),
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX products_category_id_idx ON products(category_id);
CREATE INDEX products_shop_id_idx ON products(shop_id);

CREATE TABLE product_variants (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    variant_name TEXT NOT NULL,
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    attributes JSONB,
    images JSONB,
    price DECIMAL NOT NULL,
    stock INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX product_variants_product_id_idx ON product_variants(product_id);
CREATE INDEX product_variants_attributes_idx ON product_variants using GIN(attributes);
CREATE INDEX product_variants_images_idx ON product_variants using GIN(images);

CREATE TABLE product_outbox (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data JSONB,
    event_timestamp TIMESTAMP NOT NULL
);


