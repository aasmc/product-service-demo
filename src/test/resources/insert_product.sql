insert into categories(name) values('Clothes');
insert into sellers(first_name, last_name) values('Seller', 'LastName') ;
insert into shops(seller_id, name, description) values (1, 'Shop', 'Shop Description');
insert into products(shop_id, category_id, name, description) values (1, 1,'T-Shirt', 'T-Shirt description');
insert into product_variants(variant_name, product_id, attribute_collection, image_collection, sku_collection, price)
values ('T-Shirt Blue', 1,
        '{"attributes":[
                           {"id":"colorAttrId","attributeName":"color","shortName":"color","isFaceted":true,"type":"color","isRequired":true,"availableValues":[{"colorValue":"red","colorHex":"FF0000","type":"color_type"}]},
                           {"id":"stringAttrId","attributeName":"clothes size","shortName":"size","isFaceted":true,"type":"string","isRequired":false,"availableValues":[{"stringValue":"XS","stringRuValue":"44","type":"string_type"},{"stringValue":"S","stringRuValue":"46","type":"string_type"},{"stringValue":"M","stringRuValue":"48","type":"string_type"},{"stringValue":"L","stringRuValue":"50","type":"string_type"},{"stringValue":"XL","stringRuValue":"52","type":"string_type"},{"stringValue":"XXL","stringRuValue":"54","type":"string_type"}]},
                           {"id":"weightId","attributeName":"weight","shortName":"weight","isFaceted":true,"type":"numeric","isRequired":true,"availableValues":[{"numValue":100.0,"numRuValue":100.0,"numUnit":"gram","type":"numeric_type"},{"numValue":200.0,"numRuValue":200.0,"numUnit":"gram","type":"numeric_type"}]},
                           {"id":"dimensId","attributeName":"clothes dimensions","shortName":"dimensions","isFaceted":true,"type":"composite","isRequired":false,"subAttributes":[{"attributeName":"width","shortName":"width","isFaceted":true,"type":"numeric","availableValues":[{"numValue":10.0,"numRuValue":null,"numUnit":"mm","type":"numeric_type"},{"numValue":20.0,"numRuValue":null,"numUnit":"mm","type":"numeric_type"},{"numValue":30.0,"numRuValue":null,"numUnit":"mm","type":"numeric_type"}]},{"attributeName":"length","shortName":"length","isFaceted":true,"type":"numeric","availableValues":[{"numValue":10.0,"numRuValue":null,"numUnit":"mm","type":"numeric_type"},{"numValue":20.0,"numRuValue":null,"numUnit":"mm","type":"numeric_type"},{"numValue":30.0,"numRuValue":null,"numUnit":"mm","type":"numeric_type"}]},{"attributeName":"depth","shortName":"depth","isFaceted":true,"type":"numeric","availableValues":[{"numValue":10.0,"numRuValue":null,"numUnit":"mm","type":"numeric_type"},{"numValue":20.0,"numRuValue":null,"numUnit":"mm","type":"numeric_type"},{"numValue":30.0,"numRuValue":null,"numUnit":"mm","type":"numeric_type"}]}],"availableValues":[]},
                           {"id":"stringCompositeAttrId","attributeName":"String Composite attr name","shortName":"String Composite attr name","isFaceted":true,"type":"composite","isRequired":true,"subAttributes":[{"id":"stringCompositeAttrId","attributeName":"A string attribute name","shortName":"A string attribute name","isFaceted":true,"type":"string","isRequired":true,"availableValues":[{"stringValue":"XS","stringRuValue":"44","type":"string_type"},{"stringValue":"S","stringRuValue":"46","type":"string_type"}]},{"id":"Second String subattribute id","attributeName":"Second String subattribute name","shortName":"Second String subattribute name","isFaceted":true,"type":"string","isRequired":true,"availableValues":[{"stringValue":"L","stringRuValue":"50","type":"string_type"},{"stringValue":"XL","stringRuValue":"50","type":"string_type"}]}],"availableValues":[]},
                           {"id":"colorCompositeAttrId","attributeName":"Color Composite name","shortName":"Color Composite name","isFaceted":true,"type":"composite","isRequired":true,"subAttributes":[{"id":"colorCompositeSubAttrId","attributeName":"Color Shade","shortName":"Color Shade","isFaceted":true,"type":"color","isRequired":true,"availableValues":[{"colorValue":"blue","colorHex":"0000FF","type":"color_type"},{"colorValue":"red","colorHex":"FF0000","type":"color_type"}]},{"id":"secondSubAttrId","attributeName":"Shade2 name","shortName":"Shade2 name","isFaceted":true,"type":"color","isRequired":true,"availableValues":[{"colorValue":"green","colorHex":"00FF00","type":"color_type"}]}],"availableValues":[]}
                        ]
        }',
        '{"images":
            [
                {"url":"http://images.com/old_blue_image.png","isPrimary":true}
            ]
         }',
        '{"attrName": "clothes size",
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
('Red T-Shirt variant', 1, '{"attributes":[]}',
     '{"images":
      [
          {"url":"http://images.com/old_red_image.png","isPrimary":true}
      ]
    }',
  ' {
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
('Green T-Shirt variant', 1, '{"attributes":[]}', '{"images":[]}', '{
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