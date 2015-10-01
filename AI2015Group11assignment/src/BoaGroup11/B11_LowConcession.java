package BoaGroup11;

import java.util.HashMap;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.*;
import negotiator.boaframework.opponentmodel.NoModel;

/**
 * This is an abstract class used to implement a TimeDependentAgent Strategy
 * adapted from [1] [1] S. Shaheen Fatima Michael Wooldridge Nicholas R.
 * Jennings Optimal Negotiation Strategies for Agents with Incomplete
 * Information http://eprints.ecs.soton.ac.uk/6151/1/atal01.pdf
 * 
 * The default strategy was extended to enable the usage of opponent models.
 * 
 * Note that this agent is not fully equivalent to the theoretical model,
 * loading the domain may take some time, which may lead to the agent skipping
 * the first bid. A better implementation is GeniusTimeDependent_Offering.
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 */
public class B11_LowConcession extends OfferingStrategy {

	SortedOutcomeSpace outcomespace;

	/**
	 * Empty constructor used for reflexion. Note this constructor assumes that
	 * init is called next.
	 */
	public B11_LowConcession() {
	}

	public B11_LowConcession(NegotiationSession negoSession,
			OpponentModel model, OMStrategy oms) {
		this.negotiationSession = negoSession;
		outcomespace = new SortedOutcomeSpace(
				negotiationSession.getUtilitySpace());
		negotiationSession.setOutcomeSpace(outcomespace);
		this.opponentModel = model;
		this.omStrategy = oms;
	}

	/**
	 * Method which initializes the agent by setting all parameters.
	 */
	public void init(NegotiationSession negoSession, OpponentModel model,
		OMStrategy oms, HashMap<String, Double> parameters)
		throws Exception {
			this.negotiationSession = negoSession;

			outcomespace = new SortedOutcomeSpace(
					negotiationSession.getUtilitySpace());
			negotiationSession.setOutcomeSpace(outcomespace);

			this.opponentModel = model;
			this.omStrategy = oms;

	}

	@Override
	public BidDetails determineOpeningBid() {
		return determineNextBid();
	}

	/**
	 * Simple offering strategy which retrieves the target utility and looks for
	 * the nearest bid if no opponent model is specified. If an opponent model
	 * is specified, then the agent return a bid according to the opponent model
	 * strategy.
	 */
	@Override
	public BidDetails determineNextBid() {
		double time = negotiationSession.getTime();
		double utilityGoal;
		utilityGoal = 1-Math.pow(time,3);
		if(utilityGoal < 0.7){ utilityGoal = 0.7;}

		// if there is no opponent model available
		if (opponentModel instanceof NoModel) {
			nextBid = negotiationSession.getOutcomeSpace().getBidNearUtility(
					utilityGoal);
		} else {
			nextBid = omStrategy.getBid(outcomespace, utilityGoal);
		}
		return nextBid;
	}

	public NegotiationSession getNegotiationSession() {
		return negotiationSession;
	}
	
	public String getName(){
		return "Bidding Strategy Agent 11 - Low Concession";
	}
	

}