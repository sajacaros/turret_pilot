
create table account (
  account_id bigint(20) AUTO_INCREMENT,
  username varchar(30),
  password varchar(255),
  name varchar(50),
  account_non_expired boolean not null,
  account_non_locked boolean not null,
  credentials_non_expired boolean not null,
  enabled boolean not null,
  created_date timestamp,
  updated_date timestamp,
  primary key (account_id)
);

create table user_role (
   user_id bigint not null,
   role_id bigint not null,
   primary key (user_id, role_id)
);

create table account_role (
   role_id bigint(20) AUTO_INCREMENT,
   role_name varchar(30) NOT NULL,
   primary key (role_id)
);
create table third (
    third_id bigint(20) AUTO_INCREMENT,
    name varchar(30),
    symbol varchar(30),
    access_token varchar(500),
    enabled boolean not null,
    expired_date timestamp,
    life_time bigint,
    created_date TIMESTAMP not null,
    updated_date TIMESTAMP not null,
    role_id bigint not null,
    primary key (third_id)
);

alter table user_role add constraint FK_it77eq964jhfqtu54081ebtio foreign key (role_id) references account_role (role_id);
alter table user_role add constraint FK_apcc8lxk2xnug8377fatvbn04 foreign key (user_id) references account (account_id);
alter table third add constraint FK_iecz8qxk2fnughfqtu54081ebti foreign key (role_id) references account_role (role_id);
alter table third add constraint UK_go9mqeyifewi6piiwvbw3iwwy unique (symbol);
ALTER TABLE account_role ADD CONSTRAINT UK_pt47eq964jhfqtu54081ebti3 UNIQUE (role_name);
ALTER TABLE account ADD CONSTRAINT UK_z67aeq934jh4qau54081ebtir UNIQUE (username);