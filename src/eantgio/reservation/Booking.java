package eantgio.reservation;

import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import eantgio.reservation.request.floor.FloorPad;

public class Booking extends UntypedActor {

	private int floors;


	private List<ActorRef> schedulers;

	public static Props props(List<ActorRef> schedulers, int floors) {
		return Props.create(Booking.class, schedulers, floors);
	}

	public Booking(List<ActorRef> schedulers, int floors) {
		this.floors = floors;
		this.schedulers = schedulers;
	}


	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof FloorPad.Request)
		{
			System.out.println("Received request from floor : "+((FloorPad.Request)msg).fromFloor);
			schedulers.get(0).tell(msg, getSelf()); //NON LOGIC FOR NOW
		}
	}
}
