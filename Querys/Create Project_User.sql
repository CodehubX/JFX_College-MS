use javaproject;

create table project_user(
project_user_id int not null auto_increment,
project_id int not null,
user_id int not null,
primary key(project_user_id),
FOREIGN KEY (project_id) REFERENCES project(project_id),
FOREIGN KEY (user_id) REFERENCES users(user_id)
)