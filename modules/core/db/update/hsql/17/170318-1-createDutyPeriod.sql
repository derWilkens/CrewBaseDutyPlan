create table DEMO_DUTY_PERIOD (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer not null,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    LASTNAME varchar(50),
    BEGIN_DATE timestamp,
    END_DATE timestamp,
    --
    primary key (ID)
);
