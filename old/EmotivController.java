package oneil;

import java.util.ArrayList;
import java.util.List;

import com.emotiv.Iedk.Edk;
import com.emotiv.Iedk.EdkErrorCode;
import com.emotiv.Iedk.IEegData;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class EmotivController {
	
	Pointer eEvent = Edk.INSTANCE.IEE_EmoEngineEventCreate();
	Pointer eState = Edk.INSTANCE.IEE_EmoStateCreate();
	IntByReference nSamplesTaken = new IntByReference(0);
	IntByReference userID = new IntByReference(0);
	int state = 0;
	boolean readytocollect = false;
	Pointer hData;
	float secs = 1f;

	public EmotivController() {

		if (Edk.INSTANCE.IEE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
			System.out.println("Emotiv Engine start up failed.");
			return;
		}
		System.out.println("Emotiv Engine started up.");
		hData = IEegData.INSTANCE.IEE_DataCreate();
		IEegData.INSTANCE.IEE_DataSetBufferSizeInSec(secs);
		System.out.println("Buffer size in secs: " + secs);
		System.out.println("Start receiving EEG Data!");
	}
	
	public List<double[]> getAvailableSamples() {
		state = Edk.INSTANCE.IEE_EngineGetNextEvent(eEvent);
	
		// New event needs to be handled
		if (state == EdkErrorCode.EDK_OK.ToInt()) {
			int eventType = Edk.INSTANCE.IEE_EmoEngineEventGetType(eEvent);
			Edk.INSTANCE.IEE_EmoEngineEventGetUserId(eEvent, userID);
	
			if (eventType == Edk.IEE_Event_t.IEE_UserAdded.ToInt())
				if (userID != null) {
					System.out.println("User " + userID.getValue() + " added");
					IEegData.INSTANCE.IEE_DataAcquisitionEnable(userID.getValue(), true);
					readytocollect = true;
				}
			return null;
		} else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
			System.out.println("Internal error in Emotiv Engine!");
			System.exit(1);
			return null;
		}
	
		if (readytocollect) {
			
			List<double[]> samples = new ArrayList<double[]>();
			IEegData.INSTANCE.IEE_DataUpdateHandle(userID.getValue(), hData);
			IEegData.INSTANCE.IEE_DataGetNumberOfSample(hData, nSamplesTaken);
	
			if (nSamplesTaken != null && nSamplesTaken.getValue() != 0) {
	
				double[] data = new double[nSamplesTaken.getValue()];
				for (int sampleIdx = 0; sampleIdx < nSamplesTaken.getValue(); ++sampleIdx) {
					samples.add(new double[20]);
				}
				for (int i = 0; i < 20; i++) {
					IEegData.INSTANCE.IEE_DataGet(hData, i, data, nSamplesTaken.getValue());
					
					for (int sampleIdx = 0; sampleIdx < nSamplesTaken.getValue(); ++sampleIdx) {
						samples.get(sampleIdx)[i] = data[sampleIdx];
					}
				}
				
				return samples;
			}
		}
		return null;
	}
	
	//Prints Headset settings to system.out
	IntByReference epocModeRef = new IntByReference(0), eegRateRef = new IntByReference(0), eegResRef = new IntByReference(0),
			       memsRateRef = new IntByReference(0), memsResRef = new IntByReference(0);
	public String getHeadsetSettings(){
		if (userID.getValue() == 0) 
			return "no headset connected";
		else {
			Edk.INSTANCE.IEE_GetHeadsetSettings(userID.getValue(), epocModeRef, eegRateRef, eegResRef, memsRateRef, memsResRef);
			
			String out = "ID: " + userID.getValue() + ",";
			out += "\nMode: " + (epocModeRef.getValue() == 0 ? "EPOC" : "EPOC+");
			out += "\nEEG Rate: " + (eegRateRef.getValue() == 0 ? "128Hz" : "265Hz");
			out += "\nEEG Res: " + (eegResRef.getValue() == 0 ? "14bit" : "16bit");
			out += "\nMEMS Rate: ";
			switch(memsRateRef.getValue()) {
				case 0:
					out+= "OFF";
					break;
				case 1:
					out+="32Hz";
					break;
				case 2:
					out+="64Hz";
					break;
				case 3:
					out+="128Hz";
					break;
			}
			out += "\nMEMS Res: ";
			switch(memsResRef.getValue()) {
				case 0:
					out+= "12bit";
					break;
				case 1:
					out+="14bit";
					break;
				case 2:
					out+="16bit";
					break;
			}
			return out;
		}
	}
	
	//Headset must be plugged in via USB
	public void changeSettings(Settings EPOC_MODE, Settings EEG_RATE, Settings EEG_RES, Settings MEMS_RATE, Settings MEMS_RES) {
		
		if (userID.getValue() == 0) {
			state = Edk.INSTANCE.IEE_EngineGetNextEvent(eEvent);
			if (state == EdkErrorCode.EDK_OK.ToInt()) {
				Edk.INSTANCE.IEE_EmoEngineEventGetUserId(eEvent, userID);
			}
		}
		
		Edk.INSTANCE.IEE_SetHeadsetSettings(24576, EPOC_MODE.val, EEG_RATE.val, EEG_RES.val, MEMS_RATE.val, MEMS_RES.val);
	}
	
	public void disconnect() {
		Edk.INSTANCE.IEE_EngineDisconnect();
		Edk.INSTANCE.IEE_EmoStateFree(eState);
		Edk.INSTANCE.IEE_EmoEngineEventFree(eEvent);
	}
	
	public static void main(String[] args) {
		System.loadLibrary("edk");
		EmotivController ec = new EmotivController();
		
		//int last = 0;
		
		//while (true) {
		for (int a = 0; a < 10000; a++) {
			List<double[]> samples = ec.getAvailableSamples();
			if (samples != null) {
				for (int i = 0; i < samples.size(); i++) {
					for (int j=0; j < 20; j++) {
						System.out.print(samples.get(i)[j] + " ");
					}
					System.out.println();
					
//					if (last != (int)samples.get(i)[0]-1 && !(last == 255 && (int)samples.get(i)[0] == 0))
//						System.out.println("skipped from " + last + " to " + samples.get(i)[0]);
//					
//					last = (int) samples.get(i)[0];
				}
			}
		}
		
		System.out.println(ec.getHeadsetSettings());
		ec.changeSettings(Settings.EPOC_PLUS, Settings.EEG_256Hz, Settings.EEG_16Bit, Settings.MEMS_64Hz, Settings.MEMS_16Bit);
		System.out.println(ec.getHeadsetSettings());
		
		ec.disconnect();
	}

}
