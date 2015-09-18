package BoaGroup11;

import java.io.Serializable;
import java.util.HashMap;

import negotiator.Bid;
import negotiator.NegotiationResult;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.boaframework.agent.*;
import negotiator.boaframework.*;

public class Agent11 extends BOAagent {
	@Override
	public void agentSetup () {
		OpponentModel om = new HardHeadedFrequencyModel ( negotiationSession);
		OMStrategy oms = new BestBid ( negotiationSession, om);
		OfferingStrategy offering = new TimeDependent_Offering ( negotiationSession ,
		om , oms , 0.2 , 0, 1, 0);
		AcceptanceStrategy ac = new AC_Next ( negotiationSession , offering , 1, 0);
		setDecoupledComponents (ac , offering , om , oms );
	}
	@Override
	public String getName () {
		return " Agent11 ";
	}
}
