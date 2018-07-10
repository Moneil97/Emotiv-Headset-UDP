classdef EEGimporterUDP < matlab.System
    % Untitled Add summary here
    %
    % This template includes the minimum set of functions required
    % to define a System object with discrete state.
    
    % Public, tunable properties
    properties
        
    end
    
    properties (Hidden)%(DiscreteState)
        
    end
    
    % Pre-computed constants
    properties(Access = private)
        
    end
    
    methods(Access = protected)
        %         function setupImpl(obj)
        %             % Perform one-time calculations, such as computing constants
        %             obj.hudpr = dsp.UDPReceiver('LocalIPPort',9091,'RemoteIPAddress','127.0.0.1','ReceiveBufferSize',1024);
        %         end
        
        function y = stepImpl(obj, u)
            % Implement algorithm. Calculate y as a function of input u and
            % discrete states.
            coder.extrinsic('typecast');
            coder.extrinsic('swapbytes');
            coder.extrinsic('evalin');
            coder.extrinsic('release');
            hudpr = evalin('base', 'hudpr');
            dataReceived = step(hudpr);
            y = zeros(20,1);
            if ~isempty(dataReceived)
                y = swapbytes(typecast((dataReceived), 'double'));
            end
%             release(hudpr)
        end
        
        function resetImpl(obj)
            % Initialize / reset discrete-state properties
        end
    end
end
