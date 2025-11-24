cd /d %~dp0
echo %0
echo %1
echo %2
java -jar target/pdfv-0.0.1-jar-with-dependencies.jar -file %1
