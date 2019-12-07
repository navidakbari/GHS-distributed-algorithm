package Components;
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
    Positive<EdgePort> recievePort = positive(EdgePort.class);
    Negative<EdgePort> sendPort = negative(EdgePort.class);


    public int coordinator;
    public int name;
    public int level = 0;
    public String children;
    public String nodeName;
    public String parentName;
    public int dist = 10000;
    public HashMap<String, Integer> neighbours = new HashMap<>();
    public ArrayList<TableRow> route_table = new ArrayList<>();

    Handler joinHandler = new Handler<JoinMessage>(){
        @Override
        public void handle(JoinMessage event) {
            if (nodeName.equalsIgnoreCase(event.dst)) {
                if(Collections.min(neighbours.values()) == event.edge_weight){
                    level++;
                    System.out.println(nodeName + " recieved message : src " + event.src + " dst " + event.dst + " Level: " + level);
                    if(event.dst.compareTo(event.src) > 0){

                    }
//                trigger(new ReportMessage(nodeName, parentName, dist, route_table), sendPort);

                    System.out.println(String.format("node %s parent is: %s", nodeName, parentName));
                    for (Map.Entry<String, Integer> entry : neighbours.entrySet()) {
                        if (!entry.getKey().equalsIgnoreCase(parentName)) {
    //                        trigger(new RoutingMessage(nodeName,entry.getKey() ,dist + entry.getValue(),entry.getValue()),sendPort);
                        }
                    }
                }
            }
        }
    };

    Handler reportHandler = new Handler<ReportMessage>() {
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

    Handler startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {

            System.out.println("hello" + nodeName);
            int potentially_lwoe = Collections.min(neighbours.values());
            System.out.println(potentially_lwoe);
            for(Map.Entry<String, Integer> entry: neighbours.entrySet()){
                if(entry.getValue().equals(potentially_lwoe)){
                    System.out.println(entry.getKey());
                    trigger(new JoinMessage(nodeName, entry.getKey() , entry.getValue()), sendPort);
                    break;
                }
            }

        }
    };

    public Node(InitMessage initMessage) {
        nodeName = initMessage.nodeName;
        System.out.println("initNode :" + initMessage.nodeName);
        this.neighbours = initMessage.neighbours;
        subscribe(startHandler, control);
//        subscribe(reportHandler,recievePort);
        subscribe(joinHandler,recievePort);
    }
}