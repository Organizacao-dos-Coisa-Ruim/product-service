CREATE TABLE product (
    id_product VARCHAR(36)    NOT NULL,
    tx_name    VARCHAR(256)   NOT NULL,
    price      NUMERIC(19, 2) NOT NULL,
    tx_unit    VARCHAR(32)    NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id_product),
    CONSTRAINT ck_product_price_positive CHECK (price > 0)
);
