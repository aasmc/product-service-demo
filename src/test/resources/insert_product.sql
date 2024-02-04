insert into categories(name) values('Clothes');
insert into sellers(first_name, last_name) values('Seller', 'LastName') ;
insert into shops(seller_id, name, description) values (1, 'Shop', 'Shop Description');
insert into products(shop_id, category_id, name, description) values (1, 1,'T-Shirt', 'T-Shirt description');
insert into product_variants(variant_name, product_id, attributes, images, sku_collection, price)
values ('T-Shirt Blue', 1, null, null, '{
    "attrName": "clothes size",
    "skus": [
      {
        "attrValue": "XS",
        "price": 10,
        "stock": 10,
        "sku": "t-shirt/blue/XS/Brand/230"
      },
      {
        "attrValue": "S",
        "price": 10,
        "stock": 10,
        "sku": "t-shirt/blue/S/Brand/231"
      },
      {
        "attrValue": "M",
        "price": 10,
        "stock": 10,
        "sku": "t-shirt/blue/M/Brand/232"
      }
    ]
  }', 10),
('Red T-Shirt variant', 1, null, null, ' {
    "attrName": "clothes size",
    "skus": [
      {
        "attrValue": "M",
        "price": 10,
        "stock": 10,
        "sku": "t-shirt/red/M/Brand/236"
      },
      {
        "attrValue": "L",
        "price": 10,
        "stock": 10,
        "sku": "t-shirt/red/L/Brand/237"
      },
      {
        "attrValue": "XL",
        "price": 10,
        "stock": 10,
        "sku": "t-shirt/red/XL/Brand/238"
      }
    ]
  }', 10),
('Green T-Shirt variant', 1, null, null, '{
    "attrName": "clothes size",
    "skus": [
      {
        "attrValue": "S",
        "price": 10,
        "stock": 10,
        "sku": "t-shirt/green/S/Brand/233"
      },
      {
        "attrValue": "M",
        "price": 10,
        "stock": 10,
        "sku": "t-shirt/green/M/Brand/234"
      },
      {
        "attrValue": "L",
        "price": 10,
        "stock": 10,
        "sku": "t-shirt/green/L/Brand/235"
      }
    ]
  }', 10);