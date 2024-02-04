CREATE TABLE sellers(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX sellers_updated_at_idx ON sellers(updated_at);

CREATE TABLE shops (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    seller_id BIGINT NOT NULL REFERENCES sellers(id),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX shops_seller_id_idx ON shops(seller_id);
CREATE INDEX shops_updated_at_idx ON shops(updated_at);

CREATE TABLE categories(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    parent_id BIGINT REFERENCES categories(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX categories_parent_id_idx ON categories(parent_id);
CREATE INDEX categories_updated_at_idx ON categories(updated_at);

CREATE TABLE attributes(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    composite_attribute_id BIGINT REFERENCES attributes(id),
    name VARCHAR(255) NOT NULL UNIQUE,
    short_name VARCHAR(255) NOT NULL,
    is_faceted BOOLEAN NOT NULL DEFAULT true,
    is_composite BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    numeric_values JSONB,
    string_values JSONB,
    color_values JSONB,
    a_type VARCHAR(8)
);

CREATE INDEX attributes_updated_at_idx ON attributes(updated_at);
CREATE INDEX attributes_numeric_values_idx ON attributes using GIN(numeric_values);
CREATE INDEX attributes_string_values_idx ON attributes using GIN(string_values);
CREATE INDEX attributes_color_values_idx ON attributes using GIN(color_values);

CREATE TABLE category_attributes(
    category_id BIGINT REFERENCES categories(id) NOT NULL,
    attribute_id BIGINT REFERENCES attributes(id) NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT false,
    PRIMARY KEY(category_id, attribute_id)
);

CREATE TABLE products(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    shop_id BIGINT REFERENCES shops(id),
    category_id BIGINT REFERENCES categories(id),
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX products_category_id_idx ON products(category_id);
CREATE INDEX products_shop_id_idx ON products(shop_id);

CREATE TABLE product_variants (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    variant_name TEXT NOT NULL,
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    attributes JSONB,
    images JSONB,
    sku_collection JSONB,
    price DECIMAL NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX product_variants_product_id_idx ON product_variants(product_id);
CREATE INDEX product_variants_attributes_idx ON product_variants using GIN(attributes);
CREATE INDEX product_variants_images_idx ON product_variants using GIN(images);
CREATE INDEX product_variants_skus_idx ON product_variants using GIN(sku_collection);

CREATE TABLE product_outbox (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data JSONB,
    event_timestamp TIMESTAMP NOT NULL
);


CREATE TABLE product_variant_outbox (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data JSONB,
    event_timestamp TIMESTAMP NOT NULL
);

CREATE TABLE product_update_outbox(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_timestamp TIMESTAMP NOT NULL,
    event_data JSONB
);

CREATE TABLE product_sku_update_outbox(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_timestamp TIMESTAMP NOT NULL,
    event_data JSONB
)