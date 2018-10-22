use javaproject;

create table files(
file_id int not null auto_increment,
task_id int not null,
file_file longblob not null,
file_name varchar(40) not null,
file_date datetime not null,
primary key(file_id),
FOREIGN KEY (task_id) REFERENCES task(task_id)
)