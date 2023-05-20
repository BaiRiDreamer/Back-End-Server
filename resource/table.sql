create table if not exists author
(
    author_name              text      not null primary key unique,
    author_registration_time timestamp not null,
    author_phone             varchar(15),
    password                 text      not null
);


create table if not exists shield --屏蔽
(
    author_name     text not null
        constraint author_con_connect references author (author_name),
    author_shielded text --被author屏蔽的
        constraint author_con_connect_shield references author (author_name),
    --增加了约束条件--Lwh,modified, 5.11
    primary key (author_name, author_shielded)
);



create table if not exists author_and_id
(
    author_id   varchar(18) not null unique,
    author_name text        not null primary key unique
        constraint author_con_connect references author (author_name)
);
create table if not exists city
(
    city_id serial not null primary key unique,
    city    text,                                --这里做了修改，将unique条件去除，因为有些城市名字重复，比如中国的北京和美国的北京
    country text,
    constraint uniqueCity unique (city, country) --增加约束条件,保证两者整体唯一--Lwh,modified, 5.11
);
create table if not exists post
(
    post_id         serial    not null primary key unique,
    title           text      not null,
    content         text      not null,
    posting_time    timestamp not null,
    posting_city_id integer   not null
        constraint city_con references city (city_id),
    author_name     text      not null --5.11 改成了不是unique的
        constraint author_con_POST references author (author_name),
    fileName        text,       --文件名--Lwh,modified, 5.11
    file            bytea,      --文件内容
    isUnknown       bool        --表示是不是匿名的，先初始化成false, 之后如果author选择匿名就变成true  --把post传给gui展示时如果这个值是true 就把author name 设置成'unknown'
);


create table if not exists category_post
(
    category varchar(20) not null,
    post_id  integer     not null
        constraint post_id_con references post (post_id),
    primary key (post_id, category)
);


create table if not exists author_followed_by
(
    author_name        text not null
        constraint author_con_foll references author (author_name),
    follow_author_name text
        constraint author_con_foll2 references author (author_name),
    primary key (author_name, follow_author_name)

);
create table if not exists post_favorited
(
    post_id     integer not null
        constraint post_con references post (post_id),
    author_name text
        constraint author_con_favor references author (author_name),--if not in author's field then generate id and add it to author's table
    primary key (post_id, author_name)
);

create table if not exists post_shared
(
    post_id     integer not null
        constraint post_con references post (post_id),
    author_name text
        constraint author_con_share references author (author_name),--if not in author's field then generate id and add it to author's table
    primary key (post_id, author_name)
);

create table if not exists post_liked
(
    post_id     integer not null
        constraint post_con references post (post_id),
    author_name text
        constraint author_con_like references author (author_name),--if not in author's field then generate id and add it to author's table
    primary key (post_id, author_name)
);

create table if not exists reply
(
    reply_id      serial primary key not null,
    post_id       integer            not null
        constraint post_con references post (post_id),
    reply_content text               not null,
    reply_stars   integer            not null,
    reply_author  text               not null
        constraint author_con_reply references author (author_name)--if not in author's field then generate id and add it to author's table
);

create table if not exists sec_reply
(
    sec_reply_id      serial  not null primary key,
    reply_id          integer not null
        constraint reply_con references reply (reply_id),
    sec_reply_content text    not null,
    sec_reply_stars   integer not null,
    sec_reply_author  text    not null
        constraint author_con_secreply references author (author_name)--if not in author's field then generate id and add it to author's table
);
