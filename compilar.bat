@echo off
rem 
if not exist out mkdir out
javac -encoding UTF-8 -d out -cp "lib\*" src\*.java src\conexao\*.java src\model\*.java src\dao\*.java src\view\*.java
if %errorlevel%==0 echo Compilado com sucesso.
pause
