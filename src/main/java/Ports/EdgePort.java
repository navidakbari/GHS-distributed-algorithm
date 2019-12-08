package Ports;

import Events.ChangeRootMessage;
import Events.ReportMessage;
import Events.JoinMessage;
import se.sics.kompics.PortType;

public class EdgePort extends PortType {{
    positive(JoinMessage.class);
    positive(ReportMessage.class);
    positive(ChangeRootMessage.class);


    negative(JoinMessage.class);
    negative(ReportMessage.class);
    negative(ChangeRootMessage.class);

}}
