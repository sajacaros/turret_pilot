
create table account (
  account_id bigint(20) AUTO_INCREMENT,
  username varchar(40),
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
   role_name varchar(100) NOT NULL,
   primary key (role_id)
);


alter table user_role add constraint FK_it77eq964jhfqtu54081ebtio foreign key (role_id) references account_role (role_id);
alter table user_role add constraint FK_apcc8lxk2xnug8377fatvbn04 foreign key (user_id) references account (account_id);
ALTER TABLE account_role ADD CONSTRAINT FK_pt47eq964jhfqtu54081ebti3 UNIQUE (role_name);
ALTER TABLE account ADD CONSTRAINT FK_z67aeq934jh4qau54081ebtir UNIQUE (username);