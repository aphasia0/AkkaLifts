package eantgio.lift_plant.lift;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import eantgio.lift_plant.sheduler.Scheduler.FloorReached;

import scala.concurrent.duration.FiniteDuration;

public class Lift extends UntypedActor{

	public static class Move{


		public Move(int where) {
			this.where = where;
		}

		final int where;
		public static Move of(int where) {
			return new Move(where);
		}


	}

	public static class Stop{}
	public static Stop STOP = new Stop();


	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	ActorRef sheduler;

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof Move)
		{
			Move move = (Move) msg;
			log.debug("Moving ----- Door Closed {}");

			FiniteDuration f = new FiniteDuration(1, TimeUnit.SECONDS);
			getContext().system().scheduler().scheduleOnce(f,
					getSelf(),
					FloorReached.of(move.where),
					getContext().system().dispatcher(),
					getSelf());		
			sheduler = getSender();
		}
		else if(msg instanceof Stop)
		{
			log.debug("Stopped ----- Door opening {***|***}");

		}
		else if (msg instanceof FloorReached)
		{
			log.debug("Stopped ----- Door opening {***|***}");
			sheduler.tell(msg, getSelf());
			sheduler = null;

		}
	}


}

