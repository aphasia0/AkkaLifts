package eantgio;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import eantgio.lift_plant.LiftPlant;
import eantgio.reservation.Booking;
import eantgio.reservation.Reservation;

public class ElevatorSystem extends UntypedActor{
	
	public static class Start{};
	public static Start START = new Start();
	
	public static void main(String[] args) {
		start();
	}

	public static void start() {
		ActorSystem system = ActorSystem.create("actor-demo-elevator");
		
	
		system.actorOf(Props.create(Booking.class), "ElevatorSystem").tell(START, null);
			
		
		try {
			Thread.sleep(150000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		system.terminate();
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		
		if(msg instanceof Start)
		{
			ActorRef liftPlant = getContext().actorOf(Props.create(LiftPlant.class));
			getContext().actorOf(Reservation.create(liftPlant));
		}
	}
}
