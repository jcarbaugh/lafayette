package com.carbauja.lafayette.data;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Interface for writing the a list of DataSet objects to an OutputStream.
 * The implementing class will handle the serialization of the data sets
 * into the appropriate format for writing to the OutputStream.
 * 
 * @author Jeremy Carbaugh
 */
public interface DataWriter {
	
	/**
	 * Write a list of data sets to an OutputStream.
	 * 
	 * @param dataSets		a set of DataSet objects
	 * @param os			the stream to which the serialized objects will be written
	 * @throws IOException	on write error
	 */
	public void write(List dataSets, Map emptyDataMap, OutputStream os) throws IOException;
	
}