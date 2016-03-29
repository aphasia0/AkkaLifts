package eantgio.reservation.request.lift;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;


public class LiftPad extends UntypedActor{




	public static Props props(ActorRef scheduler) {
		return Props.create(LiftPad.class,scheduler);
	}

	private ActorRef scheduler;
	
	public LiftPad(ActorRef scheduler) {
		this.scheduler = scheduler;
	}
}
