Login - session manager
-----
because of requirements there's no database/dao implementation or login validation.
I assume, that user login is validated in other class (eg. LoginController).
At the moment every login is accepted by App.

Sessions mechanic implemented in package server.sessions

After about 5 minutes of inactivity, the session automatically expires.

to compile & run from jar:

mvn clean package

java -jar target/login-1.0-SNAPSHOT-shaded.jar