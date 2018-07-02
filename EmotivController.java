package oneil;

import java.util.ArrayList;
import java.util.List;

import com.emotiv.Iedk.Edk;
import com.emotiv.Iedk.EdkErrorCode;
import com.emotiv.Iedk.EmotivCloudClient;
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
	int totalSamples = 0;
	
	public EmotivController(){
		this("", "", "");
	}

	public EmotivController(String username, String password, String licenseKey) {
		short composerPort = 1726;
		int option = 1;
		float secs = 1f;
		
		switch (option) {
		case 1: {
			if (Edk.INSTANCE.IEE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
				System.out.println("Emotiv Engine start up failed.");
				return;
			}
			else {
				System.out.println("Emotiv Engine started up.");
				if(EmotivCloudClient.INSTANCE.EC_Connect() != EdkErrorCode.EDK_OK.ToInt())
		        {
		            System.out.println("Cannot connect to Emotiv Cloud");
		            return;
		        }
		        if(EmotivCloudClient.INSTANCE.EC_Login(username, password) != EdkErrorCode.EDK_OK.ToInt())
		        {            
		            System.out.println("Your login attempt has failed. The username or password may be incorrect");
		            return;
		        }
		        if(Edk.INSTANCE.IEE_AuthorizeLicense(licenseKey, 1) != EdkErrorCode.EDK_OK.ToInt())
		        {            
		            System.out.println("authorization failed");
		            return;
		        }
			}
			break;
		}
		case 2: {
			System.out.println("Target IP of EmoComposer: [127.0.0.1] ");

			if (Edk.INSTANCE.IEE_EngineRemoteConnect("127.0.0.1", composerPort,
					"Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
				System.out
						.println("Cannot connect to EmoComposer on [127.0.0.1]");
				return;
			}
			System.out.println("Connected to EmoComposer on [127.0.0.1]");
			break;
		}
		default:
			System.out.println("Invalid option...");
			return;
		}
		
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
					System.out.print("Updated: ");
					System.out.println(nSamplesTaken.getValue());
					totalSamples += nSamplesTaken.getValue();

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
	
	
	public static void main(String[] args) {
		EmotivController ec = new EmotivController();
		
		while (true) {
			List<double[]> samples = ec.getAvailableSamples();
			if (samples != null) {
				for (int i = 0; i < samples.size(); i++) {
					for (int j=0; j < 20; j++) {
						System.out.print(samples.get(i)[j] + " ");
					}
					System.out.println();
				}
			}
		}
		
	}

}
