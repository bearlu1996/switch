package org.processmining.plugins.InductiveMiner.mining.cuts.IM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMinerState;
import org.processmining.plugins.InductiveMiner.dfgOnly.dfgCutFinder.DfgCutFinder;
import org.processmining.plugins.InductiveMiner.graphs.ConnectedComponents2;
import org.processmining.plugins.InductiveMiner.graphs.Graph;
import org.processmining.plugins.InductiveMiner.graphs.GraphImplLinearEdge;
import org.processmining.plugins.InductiveMiner.mining.IMLogInfo;
import org.processmining.plugins.InductiveMiner.mining.Miner;
import org.processmining.plugins.InductiveMiner.mining.MinerState;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.InductiveMiner.mining.cuts.CutFinder;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLog;
import org.processmining.plugins.InductiveMiner.mining.logs.IMTrace;

import javafx.util.Pair;

public class CutFinderIMExclusiveChoice implements CutFinder, DfgCutFinder {

	private static ArrayList<Pair<XEventClass, XEventClass>> invisibleTransitions; 
	private static ArrayList<Long> invisibleWeight; 
	private static IMLog log;
	private static IMLogInfo info;
	//private static HashMap<XEventClass, XEventClass> L2Ls;
	//private static HashMap<XEventClass, XEventClass> NonL2Ls;
	private static HashMap<String, HashSet<String>> aba;
	
	private boolean isShortLoop(XEventClass a, XEventClass b) {
		//return true;
		if(aba.get(a.getId()) != null && aba.get(a.getId()).contains(b.getId())) {
			if(aba.get(b.getId()) != null && aba.get(b.getId()).contains(a.getId())) {
				//System.out.println(a.getId());
				//System.out.println(b.getId());
				return true;
				
			}
		}
		return false;
	}
	//test if there is "aba" and "bab"
	private void findShortLoop(IMLog log) {
		if(log == null) {
			return;
		}
		Iterator<IMTrace> Logit = log.iterator();
		while(Logit.hasNext()) {
			IMTrace imtrace = Logit.next();
			XEventClassifier classifier = log.getClassifier();
			//Iterator<XEvent> traceIt = imtrace.iterator();
			for(int c = 1;c < imtrace.size() - 1;c ++) {
				String current = classifier.getClassIdentity(imtrace.get(c));
				String before = classifier.getClassIdentity(imtrace.get(c - 1));
				
			
				//if(c > 1) {
				String after = classifier.getClassIdentity(imtrace.get(c + 1));
				if(before.equals(after)) {
					if(aba.get(before) == null) {
						aba.put(before, new HashSet<>());
					}
					this.aba.get(before).add(current);
				}
				//}
					
				//if(c < imtrace.size() - 1) {
				//	String after_after = classifier.getClassIdentity(imtrace.get(c + 1));
					
				//}
				
			
			}
		}
		/*if(L2Ls.get(a) != null&&L2Ls.get(a) == b) {
			return true;
		}
		if(NonL2Ls.get(a) != null&&NonL2Ls.get(a) == b) {
			return false;
		}
		System.out.println("Start");
		if(a.getIndex() == 100 || b.getIndex() == 100) {
			return false;
		}
		boolean aba = false;
		boolean bab = false;
		Iterator<IMTrace> Logit = log.iterator();
		while(Logit.hasNext()) {
			IMTrace imtrace = Logit.next();
			XEventClassifier classifier = log.getClassifier();
			//Iterator<XEvent> traceIt = imtrace.iterator();
			for(int c = 1;c < imtrace.size();c ++) {
				String before = classifier.getClassIdentity(imtrace.get(c - 1));
				String after = classifier.getClassIdentity(imtrace.get(c));
				
				if(before.equals(a.getId()) && after.equals(b.getId())) {
					if(c > 1) {
						String before_before = classifier.getClassIdentity(imtrace.get(c - 2));
						if(before_before.equals(b.getId())) {
							bab = true;
						}
						
					}
					
					if(c < imtrace.size() - 1) {
						String after_after = classifier.getClassIdentity(imtrace.get(c + 1));
						if(after_after.equals(a.getId())) {
							aba = true;
						}
					}
				}
				
				if(aba&&bab) {
					break;
				}
			}
			
			
		}
		//System.out.println(a.getId() + ", " + b.getId() + ": " + aba + " " + bab);
		System.out.println("End");
		if(aba&&bab) {
			L2Ls.put(a, b);
			L2Ls.put(b, a);
		}
		else {
			NonL2Ls.put(a, b);
			NonL2Ls.put(b, a);
		}
		return (aba&&bab);*/
		//return false;
	}
	
	public Cut findCut(final IMLog log, final IMLogInfo logInfo, final MinerState minerState) {
		
		/*for(XEventClass current_event:info.getDfg().getStartActivities()) {
			System.out.println(current_event.getId());
		}
		System.out.println(info.getDfg().getNumberOfStartActivities());*/
			
		if(Miner.mineSwitch == false) {
			Graph<XEventClass> currentDfg = logInfo.getDfg().getDirectlyFollowsGraph();
			return findCut(currentDfg);
		}
		
		this.log = log;
		aba = new HashMap<>();
		findShortLoop(log);
		info = logInfo;
		//fake start and end events
		XEventClass artiStart = new XEventClass("artiStart", 100);
		XEventClass artiEnd = new XEventClass("artiEnd", 100);
		ArrayList<XEventClass> artiStarts = new ArrayList<>();
		ArrayList<XEventClass> artiEnds = new ArrayList<>();
		//remove invisible directly-follow relations
		invisibleTransitions = new ArrayList<>();
		invisibleWeight = new ArrayList<>();
		//ArrayList<Long> deletedEdges = new ArrayList<>();
		Graph<XEventClass> currentDfg = logInfo.getDfg().getDirectlyFollowsGraph().clone();
		Graph<XEventClass> currentDfgClone = logInfo.getDfg().getDirectlyFollowsGraph().clone();
		//currentDfg.addVertex(artiStart);
		//currentDfg.addVertex(artiEnd);
		//add fake edges
		int random = -1;
		for(XEventClass current_event:info.getDfg().getStartActivities()) {
			//System.out.println(current_event.getId());
			XEventClass newclass = new XEventClass("" + random, 100);
			currentDfg.addEdge(artiStart, newclass, 100);
			currentDfg.addEdge(newclass, current_event, 100);
			artiStarts.add(newclass);
			random --;
		}
		
		for(XEventClass current_event:info.getDfg().getEndActivities()) {
			//System.out.println(current_event.getId());
			XEventClass newclass = new XEventClass("" + random, 100);		
			currentDfg.addEdge(current_event, newclass, 100);
			currentDfg.addEdge(newclass, artiEnd, 100);
			artiEnds.add(newclass);
			random --;
		}
		
		
		for(XEventClass a:currentDfg.getVertices()) {			
			//System.out.println(a.getIndex());
			for(long targetIndex:currentDfg.getOutgoingEdgesOf(a)) {
				
				//System.out.println(a.getId());
				
				//a -> b
				XEventClass b = currentDfg.getEdgeTarget(targetIndex);
				if(currentDfg.containsEdge(b, a) && isShortLoop(a, b) == false) {
					//System.out.println(a.getId() + ", " + b.getId());
					continue;
				}
				//System.out.println(b.getId());
				ArrayList<XEventClass> allX = new ArrayList<>();
				
				for(long target:currentDfg.getOutgoingEdgesOf(a)) {
					if(currentDfg.containsEdge(currentDfg.getEdgeTarget(target), a) == false||isShortLoop(currentDfg.getEdgeTarget(target), a) == true) {
						allX.add(currentDfg.getEdgeTarget(target));
					}					
				}
				
				/*if(a.getId().equals("E") && b.getId().equals("D")) {
					for(XEventClass lala:allX) {
						System.out.println("x: " + lala.getId());
					}
				}*/
				
				ArrayList<XEventClass> allY = new ArrayList<>();
				
				for(long source:currentDfg.getIncomingEdgesOf(b)) {
					if(currentDfg.containsEdge(b, currentDfg.getEdgeSource(source)) == false||isShortLoop(b, currentDfg.getEdgeSource(source)) == true) {
						allY.add(currentDfg.getEdgeSource(source));
					}
				}
				
				/*if(a.getId().equals("E") && b.getId().equals("D")) {
					for(XEventClass lala:allY) {
						System.out.println("y " + lala.getId());
					}
				}*/
				
				boolean finished = false;
				for(XEventClass x:allX) {
					if(finished == true) {
						break;
					}
					for(XEventClass y:allY) {
						if(!currentDfg.containsEdge(y, x)) {
							/*if(a.getId().equals("E") && b.getId().equals("D")) {
								System.out.println(y.toString());
								System.out.println(x.toString());
							}*/
							if(!(currentDfg.containsEdge(x, b)&&currentDfg.containsEdge(b, x)&&isShortLoop(x, b) == false)) {
								/*if(a.getId().equals("E") && b.getId().equals("D")) {
									System.out.println(y.toString());
									System.out.println(x.toString());
								}*/
								if(!(currentDfg.containsEdge(a, y)&&currentDfg.containsEdge(y, a)&&isShortLoop(a, y) == false)) {
									/*if(a.getId().equals("E") && b.getId().equals("D")) {
										System.out.println(y.toString());
										System.out.println(x.toString());
									}*/
									if(a.getIndex() != 100 && b.getIndex() != 100) {
										invisibleTransitions.add(new Pair<XEventClass, XEventClass>(a, b));
									    invisibleWeight.add(currentDfg.getEdgeWeight(a, b));
									}
									
								    //System.out.println(a);
								    //System.out.println(b);
								    //System.out.println(a.getId());
								    //System.out.println(b.getId());
								    //GraphImplLinearEdge<XEventClass> currentDfg_cast = (GraphImplLinearEdge<XEventClass>) currentDfg;
								    //currentDfg_cast.removeEdge(targetIndex);
								    //deletedEdges.add(targetIndex);
								    finished = true;
								    break;
								}
							}
						}
												
					}
				}
			}
			
			
					
				
		}
		currentDfg = currentDfgClone;
		GraphImplLinearEdge<XEventClass> currentDfg_cast = (GraphImplLinearEdge<XEventClass>) currentDfg;
		
		/*for(long t:currentDfg.getEdgesOf(artiStart)) {
			currentDfg_cast.removeEdge(t);
		}
		for(long t:currentDfg.getEdgesOf(artiEnd)) {
			currentDfg_cast.removeEdge(t);
		}*/
		
		
		for(Pair<XEventClass, XEventClass> pair:invisibleTransitions) {
			if(currentDfg.containsEdge(pair.getKey(), pair.getValue())) {
				//System.out.println(pair.getKey().getId());
				//System.out.println(pair.getValue().getId());
				//if(this.info != null) {
				//	if(info.getDfg().isStartActivity(pair.getValue()) || info.getDfg().isEndActivity(pair.getKey())) {
				//		continue;
				//	}
				//	for(XEventClass current_event:info.getDfg().getStartActivities()) {
				//		System.out.println(current_event.getId());
				//	}
				//	System.out.println(info.getDfg().getNumberOfStartActivities());
				//}
				for(long t:currentDfg.getOutgoingEdgesOf(pair.getKey())) {
					
					/*int count1 = 0;
					int count2 = 0;
					
					for(long i1:currentDfg.getEdgesOf(pair.getKey())) {
						count1 ++;
					}
					
					for(long i2:currentDfg.getEdgesOf(pair.getValue())) {
						count2++;
					}*/
					
					if(currentDfg.getEdgeTarget(t) == pair.getValue()) {
						//System.out.println(pair.getKey().getId());
						//System.out.println(pair.getValue().getId());
						
						currentDfg_cast.removeEdge(t);
						break;
					}
				}
			}
		}
		/*for(XEventClass a:currentDfg.getVertices()) {
			for(long targetIndex:currentDfg.getOutgoingEdgesOf(a)) {
				System.out.println("lalalllalalla");
				GraphImplLinearEdge<XEventClass> currentDfg_cast = (GraphImplLinearEdge<XEventClass>) currentDfg;
				currentDfg_cast.removeEdge(targetIndex);
				XEventClass b = currentDfg.getEdgeTarget(targetIndex);
				//currentDfg.addEdge(a, b, 0);
				System.out.println(currentDfg.getEdgeWeight(a, b));
			}
		}*/
		
		//return findCut(logInfo.getDfg().getDirectlyFollowsGraph());
		return findCut(currentDfg);
	}

	public Cut findCut(final Dfg dfg, final DfgMinerState minerState) {
		invisibleTransitions = new ArrayList<>();
		invisibleWeight = new ArrayList<>();
		return findCut(dfg.getDirectlyFollowsGraph());
	}

	public static Cut findCut(final Graph<XEventClass> graph) {
		if(Miner.mineSwitch == false) {
			System.out.println("XOR false");
			//compute the connected components of the directly follows graph
			Collection<Set<XEventClass>> connectedComponents = ConnectedComponents2.compute(graph);
			GraphImplLinearEdge<XEventClass> currentDfg_cast = (GraphImplLinearEdge<XEventClass>) graph;
			//Miner.mineSwitch = true;
			return new Cut(Operator.xor, connectedComponents);
		}
		XEventClassifier classifier = null;
		if(log != null) {
			classifier = log.getClassifier();
		}
		
		//compute the connected components of the directly follows graph
		Collection<Set<XEventClass>> connectedComponents = ConnectedComponents2.compute(graph);
		GraphImplLinearEdge<XEventClass> currentDfg_cast = (GraphImplLinearEdge<XEventClass>) graph;
		int i = 0;
		for(int a = 0;a < invisibleTransitions.size();a ++) {
			//System.out.println(invisibleTransitions.get(a).getKey() + ", " + invisibleTransitions.get(a).getValue());
			Pair<XEventClass, XEventClass> pair = invisibleTransitions.get(a);
			if(!graph.containsEdge(pair.getKey(), pair.getValue())) {
				//graph.addEdge(pair.getKey(), pair.getValue(), invisibleWeight.get(i));
			}
			
			for(Set<XEventClass> b:connectedComponents) {
				if(b.contains(pair.getKey()) && b.contains(pair.getValue())) {
					invisibleTransitions.remove(a);
					//System.out.println(pair.getKey().getId());
					//System.out.println(pair.getValue().getId());
					a --;
					break;
				}
			}
			
			i ++;
		}
		for(int a = 0;a < invisibleTransitions.size();a ++) {
			Miner.switchTransitions.add(invisibleTransitions.get(a));
			System.out.println(invisibleTransitions.get(a).getKey() + ", " + invisibleTransitions.get(a).getValue());
			//System.out.println(log.size());
			
			//remove traces with switch behaviours
			if(log != null) {
				Iterator<IMTrace> Logit = log.iterator();
				while(Logit.hasNext()) {
					IMTrace imtrace = Logit.next();
					
					//Iterator<XEvent> traceIt = imtrace.iterator();
					for(int c = 1;c < imtrace.size();c ++) {
						String before = classifier.getClassIdentity(imtrace.get(c - 1));
						String after = classifier.getClassIdentity(imtrace.get(c));
						if(before.equals(invisibleTransitions.get(a).getKey().getId())
								&&after.equals(invisibleTransitions.get(a).getValue().getId())) {
							//Sitch on/off trace delete option
							//Logit.remove();
							break;
						}
					}
					//trace.
					
				}
			}
			//System.out.println(log.size());
		}
		System.out.println("XOR");
		System.out.println("------------------------");
		return new Cut(Operator.xor, connectedComponents);
	}

}
