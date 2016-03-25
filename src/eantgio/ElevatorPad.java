package eantgio;
import akka.actor.ActorRef;


public class ElevatorPad {

	
	public static ElevatorPad props(ActorRef elevatorManager) {
		return new ElevatorPad();		
	}
}
