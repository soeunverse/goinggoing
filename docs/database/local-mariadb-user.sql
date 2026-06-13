-- GoingGoing local MariaDB user setup.
-- Sequel Ace에서 관리자 계정(root 등)으로 접속한 뒤 전체 실행하세요.

CREATE DATABASE IF NOT EXISTS goinggoing
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'goinggoing_user'@'localhost'
  IDENTIFIED BY 'goinggoing_password';

CREATE USER IF NOT EXISTS 'goinggoing_user'@'127.0.0.1'
  IDENTIFIED BY 'goinggoing_password';

GRANT ALL PRIVILEGES ON goinggoing.* TO 'goinggoing_user'@'localhost';
GRANT ALL PRIVILEGES ON goinggoing.* TO 'goinggoing_user'@'127.0.0.1';

FLUSH PRIVILEGES;

SELECT user, host
FROM mysql.user
WHERE user = 'goinggoing_user';
