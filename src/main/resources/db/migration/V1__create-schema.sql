
create table oauth_account (
  account_id bigint(20) AUTO_INCREMENT,
  account_non_expired boolean not null,
  account_non_locked boolean not null,
  credentials_non_expired boolean not null,
  enabled boolean not null,
  password varchar(255),
  username varchar(40),
  primary key (account_id)
);

create table user_role (
   user_id bigint not null,
   role_id bigint not null,
   primary key (user_id, role_id)
);

create table account_role (
   role_id bigint(20) AUTO_INCREMENT,
   role_name varchar(255) NOT NULL,
   primary key (role_id)
);


alter table user_role add constraint FK_it77eq964jhfqtu54081ebtio foreign key (role_id) references account_role (role_id);
alter table user_role add constraint FK_apcc8lxk2xnug8377fatvbn04 foreign key (user_id) references oauth_account (account_id);
