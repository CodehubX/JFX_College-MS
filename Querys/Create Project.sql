use javaproject;

create table project(
project_id int not null auto_increment,
project_name varchar(20) not null,
project_desc text not null,
project_status varchar(20),
project_date datetime not null,
project_date_due datetime not null,
primary key(project_id)
)