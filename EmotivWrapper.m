classdef EmotivWrapper <handle
    %EMOTIVWRAPPER Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        DataChannels, DataChannelsNames, DataChannelsNamesfull, eEvent, hData, readytocollect, acqtime, mycolumn, userID_value,
    end
    
    methods
        function obj = EmotivWrapper()
            %EMOTIVWRAPPER Construct an instance of this class
            %   Detailed explanation goes here
            %w = warning ('off','all');
            loadlibrary('../../bin/win64/edk.dll','Iedk.h','addheader','IedkErrorCode.h','addheader','IEmoStateDLL.h','addheader','FacialExpressionDetection.h','addheader','MentalCommandDetection.h','addheader','IEmotivProfile.h','addheader','EmotivLicense.h','alias','libIEDK');
            loadlibrary('../../bin/win64/edk.dll','IEegData.h','addheader','Iedk.h','alias','libIEEGDATA');

            enuminfo.IEE_DataChannels_enum = struct('IED_COUNTER', 0, 'IED_INTERPOLATED', 1, 'IED_RAW_CQ', 2,'IED_AF3', 3, 'IED_F7',4, 'IED_F3', 5, 'IED_FC5', 6, 'IED_T7', 7,'IED_P7', 8, 'IED_Pz', 9,'IED_O2', 10, 'IED_P8', 11, 'IED_T8', 12, 'IED_FC6', 13, 'IED_F4', 14, 'IED_F8', 15, 'IED_AF4', 16, 'IED_GYROX', 17,'IED_GYROY', 18, 'IED_TIMESTAMP', 19,'IED_MARKER_HARDWARE', 20, 'IED_ES_TIMESTAMP',21, 'IED_FUNC_ID', 22, 'IED_FUNC_VALUE', 23, 'IED_MARKER', 24,'IED_SYNC_SIGNAL', 25);
            enuminfo.IEE_MentalCommandTrainingControl_enum = struct('MC_NONE',0,'MC_START',1,'MC_ACCEPT',2,'MC_REJECT',3,'MC_ERASE',4,'MC_RESET',5);

            obj.DataChannels = enuminfo.IEE_DataChannels_enum;
            obj.DataChannelsNames = {'IED_COUNTER','IED_INTERPOLATED','IED_AF3','IED_T7','IED_Pz','IED_T8','IED_AF4','IED_GYROX','IED_GYROY','IED_TIMESTAMP','IED_ES_TIMESTAMP'};
            obj.DataChannelsNamesfull ={'IED_COUNTER','IED_INTERPOLATED','IED_RAW_CQ','IED_AF3','IED_F7','IED_F3','IED_FC5','IED_T7','IED_P7','IED_Pz','IED_O2','IED_P8','IED_T8','IED_FC6','IED_F4','IED_F8','IED_AF4','IED_GYROX','IED_GYROY','IED_TIMESTAMP','IED_MARKER_HARDWARE','IED_ES_TIMESTAMP','IED_FUNC_ID','IED_FUNC_VALUE','IED_MARKER','IED_SYNC_SIGNAL'};
            
            calllib('libIEDK','IEE_EngineConnect', 'Emotiv Systems-5');
            
            obj.eEvent = calllib('libIEDK','IEE_EmoEngineEventCreate');
            bufferSizeInSec = 1;
            obj.hData = calllib('libIEEGDATA','IEE_DataCreate');
            calllib('libIEEGDATA','IEE_DataSetBufferSizeInSec', bufferSizeInSec)
            obj.readytocollect = false;
            obj.mycolumn = numel(obj.DataChannelsNamesfull);
            tic;
        end
        
        function out = waitUntilNextEvent(obj)
            while (true)
                [set,o] = obj.nextEvent();
                if (set == true)
                    out = o;
                    return;
                end
            end
        end
        
        function [set, out] = nextEvent(obj)
            %METHOD1 Summary of this method goes here
            %   Detailed explanation goes here
            set = false;
            out = "null";
            state = calllib('libIEDK','IEE_EngineGetNextEvent', obj.eEvent); % state = 0 if everything's OK
    
            if(state == 0) %0 == OK
                eventType = calllib('libIEDK','IEE_EmoEngineEventGetType',obj.eEvent);
                userID=libpointer('uint32Ptr',0);
                calllib('libIEDK','IEE_EmoEngineEventGetUserId',obj.eEvent, userID);

                if (strcmp(eventType,'IEE_UserAdded') == true)
                    obj.userID_value = get(userID,'value');
                    calllib('libIEEGDATA','IEE_DataAcquisitionEnable',obj.userID_value,true);
                    disp("user added");
                    obj.readytocollect = true;
                end
            end
            
            if(obj.readytocollect)
                result = calllib('libIEEGDATA','IEE_DataUpdateHandle', obj.userID_value, obj.hData);

                if(result~=0)
                    return;
                end
                
                nSamples = libpointer('uint32Ptr',0);
                calllib('libIEEGDATA','IEE_DataGetNumberOfSample', obj.hData, nSamples);
                nSamplesTaken = get(nSamples,'value');


                if (nSamplesTaken ~= 0)
                    disp("got " + nSamplesTaken + " samples");
                    %totalSamples = totalSamples + nSamplesTaken;
                    data = libpointer('doublePtr', zeros(1, nSamplesTaken));
                    data2=zeros(nSamplesTaken,numel(obj.DataChannelsNames));

                    for i = 1:obj.mycolumn
                        calllib('libIEEGDATA', 'IEE_DataGet', obj.hData, obj.DataChannels.([obj.DataChannelsNamesfull{i}]), data, uint32(nSamplesTaken));
                        data_value = get(data,'value');
                        for k = 1: nSamplesTaken
                            data2(k,i) = data_value(k);
                        end
                        %disp(data_value);
                    end

                    %disp(data2);
                    set = true;
                    out = data2;

                end

            end
            
            
        end
    end
end

