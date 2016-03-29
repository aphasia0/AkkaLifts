package eantgio.lift_plant;

import java.util.ArrayList;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import eantgio.lift_plant.lift.Lift;
import eantgio.lift_plant.sheduler.Scheduler;
import eantgio.reservation.Reservation;

public class LiftPlant extends UntypedActor{

	public static class GetSchedulers {};
	public static GetSchedulers GET_SCHEDULERS = new GetSchedulers();
	
	public static Props props(int lifts, int floors) {
		return Props.create(LiftPlant.class,lifts,floors);
	}
	
	int floors;
	int lifts;
	public ArrayList<ActorRef> schedulers = new ArrayList<>();
	
	public LiftPlant(int lifts, int floors) {
		this.lifts = lifts;
		this.floors = floors;
	}
	
	@Override
	public void preStart() throws Exception {
		super.preStart();
		for (int i = 0; i < lifts; i++) { //Create the scheduler and the linked lift
			ActorRef lift = getContext().actorOf(Props.create(Lift.class));
			schedulers.add(getContext().actorOf(Scheduler.props(lift)));
		}

	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof GetSchedulers)
		{
			getSender().tell(Reservation.Schedulers.of(schedulers), getSelf());
		}
		
	}

}
