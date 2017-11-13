drop table trx;

create table trx (
    trxId varchar(50) not null,
    cardId varchar(50),
    price varchar(50),
    dateTime varchar(100),
    POSid varchar(50),
    response varchar(50),
    refId varchar(50),
    status varchar(50),
    primary key (trxId)
);