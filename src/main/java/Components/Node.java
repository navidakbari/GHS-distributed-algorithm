package Components;
import Events.ChangeRootMessage;
import Events.InitMessage;
import Events.ReportMessage;
import Events.JoinMessage;
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
    public String childrenName = null;
    public String nodeName = null;
    public String parentName = null;
    public int dist = 10000;
    public HashMap<String, Integer> neighbours = new HashMap<>();
    public ArrayList<TableRow> route_table = new ArrayList<>();

    public Handler joinHandler = new Handler<JoinMessage>(){
        @Override
        public void handle(JoinMessage event) {
            if (nodeName.equalsIgnoreCase(event.dst)) {
                if(Collections.min(neighbours.values()) == event.edge_weight){

                    System.out.println("In joinHandler -> " + nodeName + " received message : src " + event.src + " dst " + event.dst + " Level: " + level);

                    if(event.dst.compareTo(event.src) > 0){
                        coordinator = event.dst;
                        childrenName = event.src;
                        fragmentName = event.dst;
                        level++;
                        trigger(new ChangeRootMessage(nodeName, childrenName, coordinator, level, fragmentName), sendPort);
                        if(parentName != null){
                            trigger(new ChangeRootMessage(nodeName, parentName, coordinator, level, fragmentName), sendPort);
                        }
                    }

                }
            }
        }
    };

    public Handler changeRootHandler = new Handler<ChangeRootMessage>() {
        @Override
        public void handle(ChangeRootMessage event) {
            if (nodeName.equalsIgnoreCase(event.dst)) {
                System.out.println("In changeRootHandler -> " + nodeName + " received message : src " + event.src + " dst " + event.dst + " Level: " + event.level);
                parentName = event.src;
                level = event.level;
                coordinator = event.coordinator;
                fragmentName = event.fragmentName;
                System.out.println("In changeRootHandler -> " + nodeName + " has: parentName " + parentName + " level " + level + " coordinator " + coordinator + " fragmentName " + fragmentName);
                if(childrenName != null){
                    trigger(new ChangeRootMessage(nodeName, childrenName, coordinator, level, fragmentName), sendPort);

                }
            }
        }
    };

    public Handler reportHandler = new Handler<ReportMessage>() {
        @Override
        public void handle(ReportMessage event) {
            if (nodeName.equalsIgnoreCase(event.dst))
            {
                ArrayList<TableRow> newRoute = new ArrayList<>();
                newRoute.add(new TableRow(event.src,event.src, event.dist));
                for(TableRow tr:event.route_table){
                    tr.first_node = event.src;
                    newRoute.add(tr);
                }
                for(TableRow tr:route_table){
                    boolean remove = false;
                    for(TableRow t:newRoute){
                        if(tr.dst.equals(t.dst)){
                            remove = true;
                        }
                    }
                    if(!remove){
                        newRoute.add(tr);
                    }
                }
                route_table = newRoute;
                if (parentName!=null)
                    trigger(new ReportMessage(nodeName,parentName,dist ,route_table),sendPort);
                Path path = Paths.get("src/main/java/Routes/table" + nodeName + ".txt");
                OpenOption[] options = new OpenOption[] { WRITE , CREATE};
                try {
                    Files.write(path,route_table.toString().getBytes(),options);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public Handler startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {

            int potentially_lwoe = Collections.min(neighbours.values());

            for(Map.Entry<String, Integer> entry: neighbours.entrySet()){
                if(entry.getValue().equals(potentially_lwoe)){
                    trigger(new JoinMessage(nodeName, entry.getKey() , entry.getValue()), sendPort);
                    break;
                }
            }

        }
    };

    public Node(InitMessage initMessage) {
        nodeName = initMessage.nodeName;
        System.out.println("initNode: " + initMessage.nodeName);
        this.fragmentName = initMessage.nodeName;
        this.coordinator = initMessage.nodeName;
        this.neighbours = initMessage.neighbours;
        subscribe(startHandler, control);
//        subscribe(reportHandler,recievePort);
        subscribe(joinHandler,recievePort);
        subscribe(changeRootHandler, recievePort);
    }
}