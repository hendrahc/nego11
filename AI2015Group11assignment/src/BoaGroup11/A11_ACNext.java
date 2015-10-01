package BoaGroup11;

import java.util.HashMap;
import negotiator.boaframework.*;

/**
 * This Acceptance Condition will accept an opponent bid if the utility is
 * higher than the bid the agent is ready to present
 * 
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * T. Baarslag, K. Hindriks, M. Hendrikx, A. Dirkzwager, C.M. Jonker
 * 
 * @author Alex Dirkzwager, Mark Hendrikx
 * @version 18/12/11
 */
public class A11_ACNext extends AcceptanceStrategy {

	public A11_ACNext(NegotiationSession negoSession, OfferingStrategy strat) {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
	}

	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat,
			OpponentModel opponentModel, HashMap<String, Double> parameters)
			throws Exception {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
	}

	@Override
	public Actions determineAcceptability() {
		double nextMyBidUtil = offeringStrategy.getNextBid()
				.getMyUndiscountedUtil();
		double lastOpponentBidUtil = negotiationSession.getOpponentBidHistory()
				.getLastBidDetails().getMyUndiscountedUtil();

		if (lastOpponentBidUtil >= nextMyBidUtil) {
			return Actions.Accept;
		}
		return Actions.Reject;
	}
	
	public String getName(){
		return "Acceptance Strategy Agent 11 - AC Next";
	}
}