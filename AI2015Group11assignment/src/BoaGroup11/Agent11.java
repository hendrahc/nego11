package BoaGroup11;

import negotiator.boaframework.agent.*;
import negotiator.boaframework.*;

/************************************************/
/* Assignment AI Technique - Negotiation Agent	*/
/*												*/
/************************************************/

public class Agent11 extends BOAagent {
	@Override
	public void agentSetup () {
		OpponentModel om = new O11_FreqAnalysis ( negotiationSession);
		OMStrategy oms = new OMS11_BestBid ( negotiationSession, om);
		OfferingStrategy offering = new B11_LowConcession (negotiationSession ,
		om , oms);
		AcceptanceStrategy ac = new A11_ACNext ( negotiationSession , offering);
		setDecoupledComponents (ac , offering , om , oms );
	}
	@Override
	public String getName () {
		return " Agent11 ";
	}
}
