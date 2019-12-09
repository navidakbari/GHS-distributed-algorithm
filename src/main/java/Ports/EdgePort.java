package Ports;

import Events.ChangeRootMessage;
import Events.InitiateMessage;
import Events.JoinMessage;
import Events.TestMessage;
import se.sics.kompics.PortType;

public class EdgePort extends PortType {{
    positive(JoinMessage.class);
    positive(ChangeRootMessage.class);
    positive(InitiateMessage.class);
    positive(TestMessage.class);

    negative(JoinMessage.class);
    negative(ChangeRootMessage.class);
    negative(InitiateMessage.class);
    negative(TestMessage.class);


}}
