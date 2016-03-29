package eantgio.reservation;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import eantgio.lift_plant.lift.Lift;
import eantgio.reservation.request.floor.FloorPad;

public class Booking extends UntypedActor {
	List<ActorRef> elevators = new ArrayList<>();

	private int lifts;
	private int floors;

	public static Props props(int lifts, int floors) {
		return Props.create(Booking.class, lifts, floors);
	}

	public Booking(int lifts, int floors) {
		this.lifts = lifts;
		this.floors = floors;
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
		ActorRef elevator1 = getContext().actorOf(Props.create(Lift.class),"Elevator1");
		elevators.add(elevator1);

		for (int i = 0; i < floors; i++) {
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
