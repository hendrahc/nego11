package BoaGroup11;



import java.util.HashMap;
import java.util.List;
import java.util.Random;

import java.util.Map.Entry;

import negotiator.*;
import negotiator.BidHistory;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.actions.Reject;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.Actions;
import negotiator.boaframework.OutcomeSpace;
import negotiator.boaframework.opponentmodel.NoModel;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Objective;
import negotiator.issue.ValueDiscrete;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.qualitymeasures.UtilspaceTools;
import negotiator.session.TimeLineInfo;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.utility.UtilitySpace;

/**
 * This is your negotiation party.
 */
public class Agent11_Multi extends AbstractNegotiationParty {

	private UtilitySpace opponentUtilitySpace = new UtilitySpace(); 
	private OutcomeSpace outcomespace;
	private BidHistory bidHistory = new BidHistory();
	
	private int amountOfIssues;
	private double learningRate = 0.2;
	private int learnValueAddition = 1;
	
	public void init ( UtilitySpace utilSpace , Deadline deadline ,
			TimeLineInfo timeline , long randomSeed , AgentID agentID){
		this.utilitySpace = utilSpace;
		this.deadlines = deadline;
		this.timeline = timeline;
		this.rand = new Random(randomSeed);
		
		// initialize the opponent's utility weights
		opponentUtilitySpace = utilSpace;
		amountOfIssues = opponentUtilitySpace.getDomain().getIssues().size();
		for (Entry<Objective, Evaluator> e : opponentUtilitySpace
				.getEvaluators()) {
			// set the issue weights
			opponentUtilitySpace.unlock(e.getKey());
			e.getValue().setWeight(1D / (double) amountOfIssues);
			try {
				// set all value weights to one (they are normalized when
				// calculating the utility)
				for (ValueDiscrete vd : ((IssueDiscrete) e.getKey())
						.getValues())
					((EvaluatorDiscrete) e.getValue()).setEvaluation(vd, 1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		//initialize the outcome space
		
		
	}
	
	
	/**
	 * Each round this method gets called and ask you to accept or offer. The
	 * first party in the first round is a bit different, it can only propose an
	 * offer.
	 *
	 * @param validActions
	 *            Either a list containing both accept and offer or only offer.
	 * @return The chosen action.
	 */
	@Override
	public Action chooseAction(List<Class<? extends Action>> validActions) {

		BidDetails nextBid = determineNextBid();
		
		//double nextMyBidUtil = nextBid.getMyUndiscountedUtil();
		//double lastOpponentBidUtil = bidHistory.getLastBidDetails().getMyUndiscountedUtil();

		return new Offer(nextBid.getBid());
		
//		if (lastOpponentBidUtil >= nextMyBidUtil) {
//			return new Accept();
//		}
//		return new Reject();
		
	}

	/**
	 * All offers proposed by the other parties will be received as a message.
	 * You can use this information to your advantage, for example to predict
	 * their utility.
	 *
	 * @param sender
	 *            The party that did the action.
	 * @param action
	 *            The action that party did.
	 */
	@Override
	public void receiveMessage(Object sender, Action action) {
		super.receiveMessage(sender, action);
//		Bid currentOpBid = action.getBidFromAction(action);
//		bidHistory.add(new BidDetails(currentOpBid,1));
//		updateModel(currentOpBid, getTimeLine().getTime());
		
	}
	
	public void updateModel(Bid opponentBid, double time) {
		if (bidHistory.size() < 2) {
			return;
		}
		int numberOfUnchanged = 0;
		BidDetails oppBid = bidHistory.getLastBidDetails();
		BidDetails prevOppBid = bidHistory
				.getHistory()
				.get(bidHistory.size() - 2);
		HashMap<Integer, Integer> lastDiffSet = determineDifference(prevOppBid,
				oppBid);

		// count the number of changes in value
		for (Integer i : lastDiffSet.keySet()) {
			if (lastDiffSet.get(i) == 0)
				numberOfUnchanged++;
		}

		// This is the value to be added to weights of unchanged issues before
		// normalization.
		// Also the value that is taken as the minimum possible weight,
		// (therefore defining the maximum possible also).
		double goldenValue = learningRate / (double) amountOfIssues;
		// The total sum of weights before normalization.
		double totalSum = 1D + goldenValue * (double) numberOfUnchanged;
		// The maximum possible weight
		double maximumWeight = 1D - ((double) amountOfIssues) * goldenValue
				/ totalSum;

		// re-weighing issues while making sure that the sum remains 1
		for (Integer i : lastDiffSet.keySet()) {
			if (lastDiffSet.get(i) == 0
					&& opponentUtilitySpace.getWeight(i) < maximumWeight)
				opponentUtilitySpace.setWeight(opponentUtilitySpace.getDomain()
						.getObjective(i),
						(opponentUtilitySpace.getWeight(i) + goldenValue)
								/ totalSum);
			else
				opponentUtilitySpace.setWeight(opponentUtilitySpace.getDomain()
						.getObjective(i), opponentUtilitySpace.getWeight(i)
						/ totalSum);
		}

		// Then for each issue value that has been offered last time, a constant
		// value is added to its corresponding ValueDiscrete.
		try {
			for (Entry<Objective, Evaluator> e : opponentUtilitySpace
					.getEvaluators()) {
				// cast issue to discrete and retrieve value. Next, add constant
				// learnValueAddition to the current preference of the value to
				// make
				// it more important
				((EvaluatorDiscrete) e.getValue())
						.setEvaluation(
								oppBid.getBid().getValue(
										((IssueDiscrete) e.getKey())
												.getNumber()),
								(learnValueAddition + ((EvaluatorDiscrete) e
										.getValue())
										.getEvaluationNotNormalized(((ValueDiscrete) oppBid
												.getBid().getValue(
														((IssueDiscrete) e
																.getKey())
																.getNumber())))));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private HashMap<Integer, Integer> determineDifference(BidDetails first,
			BidDetails second) {

		HashMap<Integer, Integer> diff = new HashMap<Integer, Integer>();
		try {
			for (Issue i : opponentUtilitySpace.getDomain().getIssues()) {
				diff.put(i.getNumber(), (((ValueDiscrete) first.getBid()
						.getValue(i.getNumber())).equals((ValueDiscrete) second
						.getBid().getValue(i.getNumber()))) ? 0 : 1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return diff;
	}
	
	public BidDetails determineNextBid() {
		double time = getTimeLine().getTime();
		double utilityGoal;
		utilityGoal = 1-Math.pow(time,3);
		if(utilityGoal < 0.7){ utilityGoal = 0.7;}
		BidDetails bestBid;
		try {
			bestBid = new BidDetails(utilitySpace.getMaxUtilityBid(),getTimeLine().getTime());
			return bestBid;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
				outcomespace.getBidNearUtility(utilityGoal);
		return null;
	}
	
    
    @Override
    public String getDescription() {
    	return "Agent11 - Multilateral";
    }

}