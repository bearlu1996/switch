package org.processmining.plugins.InductiveMiner.plugins;

import java.util.ArrayList;
import java.util.Iterator;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.InductiveMiner.mining.Miner;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIM;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLog;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogImpl;
import org.processmining.processtree.Node;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.impl.AbstractBlock;

import javafx.util.Pair;

public class IMProcessTree {

	public ProcessTree mineProcessTree(PluginContext context, XLog xlog) {
		return mineProcessTree(xlog);
	}

	public static ProcessTree mineProcessTree(XLog xlog) {
		return mineProcessTree(xlog, new MiningParametersIM());
	}

	public static ProcessTree mineProcessTree(XLog xlog, MiningParameters parameters) {
		//prepare the log
		IMLog log = new IMLogImpl(xlog, parameters.getClassifier(), parameters.getLifeCycleClassifier());
		return mineProcessTree(log, parameters);
	}

	public static ProcessTree mineProcessTree(XLog xlog, MiningParameters parameters, Canceller canceller) {
		//System.out.println("start");
		//prepare the log
		IMLog log = new IMLogImpl(xlog, parameters.getClassifier(), parameters.getLifeCycleClassifier());
		ProcessTree result = mineProcessTree(log, parameters, canceller);
		/*//make sure the returned model is sound
		for(Pair<XEventClass, XEventClass> i:Miner.switchTransitions) {
			System.out.println(i.getKey() + ", " + i.getValue());
		}
		for(Node i:result.getNodes()) {
		   
		    	System.out.println(i.getIncomingEdges().size());
		    
		}*/
		System.out.println("Switch behaviours before model repair:");
		for(Pair<XEventClass, XEventClass> i:Miner.switchTransitions) {
			System.out.println(i.getKey() + ", " + i.getValue());
		}
		
		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		System.out.println("Switch behaviours after model repair:");
		refineSwitchTransitions(result);
		for(Pair<XEventClass, XEventClass> i:Miner.switchTransitions) {
			System.out.println(i.getKey() + ", " + i.getValue());
		}
		Miner.switchTransitions = new ArrayList<>();
		return result;
	}

	public static ProcessTree mineProcessTree(IMLog log, MiningParameters parameters) {
		return Miner.mine(log, parameters, new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		});
	}

	public static ProcessTree mineProcessTree(IMLog log, MiningParameters parameters, Canceller canceller) {
		return Miner.mine(log, parameters, canceller);
	}
	
	//make sure all the switch behaviours are sound
    public static void refineSwitchTransitions(ProcessTree tree) {
    	Iterator<Pair<XEventClass, XEventClass>> pairIt = Miner.switchTransitions.iterator();
    	while(pairIt.hasNext()) {
    		Pair<XEventClass, XEventClass> pair = pairIt.next();
    		String source = pair.getKey().getId();
    		String target = pair.getValue().getId();
    		Node s = null;
    		Node t = null;
    		for(Node i:tree.getNodes()) {
        		if(i.getName().equals(source)) {
        			s = i;
        		}
        		
        		if(i.getName().equals(target)) {
        			t = i;
        		}
        	}
    		
    		if(s == null || t == null) {
    			pairIt.remove();
    			continue;
    		}
    		
    		//find the nearest and node
    		Node current = null;
    		Node previous = null;
    		Node and1 = null;
    		Node and1Child = null;
    		Node and2 = null;
    		Node and2Child = null;
    		current = s;
    		while(current.getIncomingEdges().size() != 0) {
    			previous = current;
    			current = current.getIncomingEdges().get(0).getSource();
    			if(current instanceof AbstractBlock.And) {
    				and1 = current;
    				and1Child = previous;
    				break;
    			}
    		}
    		current = t;
    		while(current.getIncomingEdges().size() != 0) {
    			current = current.getIncomingEdges().get(0).getSource();
    			if(current instanceof AbstractBlock.And) {
    				and2 = current;
    				and2Child = previous;
    				break;
    			}
    		}
    		
    		if(and1 != null && and2 != null && and1 != and2) {
    			pairIt.remove();
    			continue;
    		}
    		
    		if(and1 != null && and2 != null && and1 == and2) {
    			if(and1Child != and2Child) {
    				pairIt.remove();
        			continue;
    			}
    		}
    		
    		if((and1 == null && and2 != null) || (and1 != null && and2 == null)) {
    			pairIt.remove();
    			continue;
    		}
    		
    		/*if(s.getIncomingEdges().size() != 0) {
    			Block sParent = s.getIncomingEdges().get(0).getSource();
    			if(sParent instanceof AbstractBlock.Seq) {
    				for(int i = 0;i < sParent.getChildren().size() - 1;i ++) {
    					if(sParent.getChildren().get(i).getName().equals(source)) {
    						if(sParent.getChildren().get(i + 1) instanceof AbstractBlock.And) {
    							pairIt.remove();
    		        			continue;
    						}
    					}
    				}
    			}
    		}
    		
    		if(t.getIncomingEdges().size() != 0) {
    			Block tParent = t.getIncomingEdges().get(0).getSource();
    			if(tParent instanceof AbstractBlock.Seq) {
    				for(int i = 1;i < tParent.getChildren().size();i ++) {
    					if(tParent.getChildren().get(i).getName().equals(target)) {
    						if(tParent.getChildren().get(i - 1) instanceof AbstractBlock.And) {
    							pairIt.remove();
    		        			continue;
    						}
    					}
    				}
    			}
    		}*/
    		
    		
    	}
    	
    }
}
