CREATE TABLE sellers(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL
);

CREATE TABLE shops (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    seller_id BIGINT NOT NULL REFERENCES sellers(id),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL
);

CREATE INDEX shops_seller_id_idx ON shops(seller_id);

CREATE TABLE categories(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    parent_id BIGINT REFERENCES categories(id)
);

CREATE INDEX categories_parent_id_idx ON categories(parent_id);

CREATE TABLE attributes(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE category_attributes(
    category_id BIGINT REFERENCES categories(id) ON DELETE CASCADE ,
    attribute_id BIGINT REFERENCES attributes(id),
    is_required BOOLEAN NOT NULL,
    PRIMARY KEY (category_id, attribute_id)
);

CREATE TABLE attribute_values(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    attribute_id BIGINT REFERENCES attributes(id) ON DELETE CASCADE,
    value VARCHAR(255) NOT NULL,
    is_composite BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX attribute_values_attribute_id_idx ON attribute_values(attribute_id);

CREATE TABLE attribute_components(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    attribute_value_id BIGINT REFERENCES attribute_values(id) ON DELETE CASCADE,
    component_name VARCHAR(255) NOT NULL,
    component_value VARCHAR(255) NOT NULL
);

CREATE INDEX attribute_components_attribute_value_id_idx ON attribute_components(attribute_value_id);

CREATE TABLE products(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    shop_id BIGINT REFERENCES shops(id),
    category_id BIGINT REFERENCES categories(id),
    name TEXT NOT NULL,
    description TEXT NOT NULL
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
    stock INT DEFAULT 0
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


