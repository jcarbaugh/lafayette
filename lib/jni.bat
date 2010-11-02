cl -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -LD ".\edu_american_weiss_lafayette_io_jni_ADUController.c" -Fe"adu.dll" -link"AduHid.lib"
