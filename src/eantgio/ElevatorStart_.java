package eantgio;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class ElevatorStart_ {
	public static void main(String[] args) {
		start();
	}

	public static void start() {
		ActorSystem system = ActorSystem.create("actor-demo-elevator");
		
	
		ActorRef manager = system.actorOf(Props.create(ElevatorManager.class), "Elevator_Manager");

		
		try {
			Thread.sleep(150000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		system.terminate();
	}
}
