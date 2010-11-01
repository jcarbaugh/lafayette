// JNI Wrapper code for the ADU demo
#include <jni.h>
#include <stdio.h>
#include "edu_american_weiss_lafayette_io_jni_AduJava.h"
#include "AduHid.h"

//
// Java Wrapper Version Display
//
JNIEXPORT jint JNICALL Java_edu_american_weiss_lafayette_io_jni_AduJava_displayVersion
  (JNIEnv *env, jobject obj)
{
	printf ("AduJava - Version 0.0.0.1 - Alpha Test\n");
	return 0001;
}

//
// Java Wrapper for OpenAduDevice
//
JNIEXPORT jint JNICALL Java_edu_american_weiss_lafayette_io_jni_AduJava_OpenAduDevice
  (JNIEnv *env, jobject obj, jint iTimeout)
{
	void * hDevice;
	
	hDevice = OpenAduDevice(iTimeout);
	return (long)hDevice;
}

//
// Java Wrapper for WriteAduDevice
//
JNIEXPORT jint JNICALL Java_edu_american_weiss_lafayette_io_jni_AduJava_WriteAduDevice
  (JNIEnv *env, jobject obj, jint hDevice, jstring jBuffer, 
   jint nNumberOfBytesToWrite, jint iTimeout)
{
	long iRC;
	long iBytesWritten; // NOTE: this is not passed back to java
	const char *psCommand;

	psCommand = (*env)->GetStringUTFChars(env, jBuffer, 0);
	
	iRC = WriteAduDevice((void*)hDevice, psCommand, 
                  nNumberOfBytesToWrite, &iBytesWritten, iTimeout);
	(*env)->ReleaseStringUTFChars(env, jBuffer, psCommand);
	
	return iRC;
}

//
// Java Wrapper for ReadAduDevice
//
JNIEXPORT jstring JNICALL Java_edu_american_weiss_lafayette_io_jni_AduJava_ReadAduDevice
  (JNIEnv *env, jobject obj, jint hDevice, jint nNumberOfBytesToRead, 
   jint iTimeout)
{
#define MAX_SIZE 64
	int iRC = 0;
	int iBytesRead = 0;
	int iSize = MAX_SIZE;
	char sResponse[MAX_SIZE];
	
	memset(sResponse, 0, MAX_SIZE);
	if (nNumberOfBytesToRead < MAX_SIZE) 
	{
		iSize = nNumberOfBytesToRead;
	}

	iRC = ReadAduDevice((void*)hDevice, sResponse, 
                  nNumberOfBytesToRead, &iBytesRead, iTimeout);
	
	if (iRC == 0 || iBytesRead < 1)
	{
		strcpy(sResponse, "Error");
	}
	return (*env)->NewStringUTF(env, sResponse);
}

// 
// Java Wrapper for CloseAduDevice
//
JNIEXPORT void JNICALL Java_edu_american_weiss_lafayette_io_jni_AduJava_CloseAduDevice
  (JNIEnv *env, jobject obj, jint hDevice)
{
	CloseAduDevice((void*)hDevice);
}

