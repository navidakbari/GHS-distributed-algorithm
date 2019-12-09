package Components;
import Events.*;
import Ports.EdgePort;
import misc.TableRow;
import se.sics.kompics.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class Node extends ComponentDefinition {
    public Positive<EdgePort> recievePort = positive(EdgePort.class);
    public Negative<EdgePort> sendPort = negative(EdgePort.class);

    public String coordinator;
    public String fragmentName;
    public int level = 0;
    public ArrayList<String> childrensName = new ArrayList<>();
    public String nodeName = null;
    public String parentName = null;
    public HashMap<String, Integer> neighbours = new HashMap<>();
    public HashMap<String, Integer> basicEdge = new HashMap<>();
    public HashMap<String, Integer> branchEdge = new HashMap<>();
    public HashMap<String, Integer> rejectedEdge = new HashMap<>();


    public Handler joinHandler = new Handler<JoinMessage>(){
        @Override
        public void handle(JoinMessage event) {
            if (nodeName.equalsIgnoreCase(event.dst)) {
                if(Collections.min(neighbours.values()) == event.edge_weight){

                    System.out.println("In joinHandler -> " + nodeName +
                            " received message : src " + event.src +
                            " dst " + event.dst +
                            " Level " + event.level +
                            " Coordinator " + event.coordinator +
                            " fragmentName " + event.fragmentName);

                    if(event.dst.compareTo(event.src) > 0){

                        coordinator = event.dst;
                        childrensName.add(event.src);
                        fragmentName = event.dst;
                        level++;
                        basicEdge.remove(event.src);


                        for(String childrenName : childrensName){
                            trigger(new ChangeRootMessage(nodeName, childrenName, coordinator, level, fragmentName), sendPort);
                        }

                        for(String childrenName : childrensName){
                            trigger(new InitiateMessage(nodeName, childrenName, coordinator, level, fragmentName), sendPort);
                        }

                        if(parentName != null){
                            trigger(new ChangeRootMessage(nodeName, parentName, coordinator, level, fragmentName), sendPort);
                        }
                    }

                    if( level > event.level){

                    }

                }
            }
        }
    };

    public Handler changeRootHandler = new Handler<ChangeRootMessage>() {
        @Override
        public void handle(ChangeRootMessage event) {
            if (nodeName.equalsIgnoreCase(event.dst)) {
                System.out.println("In changeRootHandler -> " + nodeName +
                        " received message : src " + event.src +
                        " dst " + event.dst +
                        " Level " + event.level +
                        " Coordinator " + event.coordinator +
                        " fragmentName " + event.fragmentName);

                parentName = event.src;
                level = event.level;
                coordinator = event.coordinator;
                fragmentName = event.fragmentName;
                basicEdge.remove(event.src);
                branchEdge.put(event.src, neighbours.get(event.src));

                for(String childrenName : childrensName){
                    trigger(new ChangeRootMessage(nodeName, childrenName, coordinator, level, fragmentName), sendPort);
                }
            }
        }
    };

    public Handler initiateHandler = new Handler<InitiateMessage>() {
        @Override
        public void handle(InitiateMessage event) {
            if (nodeName.equalsIgnoreCase(event.dst)) {
                System.out.println("In InitiateHandler -> " + nodeName +
                        " received message : src " + event.src +
                        " dst " + event.dst +
                        " Level " + event.level +
                        " Coordinator " + event.coordinator +
                        " fragmentName " + event.fragmentName);

                for(String childrenName : childrensName){
                    trigger(new ChangeRootMessage(nodeName, childrenName, coordinator, level, fragmentName), sendPort);
                }

                int potentially_lwoe = Collections.min(basicEdge.values());

                for(Map.Entry<String, Integer> entry: basicEdge.entrySet()){
                    if(entry.getValue().equals(potentially_lwoe)){
                        trigger(new TestMessage(nodeName, entry.getKey() , entry.getValue(), coordinator, level, fragmentName), sendPort);
                        break;
                    }
                }
//                trigger(new JoinMessage(nodeName, parentName , potentially_lwoe, coordinator, level, fragmentName), sendPort);

            }
        }
    };

    public Handler testHandler = new Handler<TestMessage>() {
        @Override
        public void handle(TestMessage event){
            if (nodeName.equalsIgnoreCase(event.dst)) {
                System.out.println("In TestHandler -> " + nodeName +
                        " received message : src " + event.src +
                        " dst " + event.dst +
                        " Level " + event.level +
                        " Coordinator " + event.coordinator +
                        " fragmentName " + event.fragmentName);
                if(event.fragmentName == fragmentName){

                }else if(event.level < level){

                }else{

                }

//                trigger(new JoinMessage(nodeName, parentName , potentially_lwoe, coordinator, level, fragmentName), sendPort);

            }
        }
    };

    public Handler startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {

            int potentially_lwoe = Collections.min(neighbours.values());

            for(Map.Entry<String, Integer> entry: neighbours.entrySet()){
                if(entry.getValue().equals(potentially_lwoe)){
                    trigger(new JoinMessage(nodeName, entry.getKey() , entry.getValue(), coordinator, level, fragmentName), sendPort);
                    break;
                }
            }

        }
    };

    public Node(InitMessage initMessage) {

        System.out.println("initNode: " + initMessage.nodeName);

        this.nodeName = initMessage.nodeName;
        this.fragmentName = initMessage.nodeName;
        this.coordinator = initMessage.nodeName;
        this.neighbours = initMessage.neighbours;
        this.basicEdge.putAll(neighbours);
        this.parentName = null;

        subscribe(startHandler, control);
        subscribe(joinHandler,recievePort);
        subscribe(changeRootHandler, recievePort);
        subscribe(initiateHandler, recievePort);
        subscribe(testHandler, recievePort);
    }
}