create table if not exists mb_user
(
    id             BIGINT  UNIQUE AUTO_INCREMENT PRIMARY KEY,
    client_id      BIGINT  not null,
    chat_id        BIGINT  not null
);
