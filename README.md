# Product Service

Демо проект, призванный продемонстрировать один из возможных вариантов хранения в Postgres
данных о товарах, которые могут иметь различные атрибуты с различными значениями. 
Изначально для хранения такой информации больше подходит NoSQL база, тем не менее бывают архитектуры,
в которых оправдано использование реляционного хранилища. Например, в случае применения принципа 
Command and Query Responsibility Segregation (CQRS) когда запись осуществляется в Postgres, а чтение
происходит, допустим, из ElasticSearch, а данные между ними перемещаются по шине, реализованной на Kafka. 

Чтобы увидеть, почему этот вопрос не нак прост, как кажется с первого взгляда,
представим, что в нашем каталоге есть футболки мужские всего с двумя атрибутами:
  - цвет
  - размер

Мы хотим хранить товар с названием "Футболка Айтишника", она представлена в синем, красном и 
зеленом цветах, и каждый цвет доступен в нескольких размерах. Добавляя немного
сложности, представим, что цена конкретной футболки также варьируется в зависимости от цвета и размера. 
Как представить эту сущность в реляционной базе данных, с учетом того, что продавец футболки
может в какой-то момент добавить новые атрибуты для своего товара, например, габариты упаковки товара для отправки
(длина, ширина, высота)? 

С одной стороны, можно использовать подход: [Entity-Attribute-Value](https://en.wikipedia.org/wiki/Entity%E2%80%93attribute%E2%80%93value_model).
Он позволяет гибко настраивать связи между сущностями, их атрибутами и значениями, 
сохраняя возможность динамического добавления новых атрибутов сущности. Однако у такого подхода
есть свои недостатки. На них останавливаться не буду - в статье: ["Замена EAV на JSONB в PostgreSQL"](https://habr.com/ru/articles/475178/)
они приведены, также там есть сравнение по производительности и памяти EAV и JSONB. 

Я же постараюсь показать на конкретном примере, как можно использовать JSONB
для решения этой, а также сопутствующих задач обновления и удаления атрибутов и значений
с помощью функций Postgres для работы с JSONB. 

## Модели данных

### Product и ProductVariant

Будем хранить товары в таблице со следующей структурой:
```sql
CREATE TABLE products(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    shop_id BIGINT REFERENCES shops(id),
    category_id BIGINT REFERENCES categories(id),
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```
То есть, в таблице products мы храним только общую для товара информацию, а детали о вариантах
товара, формируемых на основе комбинаций различных атрибутов, мы будем хранить в отдельной таблице:

```sql
CREATE TABLE product_variants (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    variant_name TEXT NOT NULL,
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    attribute_collection JSONB,
    image_collection JSONB,
    sku_collection JSONB,
    price DECIMAL NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```
Для нас интерес представляют три поля:
- image_collection
- attribute_collection
- sku_collection

#### image_collection

Каждый вариант товара может иметь одну или несколько фотографий. При этом мы храним только url 
на фото, и флаг, является ли это фото основным в коллекции. ImageCollection представлена 
следующим классом:

```kotlin
data class AppImage(
    val url: String,
    val isPrimary: Boolean = false
)

data class ImageCollection(
    val images: MutableSet<AppImage> = hashSetOf()
)
```

#### attribute_collection

Это более сложный случай, который рассмотрим подробно. Атрибуты разделены на типы: строковый,
числовой, цветовой и составной, значения атрибутов также разделяются по аналогичным типам, кроме составного. 
В принципе, в поле JSONB может хранить `Map<String, Any>`, которая и будет тем JSON объектом,
приходящим с фронта при создании товара. Однако такой вариант лишает нас строгой типизации
данных и чреват ошибками. Поэтому воспользуемся возможностями библиотеки Jackson по сериализации/десериализации
иерархии классов, в частности аннотациями `@JsonTypeInfo` и `@JsonSubTypes`, использующимися для того, 
чтобы подсказать Jackson, с каким реально классом сейчас идет работа. Подробнее можно почитать [тут](https://www.baeldung.com/jackson-inheritance).

Итак, значения атрибутов выглядят следубщим образом:
```kotlin
enum class AttributeValueType(
    @field:JsonValue
    val value: String
) {
    STRING_TYPE("string_type"),
    NUMERIC_TYPE("numeric_type"),
    COLOR_TYPE("color_type")
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    visible = true,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type"
)
@JsonSubTypes(
    *arrayOf(
        JsonSubTypes.Type(
            value = StringAttributeValueDto::class,
            name = "string_type"
        ),
        JsonSubTypes.Type(
            value = NumericAttributeValueDto::class,
            name = "numeric_type"
        ),
        JsonSubTypes.Type(
            value = ColorAttributeValueDto::class,
            name = "color_type"
        )
    )
)
sealed class AttributeValueDto(
    open val type: AttributeValueType
)

data class StringAttributeValueDto(
    val stringValue: String,
    val stringRuValue: String?,
    override val type: AttributeValueType = AttributeValueType.STRING_TYPE
) : AttributeValueDto(type)

data class NumericAttributeValueDto(
    val numValue: Double,
    val numRuValue: Double?,
    val numUnit: String,
    override val type: AttributeValueType = AttributeValueType.NUMERIC_TYPE
) : AttributeValueDto(type)

data class ColorAttributeValueDto(
    val colorValue: String,
    val colorHex: String,
    override val type: AttributeValueType = AttributeValueType.COLOR_TYPE
) : AttributeValueDto(type)
```

Сами же атрибуты представлены следующей иерархией классов, в которой ключевой момент состоит 
в том, чтобы обеспечить совпадение типов атрибутов и значений:

```kotlin
enum class AttributeType(
    @field:JsonValue
    val value: String
) {
    STRING_ATTR("string"),
    NUMERIC_ATTR("numeric"),
    COLOR_ATTR("color"),
    COMPOSITE_ATTR("composite")
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(*arrayOf(
    JsonSubTypes.Type(
        value = StringAttributeDto::class,
        name = "string"
    ),
    JsonSubTypes.Type(
        value = NumericAttributeDto::class,
        name = "numeric"
    ),
    JsonSubTypes.Type(
        value = ColorAttributeDto::class,
        name = "color"
    ),
    JsonSubTypes.Type(
        value = CompositeAttributeDto::class,
        name = "composite"
    )
))
sealed class AttributeDto(
    open val id: String?,
    open val attributeName: String,
    open val shortName: String,
    open val isFaceted: Boolean,
    open val type: AttributeType,
    open val createdAt: LocalDateTime?,
    open val isRequired: Boolean? = null,
    open val availableValues: List<AttributeValueDto>
)

data class StringAttributeDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    override val attributeName: String,
    override val shortName: String,
    override val isFaceted: Boolean,
    override val type: AttributeType = AttributeType.STRING_ATTR,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val isRequired: Boolean? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val createdAt: LocalDateTime? = null,
    override val availableValues: MutableList<StringAttributeValueDto>,
) : AttributeDto(id, attributeName, shortName, isFaceted, type, createdAt, isRequired, availableValues)

data class NumericAttributeDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    override val attributeName: String,
    override val shortName: String,
    override val isFaceted: Boolean,
    override val type: AttributeType = AttributeType.NUMERIC_ATTR,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val isRequired: Boolean? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val createdAt: LocalDateTime? = null,
    override val availableValues: MutableList<NumericAttributeValueDto>,
) : AttributeDto(id, attributeName, shortName, isFaceted, type, createdAt, isRequired, availableValues)

data class ColorAttributeDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    override val attributeName: String,
    override val shortName: String,
    override val isFaceted: Boolean,
    override val type: AttributeType = AttributeType.COLOR_ATTR,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val isRequired: Boolean? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val createdAt: LocalDateTime? = null,
    override val availableValues: MutableList<ColorAttributeValueDto>,
) : AttributeDto(id, attributeName, shortName, isFaceted, type, createdAt, isRequired, availableValues)

data class CompositeAttributeDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val id: String? = null,
    override val attributeName: String,
    override val shortName: String,
    override val isFaceted: Boolean,
    override val type: AttributeType = AttributeType.COMPOSITE_ATTR,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val isRequired: Boolean? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    override val createdAt: LocalDateTime? = null,
    val subAttributes: List<AttributeDto>
): AttributeDto(id, attributeName, shortName, isFaceted, type, createdAt, isRequired, listOf())
```

Особняком стоит составной тип атрибутов. Например, атрибут "Габариты", может содержать в себе
такие под-атрибуты, как: "Длина", "Ширина" и "Высота". Сам составной атрибут не имеет непосредственного
списка значений, так как все значения принадлежат конкретным под-атрибутам. 

#### sku_collection

Немаловажной частью варианта товара является его артикул или [Stock Keeping Unit (SKU)](https://www.shopify.com/blog/what-is-a-stock-keeping-unit#),
который позволяет уникально идентифицировать конкретную единицу хранения товара и отслеживать ее наличие 
на складе.

В нашем случае, каждый вариант товара будет иметь список артикулов, представленных в следующем виде:
```kotlin
data class Sku(
    val attrValue: String,
    val price: BigDecimal,
    val stock: Int,
    val sku: String
)

data class SkuCollection(
    val attrName: String,
    val skus: List<Sku>
)
```

При формировании `SkuCollection`, один из атрибутов товара будет определяющим для конкретной единицы товара 
(этот атрибут задается продавцом). Постараюсь пояснить, например, у нас есть товар "Футболка Айтишника", 
в нескольких вариантах, определяемых цветами, и каждый вариант имеет несколько размеров, при этом количество 
штук каждой футболки определенного цвета, определенного размера различается:
- синяя
  - XL - 2шт, XXL - 4шт
- зеленая
  - M - 3шт, S - 1шт
- красная
  - XS - 10шт

Таким образом, в этом случае атрибут, определяющий конкретную единицу товара - размер, и имменно это
хранится в поле `SkuCollection.attrName`. Поле `Sku.price` обеспечивает возможность хранить цену товара 
в зависимости от его артикула. Поле `Sku.stock` - указывает количество единиц товара на складе. 

Для того, чтобы не усложнять проект, сам уникальный Stock Keeping Unit - поле `Sku.sku` приходит извне, 
и наш сервис не отвечает за его формирование и обеспечение уникальности, читаемости и удобства использования.

## Attribute и Category

Итак, сервис может хранить товары, их атрибуты и значения в удобном нам формате, но как быть
с тем, чтобы на фронте был удобный UI для создания товаров и заполнения информации об атрибутах?
Например, мы хотим добавить новый товар "Футболка Senior Developer", для этого нам необходимо
на фронте выбрать соответствующую категорию и заполнить обязательные атрибуты для нее, указать
артикулы, их количество и цену. При заполнении атрибута "цвет" мы хотим, чтобы продавец не 
руками вводил нужный ему цвет, а выбрал из списка предложенных наиболее подходящий, и только в отсутствии
такового - заполнил поле самостоятельно. Аналогично происходит и с другими атрибутами. Для
этого нам необходимо структурированно хранить данные на бэке.

Для реализации задачи используются следующие таблицы в БД. 

```sql
CREATE TABLE categories(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    parent_id BIGINT REFERENCES categories(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

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

CREATE TABLE category_attributes(
    category_id BIGINT REFERENCES categories(id) NOT NULL,
    attribute_id BIGINT REFERENCES attributes(id) NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT false,
    PRIMARY KEY(category_id, attribute_id)
);
```

Для представления данных об атрибутах в коде используется иерархия, аналогичная той, которая применяется 
для `AttributeDto`, однако так как в проекте используется Spring Data JPA и Hibernate в качесте persistence provider,
необходимо подсказать Hibernate, какой тип наследования использовать. В нашем случае это   
```InheritanceType.SINGLE_TABLE``` - когда данные обо всех подтипах хранятся в одной таблице, а сами
подтипы различаются значением, указанным в `@DiscriminatorValue`. Плюсом такого подхода
является простота реализации и скорость выборки как при полиморфных запросах, так и при запросе конкретного подтипа.
Одним из минусов является невозможность наложить `NOT NULL` ограничения на уровне базы на столбцы, которые
присутствуют только в подтипах. 

Собственно так выглядят сами модели: 

```kotlin
@Entity
@Table(name = "attributes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "a_type")
abstract class Attribute(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    @Column(name = "short_name", nullable = false)
    var shortName: String,
    @Column(name = "is_faceted", nullable = false)
    var isFaceted: Boolean,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "composite_attribute_id")
    var compositeAttribute: CompositeAttribute? = null
) {

    @Column(name = "created_at")
    @org.hibernate.annotations.CreationTimestamp
    var createdAt: LocalDateTime? = null

    @Column(name = "updated_at")
    @org.hibernate.annotations.UpdateTimestamp
    var updatedAt: LocalDateTime? = null

    override fun equals(other: Any?): Boolean {
        val o = other as? Attribute ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

@Entity
@DiscriminatorValue("SA")
class StringAttribute(
    name: String,
    shortName: String,
    isFaceted: Boolean,
    @org.hibernate.annotations.Type(JsonBinaryType::class)
    @Column(name = "string_values", columnDefinition = "jsonb")
    var stringValues: MutableList<StringAttributeValueDto> = arrayListOf()
) : Attribute(name = name, shortName = shortName, isFaceted = isFaceted)  {

    override fun toString(): String {
        return "StringAttribute(id=$id, name='$name', shortName='$shortName', isFaceted=$isFaceted)"
    }
}

@Entity
@DiscriminatorValue("NA")
class NumericAttribute(
    name: String,
    shortName: String,
    isFaceted: Boolean,
    @org.hibernate.annotations.Type(JsonBinaryType::class)
    @Column(name = "numeric_values", columnDefinition = "jsonb")
    var numericValues: MutableList<NumericAttributeValueDto> = arrayListOf()
) : Attribute(name = name, shortName = shortName, isFaceted = isFaceted) {

    override fun toString(): String {
        return "NumericAttribute(id=$id, name='$name', shortName='$shortName', isFaceted=$isFaceted)"
    }
}

@Entity
@DiscriminatorValue("CLA")
class ColorAttribute(
    name: String,
    shortName: String,
    isFaceted: Boolean,
    @org.hibernate.annotations.Type(JsonBinaryType::class)
    @Column(name = "color_values", columnDefinition = "jsonb")
    var colorValues: MutableList<ColorAttributeValueDto> = arrayListOf()
) : Attribute(name = name, shortName = shortName, isFaceted = isFaceted) {

    override fun toString(): String {
        return "ColorAttribute(id=$id, name='$name', shortName='$shortName', isFaceted=$isFaceted)"
    }
}

@Entity
@DiscriminatorValue("CA")
class CompositeAttribute(
    name: String,
    shortName: String,
    isFaceted: Boolean,
    @OneToMany(mappedBy = "compositeAttribute", cascade = [CascadeType.PERSIST])
    var subAttributes: MutableSet<Attribute> = hashSetOf()
) : Attribute(name = name, shortName = shortName, isFaceted = isFaceted) {

    override fun toString(): String {
        return "CompositeAttribute(id=$id, name='$name', shortName='$shortName', isFaceted=$isFaceted)"
    }
}
```

Для того, чтобы Hibernate корректно конвертировал наш тип данных в JSONB и обратно необходимо добавить
в проект зависимость на Hypersistence Utils`io.hypersistence:hypersistence-utils-hibernate-63:3.7.0` 
(версию необходимо кореллировать с версией Hibernate), а также указать аннотацию 
`@org.hibernate.annotations.Type(JsonBinaryType::class)` над требуемым полем. Более подробно о том, как
мапить данные в JSONB можно почитать [тут](https://vladmihalcea.com/how-to-map-json-objects-using-generic-hibernate-types/). 

Собственно на этом заканчивается описание модели данных, и начинается работа с [функциями Postgres для JSONB](https://www.postgresql.org/docs/16/functions-json.html).

## Обновление и удаление с JSONB и jsonb_set

Основная логика, связанная с обновлением данных о варианте товара с помощью функции `jsonb_set` находится
в репозитории: `ProductVariantRepository`. Рассмотрим подробно несколько SQL запросов.
К сожалению, при использовании Spring Data JPA мы можем использовать только нативные запросы, если
хотим работать с `jsonb_set`.

### Обновляем цену артикула
Начнем с простого. Будем исходить из знания о том, что в таблице product_variants хранится запись о товаре 
со следующим списком артикулов:
```json
{
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
  }
```
И нам требуется изменить цену нулевого артикула с `sku="t-shirt/blue/XS/Brand/230"`. Чтобы этого добиться
мы можем воспользоваться функцией `jsonb_set`. Ниже приведу полное ее описание из документации.  

```text
jsonb_set ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean ] ) → jsonb
Returns target with the item designated by path replaced by new_value, or with new_value added if create_if_missing is true (which is the default) and the item designated by path does not exist. All earlier steps in the path must exist, or the target is returned unchanged. As with the path oriented operators, negative integers that appear in the path count from the end of JSON arrays. If the last path step is an array index that is out of range, and create_if_missing is true, the new value is added at the beginning of the array if the index is negative, or at the end of the array if it is positive.
jsonb_set('[{"f1":1,"f2":null},2,null,3]', '{0,f1}', '[2,3,4]', false) → [{"f1": [2, 3, 4], "f2": null}, 2, null, 3]
jsonb_set('[{"f1":1,"f2":null},2]', '{0,f3}', '[2,3,4]') → [{"f1": 1, "f2": null, "f3": [2, 3, 4]}, 2]
```

Итак, из документации становится понятно, что для того, чтобы обновить значение в JSONB, нам 
необходимо найти конкретный путь до поля, значение которого мы хотим поменять. В нашем случае - 
это '{sku,0,price}', где 0 - индекс конкретного элемента `Sku` в списке. Однако при получении 
запроса на обновление нам не передается индекс. Для того, чтобы его найти, воспользуемся 
CTE и функцией `jsonb_array_elements`, раскрывающей элементы из JSON списка во множество значений. 
Также нам потребуется выражение `WITH ORDINALITY`, которое добавляет в выходные данные столбец типа `bigint`,
числа в котором начинаются с 1 и увеличиваются на 1 для каждой строки, выданной функцией. 
Тут стоит учитывать, что индексы в JSONB начинаются с 0, поэтому из полученного с помощью
`WITH ORDINALITY` индекса необходимо вычесть единицу. Также стоит запомнить, что `WITH ORDINALITY`
возвращает `bigint`, а индексы в пути должны быть типа `int`, и нам в дальнейшем понадобится их кастить.
Так как путь должен быть прведен к типу `text[]`, воспользуемся нативным синтаксисом Postgres. При приведении к типу 
`text[]` необходимо экранировать двоеточия обратным слэшем.
Так выглядит запрос CTE.

```sql
WITH sku_path AS (
    SELECT ('{skus,'||index - 1||',price}')\:\:text[] AS path
    FROM product_variants, jsonb_array_elements(sku_collection->'skus') WITH ORDINALITY arr(sku_element, index)
    WHERE id = :variantId AND sku_element->>'sku' = :sku
)
```
Также стоит обратить внимание на специфичный для JSONB оператор `->>`. Он возвращает значение
в виде строки. 

Осталось дело за малым - передать полученный путь в функцию `jsonb_set`. Так выглядит 
метод для обновления цены артикула.

```kotlin
@Modifying
@Query(
  value = """
          WITH sku_path AS (
              SELECT ('{skus,'||index - 1||',price}')\:\:text[] AS path
              FROM product_variants, jsonb_array_elements(sku_collection->'skus') WITH ORDINALITY arr(sku_element, index)
              WHERE id = :variantId AND sku_element->>'sku' = :sku
          )
          UPDATE product_variants
          SET sku_collection = jsonb_set(sku_collection, sku_path.path, to_jsonb(:newPrice), false)
          FROM sku_path 
          WHERE id = :variantId
       """, nativeQuery = true
)
fun updateSkuPrice(
  @Param("sku") sku: String,
  @Param("variantId") variantId: Long,
  @Param("newPrice") newPrice: BigDecimal
): Int
```

Обновление поля `stock` у конкретного артикула происхоит аналогичным образом. 

### Добавление в конец списка

Теперь предположим, что нам требуется добавить в конец списка в JSONB какое-то значение, например,
еще одну фотографию. 

Допустим, сейчас храниться только одно фото в image_collection:
```json
{
  "images": [
    {"url":"http://images.com/old_blue_image.png","isPrimary":true}
  ]
}
```

Как и в предыдущем случае воспользуемся функцией `jsonb_set`. Сейчас мы изначально знаем путь до
элемента JSONB, в конец которого добавляем новый элемент, поэтому нам не понадобится CTE.
Однако нам необходимо сконвертировать добавляемый в коллекцию класс `AppImage` в строку в виде JSON,
так как иначе мы не сможем его скастить в запросе к типу `jsonb`. Для добавления в конец списка 
используется оператор `||`. Полный запрос будет выглядеть следующим образом:
```kotlin
@Modifying
@Query(
  """
       UPDATE product_variants
       SET image_collection = jsonb_set(image_collection, '{images}', (image_collection->'images')\:\:jsonb || (:photo)\:\:jsonb, false)
       WHERE id = :variantId
   """, nativeQuery = true
)
fun addImage(
  @Param("variantId") variantId: Long,
  @Param("photo") photo: String
): Int
```

### Удаление из списка

К сожалению, нет функции, которая позволяет просто удалить значение из списка в JSONB. Однако это не 
безвыходная ситуация. Можем воспользоваться комбинацией функций: `jsonb_set`, `jsonb_array_elements` и `jsonb_agg`.
Первые две уже знакомы, `jsonb_agg` аггрегирует передаваемые значения в JSON список. 

Логика удаления такая: проходим по каждому элементу списка, оставляем в нем только те элементы,
которые не удовлетворяют условию удаления, агрегируем их обратно в список. Но тут есть важный момент: 
если после удаления в результирующем списке не останется элементов, то `jsonb_agg` вернет `NULL`, а нам 
требуется сформировать пустой список, для этого воспользуемся функцией `COALESCE`, которая оставляет 
первое не `NULL` значение. Первым параметром будет результат `jsonb_agg`, а вторым - пустой список. 

Сам запрос:
```kotlin
@Modifying
@Query(
  """
       UPDATE product_variants
       SET image_collection = jsonb_set(
           image_collection,
           '{images}',
           COALESCE(
               (SELECT jsonb_agg(elem) 
               FROM jsonb_array_elements(image_collection->'images') elem 
               WHERE elem->>'url' != :imageUrl),
               '[]'\:\:jsonb
           )    
       )
       WHERE id = :variantId
   """, nativeQuery = true
)
fun removeImage(
  @Param("variantId") variantId: Long,
  @Param("imageUrl") imageUrl: String
): Int
```

### Удаление из списка при двойной вложенности

Допустим, что сейчас у товара следующий набор атрибутов:
```json
{
  "attributes": [
    {
      "id": "colorAttrId",
      "attributeName": "color",
      "shortName": "color",
      "isFaceted": true,
      "type": "color",
      "isRequired": true,
      "availableValues": [
        {
          "colorValue": "red",
          "colorHex": "FF0000",
          "type": "color_type"
        }
      ]
    },
    {
      "id": "dimensId",
      "attributeName": "clothes dimensions",
      "shortName": "dimensions",
      "isFaceted": true,
      "type": "composite",
      "isRequired": false,
      "subAttributes": [
        {
          "attributeName": "width",
          "shortName": "width",
          "isFaceted": true,
          "type": "numeric",
          "availableValues": [
            {
              "numValue": 10.0,
              "numRuValue": null,
              "numUnit": "mm",
              "type": "numeric_type"
            },
            {
              "numValue": 20.0,
              "numRuValue": null,
              "numUnit": "mm",
              "type": "numeric_type"
            },
            {
              "numValue": 30.0,
              "numRuValue": null,
              "numUnit": "mm",
              "type": "numeric_type"
            }
          ]
        },
        {
          "attributeName": "length",
          "shortName": "length",
          "isFaceted": true,
          "type": "numeric",
          "availableValues": [
            {
              "numValue": 10.0,
              "numRuValue": null,
              "numUnit": "mm",
              "type": "numeric_type"
            },
            {
              "numValue": 20.0,
              "numRuValue": null,
              "numUnit": "mm",
              "type": "numeric_type"
            },
            {
              "numValue": 30.0,
              "numRuValue": null,
              "numUnit": "mm",
              "type": "numeric_type"
            }
          ]
        },
        {
          "attributeName": "depth",
          "shortName": "depth",
          "isFaceted": true,
          "type": "numeric",
          "availableValues": [
            {
              "numValue": 10.0,
              "numRuValue": null,
              "numUnit": "mm",
              "type": "numeric_type"
            },
            {
              "numValue": 20.0,
              "numRuValue": null,
              "numUnit": "mm",
              "type": "numeric_type"
            },
            {
              "numValue": 30.0,
              "numRuValue": null,
              "numUnit": "mm",
              "type": "numeric_type"
            }
          ]
        }
      ],
      "availableValues": []
    }
  ]
}
```

И нам необходимо в атрибуте "clothes dimensions", в под-атрибуте "width" удалить значение "numValue" = 10.0.
Для того, чтобы это сделать, нам придется:
1. Найти индекс атрибута с названием "clothes dimensions"
```sql
WITH attr_path AS (
      SELECT CAST((index - 1) AS INTEGER) AS idx 
      FROM product_variants, jsonb_array_elements(attribute_collection->'attributes') WITH ORDINALITY arr(elem, index)
      WHERE id = :variantId AND elem->>'attributeName' = :attrName
),
```
2. Найти индекс под-атрибута с названием "width" и сформировать путь до списка `availableValues`.
```sql
sub_attr_path AS (
    SELECT('{attributes,'||(SELECT idx FROM attr_path)||',subAttributes,'||index - 1||',availableValues}')\:\:text[] as path,
    CAST((index - 1) AS INTEGER) AS idx
    FROM product_variants, jsonb_array_elements(attribute_collection->'attributes'->(SELECT idx AS INTEGER FROM attr_path)->'subAttributes') WITH ORDINALITY arr(elem, index)
    WHERE id = :variantId AND elem->>'attributeName' = :subAttrName
)
```
3. Пройти по списку "availableValues" и оставить в нем только те, элементы, которые не удовлетворяют условию удаления - то есть все, не равные 10.0.
```sql
COALESCE(
         (SELECT jsonb_agg(elem)
             FROM jsonb_array_elements(attribute_collection->'attributes'->ap.idx->'subAttributes'->sap.idx->'availableValues') elem
             WHERE CAST(elem->>'numValue' AS DOUBLE PRECISION) != :numValue 
         ),
         '[]'\:\:jsonb
     )
```
В целом задача похожа на предыдущие, только добавляется второй уровень вложенности. 
Общий запрос выглядит следующим образом:

```kotlin
@Modifying
@Query("""
       WITH attr_path AS (
           SELECT CAST((index - 1) AS INTEGER) AS idx 
           FROM product_variants, jsonb_array_elements(attribute_collection->'attributes') WITH ORDINALITY arr(elem, index)
           WHERE id = :variantId AND elem->>'attributeName' = :attrName
       ),
        sub_attr_path AS (
           SELECT('{attributes,'||(SELECT idx FROM attr_path)||',subAttributes,'||index - 1||',availableValues}')\:\:text[] as path,
           CAST((index - 1) AS INTEGER) AS idx
           FROM product_variants, jsonb_array_elements(attribute_collection->'attributes'->(SELECT idx AS INTEGER FROM attr_path)->'subAttributes') WITH ORDINALITY arr(elem, index)
           WHERE id = :variantId AND elem->>'attributeName' = :subAttrName
       )
       UPDATE product_variants
       SET attribute_collection = jsonb_set(
           attribute_collection,
           sap.path,
           COALESCE(
                    (SELECT jsonb_agg(elem)
                        FROM jsonb_array_elements(attribute_collection->'attributes'->ap.idx->'subAttributes'->sap.idx->'availableValues') elem
                        WHERE CAST(elem->>'numValue' AS DOUBLE PRECISION) != :numValue 
                    ),
                    '[]'\:\:jsonb
                )
       )
       FROM attr_path ap, sub_attr_path sap 
       WHERE id = :variantId
   """, nativeQuery = true)
fun removeCompositeAttributeNumericValue(
  @Param("variantId") variantId: Long,
  @Param("attrName") attrName: String,
  @Param("subAttrName") subAttrName: String,
  @Param("numValue") numValue: Double,
  ): Int
```

Остальные запросы очень похожи на то, что разобрано выше, поэтому не составят труда для понимания. 
Для наглядности в проекте есть тесты как `ProductVariantRepository`, так и `ProductVariantController` - 
запуск тестов потребует наличия Docker, так как используется библиотека TestContainers. 

## Примечание
Функционал работы с JSONB в Postgres гораздо обширнее того, что рассмотрено выше. Поддерживается множество других 
функций, [аггрегирующие операторы](https://www.postgresql.org/docs/16/functions-aggregate.html), 
можно [создать GIN индекс](https://postgrespro.ru/docs/postgrespro/9.5/datatype-json) с классом
операторов `jsonb_ops` или `jsonb_path_ops` отличающихся по набору доступных операторов, 
производительности и памяти. 
 

## Дополнительно
Сервис выставляет наружу несколько REST ручек для CRUD операций с такими сущностями как:
- Seller
  - POST /v1/sellers - создать продавца
  - GET /v1/sellers/{id} - получить продавца по id
- Shop
  - POST /v1/shops - создать магазин
  - GET /v1/shops/{id} - получить магазин по id
- Category
  - POST /v1/categories - создать категорию
  - GET /v1/categories/{id} - получить категорию по id
  - GET /v1/categories - получить все категории
  - PUT /v1/categories/{id}/attributes - добавить атрибут в категорию
- Attribute
  - POST /v1/attributes - создать атрибут
  - GET /v1/attributes/category/{categoryName} - получить атрибуты для категории
  - GET /v1/attributes/all - получить все атрибуты 
  - GET /v1/attributes/{name} - получить атрибут по названию
  - PUT /v1/attributes/{id}/value - добавить значение атрибуту
- Product
  - POST /v1/products - создать товар
  - GET /v1/products/{id} - получить товар по id
  - GET /v1/products/variant/{id} - получить список вариантов товара по id
  - PUT /v1/products/{productId}/variant - добавить вариант товара
  - DELETE /v1/products/{productId}/variant/{variantId} - удалить вариант товара
  - DELETE /v1/products/{productId} - удалить товар
  - PATCH /v1/products/{productId} - обновить товар
- ProductVariant
  - PATCH /v1/product-variants/sku-stock - обновить количество товара по артикулу
  - PATCH /v1/product-variants/sku-price - обновить цену артикула
  - PATCH /v1/product-variants/price - обновить цену варианта товара
  - PATCH /v1/product-variants/{id}/name - обновить название варианта товара
  - PATCH /v1/product-variants/{id}/photo - добавить фото 
  - DELETE /v1/product-variants/{id}/photo - удалить фото
  - PATCH /v1/product-variants/{id}/attribute - добавить атрибут
  - DELETE /v1/product-variants/{id}/attribute - удалить атрибут
  - PATCH /v1/product-variants/{id}/attribute-value - добавить значение атрибуту
  - DELETE /v1/product-variants/{id}/attribute-value-delete - удалить значение у атрибута
  - PATCH /v1/product-variants/{id}/composite-attribute-value - добавить значение составному атрибуту
  - DELETE /v1/product-variants/{id}/composite-attribute-value-delete - удалить значение у составного атрибута

Также в сервисе реализовано транзакционное сохранение событий о добавлении / обновление товаров и их вариантов
в отдельные outbox таблицы, которые могут вычитываться коннекторами и отправлять данные, например, в Kafka. 


## Запуск приложения
1. Перейти в директорию docker и стартовать контейнер с Postgres 
```shell
docker-compose up -d
```
2. Выполнить чистый билд без тестов
```shell
./gradlew clean build -x test
```
3. Стартовать приложение
```shell
./gradlew bootRun
```

### Известные проблемы
1. Тесты `ProductVariantControllerTest` и `ProductVariantRepositoryTest` необходимо запускать
по отдельности от остальных тестов, так как не может отработать инициализационный скрипт 
`insert_product.sql` из-за того, что в некоторых тестах происходит очищение базы после каждого тестового
метода, а в этих классах - после всех методов. 