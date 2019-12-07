package Ports;

import Events.ReportMessage;
import Events.JoinMessage;
import se.sics.kompics.PortType;

public class EdgePort extends PortType {{
    positive(JoinMessage.class);
    positive(ReportMessage.class);

    negative(JoinMessage.class);
    negative(ReportMessage.class);
}}
