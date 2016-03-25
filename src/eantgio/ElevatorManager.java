package eantgio;
import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ElevatorManager extends UntypedActor{

	List<ActorRef> elevators = new ArrayList<>();

	final static int FLOOR_N = 5;
	
	@Override
	public void preStart() throws Exception {
		super.preStart();
		ActorRef elevator1 = getContext().actorOf(Props.create(Elevator.class),"Elevator1");
		elevators.add(elevator1);
		
		for (int i = 0; i < FLOOR_N; i++) {
			getContext().actorOf(FloorPad.props(getSelf(), i),"FloorPad-"+i);
		}
		
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof FloorPad.Request)
		{
			System.out.println("Received request from floor : "+((FloorPad.Request)msg).fromFloor);
			elevators.get(0).tell(msg, getSelf()); //NON LOGIC FOR NOW
		}
	}
	

}
