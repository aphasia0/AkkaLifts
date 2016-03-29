package eantgio.reservation.request.floor;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Procedure;
import scala.concurrent.duration.FiniteDuration;

public class FloorPad extends UntypedActor{

	FiniteDuration f = FiniteDuration.create((int)(Math.random()*40), TimeUnit.SECONDS);
	{
		getContext().system().scheduler().schedule(FiniteDuration.Zero(),f, getSelf(), CALL,
				getContext().system().dispatcher(),
				getSelf());
	}

	public static class Call{}
	public static Call CALL = new Call();

	public static class Request{
		public final int fromFloor;

		private static Request of (final int floor)
		{
			return new Request(floor);
		}

		private Request(final int floor) {
			this.fromFloor = floor;
		}
	}

	public static Props props(ActorRef elevatorManager, int myFloor) {
		return Props.create(FloorPad.class, elevatorManager, myFloor);		
	}

	final int floor;
	ActorRef elevatorManager;

	public FloorPad(ActorRef elevatorManager, int floor) {
		this.floor = floor;
		this.elevatorManager = elevatorManager;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof Call)
		{
			System.out.println(getSelf().path()+" Elevator has been called from floor "+floor);
			elevatorManager.tell(Request.of(floor), getSelf());
			getContext().become(called);
		}

	}

	Procedure<Object> called = new Procedure<Object>() {
		@Override
		public void apply(Object msg) {
			if (msg instanceof Call)
			{
				System.out.println(getSelf().path()+" Already called from floor "+floor);
			}
		}
	};




}
