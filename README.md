# A Novel Approach to Discover Switch Behaviours in Process Mining

Code, data and evaluation results for paper:

**Lu Y., Chen Q., Poon S. (2021) A Novel Approach to Discover Switch Behaviours in Process Mining. In: Leemans S., Leopold H. (eds) Process Mining Workshops. ICPM 2020. Lecture Notes in Business Information Processing, vol 406. Springer, Cham. https://doi.org/10.1007/978-3-030-72693-5_5**


**The code files are adapted from the "ProcessTree" package and "InductiveMinerDeprecated" package directly in the ProM framework

How to use the code?
-----------------------------------------------------------------------
## Discovering Switch Process Trees
1. In eclipse, import the "InductiveMinerDeprecated" package from ProM
2. Replace CutFinderIMExclusiveChoice.java in org.processmining.plugins.InductiveMiner.mining.cuts.IM
3. Replace IMProcessTree.java in org.processmining.plugins.InductiveMiner.plugins
4. Replace Miner.java in org.processmining.plugins.InductiveMiner.mining
5. Replace MiningParameters.java in org.processmining.plugins.InductiveMiner.mining
6. Replace CutFinderIM.java in org.processmining.plugins.InductiveMiner.mining.cuts.IM
7. Run ProM and use "Mine Process Tree with Inductive Miner" to get the process tree, select IM or IMf as the discovery algorithm, export the process tree as a file
8. Take a look at the console in eclipse, all the switch behaviours detected will appear under the "Switch behaviours after model repair" line
9. Copy all the switch behaviours to a text file
-----------------------------------------------------------------------

## Translating Switch Process Trees into workflow nets

1. In eclipse, import the "ProcessTree" package from ProM
2. Replace ProcessTree2Petrinet.java in org.processmining.processtree.conversion
3. Run ProM, import the process tree file we get when discovering switch process trees
4. Run the "Convert Process Tree to Petri Net 2"
5. In the console of eclipse, copy the switch behaviours we get in the last part into the console line by line<br/>
Example format:<br/>
Activity1, Activity2<br/>
Activity3, Activity4<br/>
......<br/>
6. When you finish, type "finish" in the console and press enter
7. You will see the petri-net in ProM

-----------------------------------------------------------------------

## Turning on/off the "delete trace" option

1. Trun on: comment the line 404 in CutFinderIMExclusiveChoice.java
2. Trun off: Enable the code in line 404 in CutFinderIMExclusiveChoice.java


 

