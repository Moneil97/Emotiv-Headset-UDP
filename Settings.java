package oneil;

//! Set headset setting for EPOC+ headset
/*!
 *  \param userId       - user ID
 *  \param EPOCmode     - If 0, then EPOC mode is EPOC.
 *                      - If 1, then EPOC mode is EPOC+.
 *  \param eegRate      - If 0, then EEG sample rate is 128Hz.
 *                      - If 1, then EEG sample rate is 256Hz.
 *  \param eegRes       - If 0, then EEG Resolution is 14bit.
 *                      - If 1, then EEG Resolution is 16bit.
 *  \param memsRate     - If 0, then MEMS sample rate is OFF.
 *                      - If 1, then MEMS sample rate is 32Hz.
 *                      - If 2, then MEMS sample rate is 64Hz.
 *                      - If 3, then MEMS sample rate is 128Hz.
 *  \param memsRes      - If 0, then MEMS Resolution is 12bit.
 *                      - If 1, then MEMS Resolution is 14bit.
 *                      - If 2, then MEMS Resolution is 16bit.
 *  \return EDK_ERROR_CODE 
 *                      - EDK_ERROR_CODE = EDK_OK if the command successful
*/

public enum Settings {
	
	EPOC(0), EPOC_PLUS(1),
	EEG_128Hz(0), EEG_256Hz(1),
	EEG_14Bit(0), EEG_16Bit(1),
	MEMS_OFF(0), MEMS_32Hz(1), MEMS_64Hz(2), MEMS_128Hz(3),
	MEMS_12Bit(0), MEMS_14Bit(1), MEMS_16Bit(2);
	
	int val;
	
	Settings(int val) {
		this.val = val;
	}
}
