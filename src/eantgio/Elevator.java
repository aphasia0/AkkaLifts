package eantgio;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import akka.actor.UntypedActor;
import akka.japi.Procedure;
import eantgio.FloorPad.Call;
import eantgio.FloorPad.Request;
import scala.concurrent.duration.FiniteDuration;

public class Elevator extends UntypedActor{

	public enum Direction{
		UP, DOWN, IDLE;
	}

	public static class FloorReached{
		final int floor;

		private static FloorReached of (final int floor)
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



	@Override
	public void preStart() throws Exception {
		super.preStart();

	}

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
				System.out.println(getSelf().path()+ "Request received while moving");
			}
			else if (msg instanceof FloorReached)
			{
				currentPosition = futurePosition;
				System.out.println(getSelf().path()+ "Floor "+currentPosition+" reached, Opening the door [[----]]");
				int weAreGoingTo = getNextMove();
				
				if(weAreGoingTo == -1)
				{
					context().unbecome();
					direction = Direction.IDLE;
					System.out.println(getSelf().path()+ " No more reservation, Closing the door [[]]");
				}
				else
				{
					modifyDirection(weAreGoingTo);
					goToFloor(weAreGoingTo);
					System.out.println(getSelf().path()+ " Moving towards floor "+weAreGoingTo+", Closing the door [[]]");
					getContext().become(moving);
				}
					
			}
		}
	};

	@Override
	public void onReceive(Object msg) throws Exception { //IDLE STATE

		if(msg instanceof FloorPad.Request)
		{
			int weAreGoingTo = ((FloorPad.Request)msg).fromFloor;
			if(weAreGoingTo == currentPosition) //OPEN THE DOOR WE'RE ALREADY THERE
			{
				System.out.println(getSelf().path()+ " We are already there, Opening the door [[----]]");
			}
			else
			{
				modifyDirection(weAreGoingTo);
				goToFloor(weAreGoingTo);
				System.out.println(getSelf().path()+ " Moving towards floor "+weAreGoingTo+", Closing the door [[]]");
				getContext().become(moving);
			}
		}

	}


	protected int getNextMove() {
		if(upList.size() == 0 && downList.size() == 0)
			return -1; //No next move
		if(direction == Direction.UP && !upList.isEmpty())
		{
			return upList.first();
			
		}
		else if(direction == Direction.DOWN && !downList.isEmpty())
		{
			return downList.last();
		}
		else{ //Change THIS
			if(!upList.isEmpty())
			{
				return upList.first();
			}
			else
				return downList.last();
		}
	}

	private void modifyDirection(int weAreGoingTo) {
		if(weAreGoingTo > currentPosition)
		{
			direction = Direction.UP;
			futurePosition = currentPosition + 1;
		}
		else if (weAreGoingTo < currentPosition)
		{
			direction = Direction.DOWN;
			futurePosition = currentPosition - 1;
		}
	}

	private void goToFloor(int fromFloor) {
		FiniteDuration f = new FiniteDuration(1, TimeUnit.SECONDS);
		getContext().system().scheduler().scheduleOnce(f, getSelf(),FloorReached.of(fromFloor),
				getContext().system().dispatcher(),
				getSelf());		
	}

}

