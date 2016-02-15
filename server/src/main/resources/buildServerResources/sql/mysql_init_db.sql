CREATE DATABASE highlighter;
CREATE USER hghlght_user IDENTIFIED BY 'highlighter';
grant usage on *.* to hghlght_user@localhost identified by 'highlighter';
grant all privileges on highlighter.* to hghlght_user@localhost;