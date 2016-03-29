package eantgio.reservation;

import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import eantgio.lift_plant.LiftPlant;
import eantgio.reservation.request.lift.LiftPad;

public class Reservation extends UntypedActor{

	public static Props create(ActorRef liftPlant) {
		return Props.create(Reservation.class, liftPlant);
	}
	
	public static class Schedulers{
		
		public final List<ActorRef> schedulers;

		private static Schedulers of (final List<ActorRef> schedulers)
		{
			return new Schedulers(schedulers);
		}

		private Schedulers (final List<ActorRef> schedulers) {
			this.schedulers = schedulers;
		}
	}


	private ActorRef liftPlant;
	
	public Reservation(ActorRef liftPlant) {
		this.liftPlant = liftPlant;
	}
	
	@Override
	public void preStart() throws Exception {
		super.preStart();
		liftPlant.tell(LiftPlant.GET_SCHEDULERS, getSelf());
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof Schedulers)
		{
			Schedulers s = (Schedulers) msg;
			getContext().actorOf(Props.create(Booking.class));
			
		
			s.schedulers.forEach(e -> getContext().actorOf(LiftPad.props(e)));
		}
		
	}

}
