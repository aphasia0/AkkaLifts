package eantgio.lift_plant.sheduler;


import java.util.SortedSet;
import java.util.TreeSet;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import eantgio.lift_plant.lift.Lift;
import eantgio.reservation.request.floor.FloorPad;

public class Scheduler extends UntypedActor{



	public static Props props(ActorRef lift) {
		return Props.create(Scheduler.class,lift);
	}



	public Scheduler(ActorRef lift) {
		this.lift = lift;
	}


	public enum Direction{
		UP, DOWN, IDLE;
	}

	public static class FloorReached{
		final int floor;

		public static FloorReached of (final int floor)
		{
			return new FloorReached(floor);
		}

		private FloorReached(final int floor) {
			this.floor = floor;
		}
	}


	Direction direction = Direction.IDLE;
	int currentPosition;
	int futurePosition;
	SortedSet<Integer> upList = new TreeSet<>();
	SortedSet<Integer> downList = new TreeSet<>();
	private ActorRef lift;
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	//
	//public static void main(String[] args) {
	//	upList.add(33);
	//	upList.add(22);
	//	System.out.println(printList(upList));
	//}


	protected void storeThisCall(int weWannaGo) {

		if(weWannaGo > futurePosition)
		{
			upList.add(weWannaGo);
		}
		else if(weWannaGo < futurePosition)
		{
			downList.add(weWannaGo);
		}

	}

	Procedure<Object> moving = new Procedure<Object>() {
		@Override
		public void apply(Object msg) {
			if(msg instanceof FloorPad.Request)
			{
				int weWannaGo = ((FloorPad.Request)msg).fromFloor;
				storeThisCall(weWannaGo);
				log.debug("Request received while moving, reservation for floor "+weWannaGo+" stored!");
			}
			else if (msg instanceof FloorReached)
			{
				currentPosition = futurePosition;
				log.debug("Floor "+currentPosition+" reached, getting next floor.");

				int weAreGoingTo = getNextMove();

				if(weAreGoingTo == -1)
				{
					context().unbecome();
					direction = Direction.IDLE;
					log.debug(" No more reservation. Waiting");
				}
				else
				{
					modifyDirection(weAreGoingTo);
					lift.tell(Lift.Move.of(weAreGoingTo), getSelf());
					log.debug(" Moving towards floor "+weAreGoingTo);
				}

			}
		}
	};

	@Override
	public void onReceive(Object msg) throws Exception { //IDLE STATE
	
		if(msg instanceof FloorPad.Request)
		{
			log.debug("aa");
			int weAreGoingTo = ((FloorPad.Request)msg).fromFloor;
			if(weAreGoingTo == currentPosition) //OPEN THE DOOR WE'RE ALREADY THERE
			{
				log.debug(getSelf().path()+ " We are already there");
			}
			else
			{
				modifyDirection(weAreGoingTo);
				log.debug("We are at floor number "+currentPosition+". Next floor is :"+weAreGoingTo);
				lift.tell(Lift.Move.of(weAreGoingTo), getSelf());
				getContext().become(moving);
			}
		}

	}


	protected int getNextMove() {

		int next;

		log.debug("UpList reservation is : "+printList(upList));
		log.debug("UpList reservation is : "+printList(downList));
		if(upList.size() == 0 && downList.size() == 0)
			next = -1; //No next move
		else if(direction == Direction.UP && !upList.isEmpty())
		{
			next = upList.first();
			upList.remove(next);
		}
		else if(direction == Direction.DOWN && !downList.isEmpty())
		{
			next = downList.last();
			downList.remove(next);

		}
		else if(!upList.isEmpty())  //Change DIRECTION
		{
			next = upList.first();
			upList.remove(next);
		}
		else { //Change DIRECTION
			next = downList.last();
			downList.remove(next);
		}
		log.debug("Decision is : "+next);
		return next;
	}



	private void modifyDirection(int weAreGoingTo) {
		if(weAreGoingTo > currentPosition)
		{
			direction = Direction.UP;
			futurePosition = weAreGoingTo;
		}
		else if (weAreGoingTo < currentPosition)
		{
			direction = Direction.DOWN;
			futurePosition = weAreGoingTo;
		}
	}

	private static String printList(SortedSet<Integer> list) {
		StringBuffer b = new StringBuffer("[ ");
		list.forEach(e -> b.append(e+",") );
		b.replace(b.length()-1, b.length(), "]");
		return b.toString();
	}



}

