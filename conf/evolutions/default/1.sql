# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table answer (
  id                        bigint not null,
  user_id                   bigint,
  poll_id                   bigint,
  constraint uq_answer_1 unique (poll_id,user_id),
  constraint pk_answer primary key (id))
;

create table answer_detail (
  id                        bigint not null,
  answer_id                 bigint not null,
  choice_id                 bigint,
  constraint uq_answer_detail_1 unique (choice_id,answer_id),
  constraint pk_answer_detail primary key (id))
;

create table choice (
  id                        bigint not null,
  label                     varchar(255) not null,
  sort_order                integer not null,
  poll_id                   bigint,
  constraint uq_choice_1 unique (label,poll_id),
  constraint pk_choice primary key (id))
;

create table poll (
  id                        bigint not null,
  title                     varchar(255) not null,
  description               varchar(255),
  constraint pk_poll primary key (id))
;

create table user (
  id                        bigint not null,
  username                  varchar(255) not null,
  registered                boolean not null,
  constraint uq_user_username unique (username),
  constraint pk_user primary key (id))
;

create sequence answer_seq;

create sequence answer_detail_seq;

create sequence choice_seq;

create sequence poll_seq;

create sequence user_seq;

alter table answer add constraint fk_answer_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_answer_user_1 on answer (user_id);
alter table answer add constraint fk_answer_poll_2 foreign key (poll_id) references poll (id) on delete restrict on update restrict;
create index ix_answer_poll_2 on answer (poll_id);
alter table answer_detail add constraint fk_answer_detail_answer_3 foreign key (answer_id) references answer (id) on delete restrict on update restrict;
create index ix_answer_detail_answer_3 on answer_detail (answer_id);
alter table answer_detail add constraint fk_answer_detail_choice_4 foreign key (choice_id) references choice (id) on delete restrict on update restrict;
create index ix_answer_detail_choice_4 on answer_detail (choice_id);
alter table choice add constraint fk_choice_poll_5 foreign key (poll_id) references poll (id) on delete restrict on update restrict;
create index ix_choice_poll_5 on choice (poll_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists answer;

drop table if exists answer_detail;

drop table if exists choice;

drop table if exists poll;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists answer_seq;

drop sequence if exists answer_detail_seq;

drop sequence if exists choice_seq;

drop sequence if exists poll_seq;

drop sequence if exists user_seq;

