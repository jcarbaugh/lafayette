"C:\Program Files\Microsoft Visual Studio 10.0\Common7\Tools\vsvars32.bat"

cl -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -LD lib\edu_american_weiss_lafayette_io_jni_Opto22Controller.cpp -Felib\AduJava.dll -linklib\AduHid.lib
