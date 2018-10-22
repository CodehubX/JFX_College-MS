use javaproject;

create table users(
user_id int not null auto_increment,
user_name varchar(50) not null,
user_email varchar(50) not null,
user_pass varchar(255) not null,
user_date datetime not null,
user_type varchar(20) not null,
primary key(user_id)
);