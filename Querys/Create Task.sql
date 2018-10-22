use javaproject;

create table task(
task_id int not null auto_increment,
user_id int not null,
project_id int not null,
task_name varchar(20) not null,
task_desc text not null,
task_importance varchar(20) not null,
task_status varchar(20),
task_date datetime not null,
task_date_due datetime not null,
task_approval varchar(20),
primary key(task_id),
FOREIGN KEY (user_id) REFERENCES users(user_id),
FOREIGN KEY (project_id) REFERENCES project(project_id)
)