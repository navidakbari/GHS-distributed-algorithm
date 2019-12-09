package Ports;

import Events.ChangeRootMessage;
import Events.InitiateMessage;
import Events.ReportMessage;
import Events.JoinMessage;
import se.sics.kompics.PortType;

public class EdgePort extends PortType {{
    positive(JoinMessage.class);
    positive(ReportMessage.class);
    positive(ChangeRootMessage.class);
    positive(InitiateMessage.class);


    negative(JoinMessage.class);
    negative(ReportMessage.class);
    negative(ChangeRootMessage.class);
    negative(InitiateMessage.class);


}}
