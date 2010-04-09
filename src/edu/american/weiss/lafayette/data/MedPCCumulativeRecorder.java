package edu.american.weiss.lafayette.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.CompositeTransitionEvent;
import edu.american.weiss.lafayette.event.DestroyEvent;
import edu.american.weiss.lafayette.event.ReinforcerEvent;
import edu.american.weiss.lafayette.event.ResponseEvent;

public abstract class MedPCCumulativeRecorder extends CumulativeRecorder {
	
	private final String NEWLINE = System.getProperty("line.separator");

	private int subjectNumber;
	private int experimentNumber;
	private int groupNumber;
	private int recordNumber;
	private long dataElementCount;
	protected List outputList;
	
	public MedPCCumulativeRecorder() {
		outputList = new ArrayList(10000);
	}
	
	private int getDatumType(ChamberEvent ce) {
		if (ce instanceof ResponseEvent) {
			return 1;
		} else if (ce instanceof ReinforcerEvent) {
			return 2;
		} else if (ce instanceof CompositeTransitionEvent) {
			CompositeTransitionEvent cte = (CompositeTransitionEvent) ce;
			if (cte.getActiveComposite() != null) {
				if (cte.getActiveComposite().getType() == Composite.ACTIVE_COMPOSITE) {
					return 6;
				} else {
					return 5;
				}
			}
		}	
		return 0;
	}
	
	protected abstract void generateEventEntries(ChamberEvent ce);
		
	protected void processChamberEvent(ChamberEvent ce) {
		
		if (ce instanceof DestroyEvent) {
			try {
				destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			
			generateEventEntries(ce);
			
		}		

	}
	
	protected void addEventEntry(long eventTime, int datumType, int datumIndex) {

		StringBuffer dataElement = new StringBuffer(14);
	
		long time = (eventTime - startTime) / 10;
	
		dataElement.append(time);
		dataElement.append(".");
		dataElement.append(datumType);
		dataElement.append(datumIndex);
		dataElement.append("0");
		dataElement.append(NEWLINE);
		
		try {
			dataElementCount++;
			outputList.add(dataElement.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	protected abstract String getOutputFileName();
	
	protected void destroyChild() {
		
		try {
			
			Calendar calStart = Calendar.getInstance();
			Calendar calStop = Calendar.getInstance();
			
			calStart.setTimeInMillis(startTime);
			calStop.setTimeInMillis(stopTime);
			
			File outputFile = new File(Application.getProperty("log_path") + "/" + getOutputFileName());
			FileOutputStream fos = new FileOutputStream(outputFile);
			FileChannel out = fos.getChannel();

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			bw.write(NEWLINE);
			bw.write(NEWLINE);
			
			bw.write((calStart.get(Calendar.MONTH) + 1) + NEWLINE);
			bw.write((calStart.get(Calendar.DATE)) + NEWLINE);
			bw.write((calStart.get(Calendar.YEAR)) + NEWLINE);
			
			bw.write((calStop.get(Calendar.MONTH) + 1) + NEWLINE);
			bw.write((calStop.get(Calendar.DATE)) + NEWLINE);
			bw.write((calStop.get(Calendar.YEAR)) + NEWLINE);

			bw.write(0 + NEWLINE); // subject number
			bw.write(0 + NEWLINE); // experiment number
			bw.write(0 + NEWLINE); // group number
			bw.write(0 + NEWLINE); // record number
			
			bw.write((calStart.get(Calendar.MONTH) + 1) + NEWLINE);
			bw.write((calStart.get(Calendar.DATE)) + NEWLINE);
			bw.write((calStart.get(Calendar.YEAR)) + NEWLINE);
			
			bw.write((calStop.get(Calendar.MONTH) + 1) + NEWLINE);
			bw.write((calStop.get(Calendar.DATE)) + NEWLINE);
			bw.write((calStop.get(Calendar.YEAR)) + NEWLINE);

			bw.write(0 + NEWLINE);
			bw.write(1 + NEWLINE);
			bw.write(dataElementCount + NEWLINE);
			
			bw.flush();
			
			Iterator it = outputList.iterator();
			while (it.hasNext()) {
				bw.write((String) it.next());
			}
			
			try { bw.close(); } catch (Exception e) { }
			try { out.close(); } catch (Exception e) { }
			try { fos.close(); } catch (Exception e) { }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public int getExperimentNumber() {
		return experimentNumber;
	}

	public void setExperimentNumber(int experimentNumber) {
		this.experimentNumber = experimentNumber;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public int getRecordNumber() {
		return recordNumber;
	}

	public void setRecordNumber(int recordNumber) {
		this.recordNumber = recordNumber;
	}

	public int getSubjectNumber() {
		return subjectNumber;
	}

	public void setSubjectNumber(int subjectNumber) {
		this.subjectNumber = subjectNumber;
	}

}